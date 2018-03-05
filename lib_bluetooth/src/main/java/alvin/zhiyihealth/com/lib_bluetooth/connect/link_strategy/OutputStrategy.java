package alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy;

import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import alvin.zhiyihealth.com.lib_bluetooth.data.dataWriter.DataWriter;
import alvin.zhiyihealth.com.lib_bluetooth.device.DeviceManager;
import alvin.zhiyihealth.com.lib_bluetooth.listener.InstallDataListen;
import alvin.zhiyihealth.com.lib_bluetooth.listener.WriteDataListener;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

/**
 * Created by zouyifeng on 09/02/2018.
 * 22:06
 */

public class OutputStrategy implements ConnectStrategy, DataWriter {
    private OutputMQThread currentThread;

    public OutputStrategy() {
    }

    @Override
    public int writeOutData(Object data, long delayMillis, int id) {
        if (currentThread != null) {
            Message obtain = Message.obtain();
            obtain.what = MessageType.WRITE;
            obtain.obj = data;
            obtain.arg1 = id;
            Bundle bundle = obtain.getData();
            bundle.putLong("delayMillis", delayMillis);
            obtain.setData(bundle);
            currentThread.sendMessage(obtain);
        }

        return -1;
    }

    @Override
    public void disConnect() {
        if (currentThread != null) {
            currentThread.disConnect();
            currentThread.stopMQ();
        }
        currentThread = null;
    }

    @Override
    public void connect(Executor executor, DeviceManager deviceManager) {
        if (currentThread == null) {
            currentThread = new OutputMQThread();
            executor.execute(currentThread);
        }

        currentThread.disConnect();
        currentThread.boundDeviceManager(deviceManager);
    }

    private static class OutputMQThread extends BaseConnectThread {
        private BluetoothSubmitHandler handler;

        private ArrayList<Message> messages;

        private OutputMQThread() {
            messages = new ArrayList<>();
        }

        private void sendMessage(Message o) {
            if (handler == null) {
                synchronized (this) {
                    if (handler == null) {
                        Bundle data = o.getData();
                        data.putLong("sendTime", SystemClock.uptimeMillis());
                        messages.add(o);
                    } else {
                        handler.sendMessage(o);
                    }
                }
            } else {
                handler.sendMessage(o);
            }

        }

        public void stopMQ() {
            Message obtain = Message.obtain();
            obtain.what = MessageType.STOP;
            sendMessage(obtain);
        }

        @Override
        public void disConnect() {
            Message obtain = Message.obtain();
            obtain.what = MessageType.CLOSE;
            sendMessage(obtain);
        }

        @Override
        public void boundDeviceManager(DeviceManager deviceManager) {
            Message obtain = Message.obtain();
            obtain.what = MessageType.BOUND;
            obtain.obj = deviceManager;
            sendMessage(obtain);
        }

        @Override
        public void run() {
            Looper.prepare();

            handler = new BluetoothSubmitHandler();

            onRun();
            Looper.loop();
        }

        private void onRun() {
            synchronized (this) {
                ArrayList<Message> list = (ArrayList<Message>) messages.clone();
                messages.clear();

                for (Message o : list) {
                    Bundle bound = o.getData();
                    long sendTime = bound.getLong("sendTime", 0);

                    if (sendTime < 0) sendTime = 0;

                    switch (o.what) {
                        case MessageType.WRITE:
                            Bundle data = o.getData();

                            long delayMillis = data.getLong("delayMillis", 0);
                            if (sendTime == 0) {
                                handler.sendMessageDelayed(o, delayMillis);
                            } else {
                                handler.sendMessageAtTime(o, sendTime + delayMillis);
                            }
                            break;

                        case MessageType.BOUND:
                        case MessageType.CLOSE:
                        case MessageType.STOP:
                            if (sendTime == 0) {
                                handler.sendMessage(o);
                            } else {
                                handler.sendMessageAtTime(o, sendTime);
                            }

                            break;
                    }
                }
            }
        }
    }

    private interface MessageType {
        /**
         * 绑定设备
         */
        int BOUND = 0x0000000F;

        /**
         * 写出数据
         */
        int WRITE = 0x000000FF;

        /**
         * 断开连接
         */
        int CLOSE = 0x00000FFF;

        /**
         * 停止消息队列
         */
        int STOP = 0x0000FFFF;
    }

    /**
     * BluetoothSubmitHandler 是专门将数据写出到蓝牙设备的消息对象
     */
    private static class BluetoothSubmitHandler extends Handler {
        private DeviceManager mDeviceManager;

        private BluetoothSocket mSocket;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageType.BOUND:
                    boundDevice((DeviceManager) msg.obj);
                    break;

                case MessageType.WRITE:
                    writeOut(msg.obj, msg.arg1);
                    break;

                case MessageType.CLOSE:
                    try {
                        closeDevice();
                    } catch (Exception e1) {
                        LogUtil.logE(e1.getLocalizedMessage());
                        mDeviceManager = null;
                        mSocket = null;
                    }
                    break;

                case MessageType.STOP:
                    stopMQ();
                    break;
            }
        }

        /**
         * 绑定蓝牙设备
         *
         * @param deviceManager
         */
        private void boundDevice(DeviceManager deviceManager) {
            try {
                closeDevice();

                mDeviceManager = deviceManager;
                mSocket = deviceManager.getDeviceConnector().connect();

            } catch (IOException e) {
                LogUtil.logE(e.getLocalizedMessage());
                try {
                    closeDevice();
                } catch (Exception e1) {
                    LogUtil.logE(e1.getLocalizedMessage());
                }
            }
        }

        /**
         * 将数据写出到蓝牙设备
         *
         * @param data 数据
         * @param id   数据id
         */
        private void writeOut(Object data, int id) {
            if (mDeviceManager == null || mSocket == null || !mSocket.isConnected())
                return;

            //获取写出数据监听
            WriteDataListener listener = null;
            if (mDeviceManager.getWriteFormatter() instanceof InstallDataListen) {
                listener = (WriteDataListener) ((InstallDataListen) mDeviceManager.getWriteFormatter()).getDataListener();
            }

            //获取数据大小
            long dataSize = mDeviceManager.getWriteFormatter().sizeOf(data);

            //创建流对象
            BufferedInputStream in = null;
            BufferedOutputStream out;
            try {
                in = new BufferedInputStream(mDeviceManager.getWriteFormatter().writeFormat(data));
                out = new BufferedOutputStream(mSocket.getOutputStream());

                //创建缓冲区
                byte[] bytes = new byte[1024];
                int len;
                int count = 0;

                //判断是否需要告知监听当前写出进度
                if (listener != null && listener.enableProgress())
                    while ((len = in.read(bytes)) != -1) {
                        out.write(bytes, 0, len);
                        count++;
                        listener.progress(id, dataSize, count * 1024);
                    }
                else
                    while ((len = in.read(bytes)) != -1) {
                        out.write(bytes, 0, len);
                    }

                //写出所有数据
                out.flush();

                if (listener != null && listener.enableProgress()) {
                    listener.progress(id, dataSize, dataSize);
                }

                //数据写出成功发出成功通知
                if (listener != null) {
                    listener.success(id);
                }

                //关闭流
                in.close();

            } catch (IOException e) {
                if (listener != null)
                    listener.failed(id);

                LogUtil.logE(e.getLocalizedMessage());

                try {
                    if (in != null)
                        in.close();

                    closeDevice();
                } catch (Exception e1) {
                    LogUtil.logE(e1.getLocalizedMessage());
                }
            }
        }

        private void closeDevice() throws IOException {
            if (mDeviceManager != null && mSocket != null) {
                mDeviceManager.getDeviceConnector().close();
                mSocket.close();
                mDeviceManager = null;
                mSocket = null;
            }
        }


        /**
         * 结束消息队列
         */
        private void stopMQ() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                getLooper().quitSafely();
            } else {
                getLooper().quit();
            }

            removeCallbacksAndMessages(null);

            try {
                closeDevice();
            } catch (Exception e1) {
                LogUtil.logE(e1.getLocalizedMessage());
                mDeviceManager = null;
                mSocket = null;
            }
        }
    }

}
