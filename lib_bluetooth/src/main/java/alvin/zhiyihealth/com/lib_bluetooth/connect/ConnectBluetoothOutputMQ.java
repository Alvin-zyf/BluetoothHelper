package alvin.zhiyihealth.com.lib_bluetooth.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;

import alvin.zhiyihealth.com.lib_bluetooth.utils.ConnectTypeUtil;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;
import alvin.zhiyihealth.com.lib_bluetooth.helper.BaseDeviceHelper;

/**
 * Created by zouyifeng on 12/12/2017.
 * 10:16
 * <p>
 * 向蓝牙设备写入的线程
 */

public class ConnectBluetoothOutputMQ extends ConnectBluetooth {

    private BluetoothSubmitHandler handler;

    /**
     * handler消息标示，用于停止当前looper循环
     */
    static final int STOP_MSG_TYPE_OUTPUT = 0x0F00000;

    /**
     * handler消息标示，用于执行小数据传输
     */
    static final int S_EXECUTE_MSG_TYPE_OUTPUT = 0x000000F;

    /**
     * handler消息标示，用于执行大数据传输
     */
    static final int B_EXECUTE_MSG_TYPE_OUTPUT = 0x00000FF;

    /**
     * handler消息标示，用于切换Device
     */
    static final int CHANGE_DEVICE = 0x00000FFF;

    public ConnectBluetoothOutputMQ(BaseDeviceHelper mDevice, BluetoothAdapter mAdapter) {
        super(mDevice, mAdapter);
    }

    @Override
    public void run() {
        Looper.prepare();

        handler = new BluetoothSubmitHandler(mAdapter);

        Looper.loop();
    }

    /**
     * 发送消息关闭消息队列
     */
    public void closeMQ() {
        Message obtain = Message.obtain(handler);
        obtain.what = STOP_MSG_TYPE_OUTPUT;
        obtain.obj = mDevice;
        obtain.sendToTarget();
    }

    /**
     * 提交数据，发送给蓝牙设备
     *
     * @param isSmall true 为小数据，执行小数据传输方法{@link #S_EXECUTE_MSG_TYPE_OUTPUT}
     *                false 为大数据{@link #B_EXECUTE_MSG_TYPE_OUTPUT}
     */
    public void submitData(boolean isSmall) {
        if (mDevice == null) return;

        Message obtain = Message.obtain(handler);
        obtain.what = isSmall ? S_EXECUTE_MSG_TYPE_OUTPUT : B_EXECUTE_MSG_TYPE_OUTPUT;
        obtain.obj = mDevice;
        obtain.sendToTarget();
    }

    /**
     * 调用此方法切换设备时，请将之前的设备关闭,如果未关闭则不能新建连接
     *
     * @param mDevice
     */
    public void setDeviceHelper(BaseDeviceHelper mDevice) {
        if (!this.mDevice.getDevice().getAddress().equals(mDevice.getDevice().getAddress())) {
            Message obtain = Message.obtain(handler);
            obtain.what = CHANGE_DEVICE;
            obtain.obj = this.mDevice;
            obtain.sendToTarget();
            this.mDevice = mDevice;
        }
    }

    public BaseDeviceHelper getDeviceHelper(){
        return mDevice;
    }

    @Override
    public boolean isConnect() {
        isConnect = handler != null && handler.isConnect;
        return isConnect;
    }

    @Override
    public void setConnect(boolean connect) {
    }



    /**
     * BluetoothSubmitHandler 是专门将数据写出到蓝牙设备的消息对象
     */
    private static class BluetoothSubmitHandler extends Handler {

        private BluetoothAdapter mAdapter;

        private boolean isConnect;

        public BluetoothSubmitHandler(BluetoothAdapter mAdapter) {
            this.mAdapter = mAdapter;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case S_EXECUTE_MSG_TYPE_OUTPUT:
                    submitSmallData((BaseDeviceHelper) msg.obj);
                    break;

                case B_EXECUTE_MSG_TYPE_OUTPUT:
                    submitBigData((BaseDeviceHelper) msg.obj);
                    break;

                case STOP_MSG_TYPE_OUTPUT:
                    closeMQ((BaseDeviceHelper) msg.obj);
                    break;

                case CHANGE_DEVICE:
                    ((BaseDeviceHelper) msg.obj).closeSocket();
                    break;
            }
        }

        /**
         * 上传数据量较大的数据
         *
         * @param mDevice 蓝牙设备帮助类
         */
        private void submitBigData(BaseDeviceHelper mDevice) {
            BufferedInputStream bufferedInputStream = null;
            BufferedOutputStream bufferedOutputStream;
            BluetoothSocket mSocket;
            try {
                if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.CLIENT_OUTPUT)) {
                    mSocket = mDevice.actClientConnectDevice();
                } else if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.SERVER_OUTPUT)) {
                    mSocket = mDevice.actServerConnectDevice(mAdapter);
                } else return;

                if (mSocket == null) {
                    LogUtil.logI("socket is null");
                    return;
                }

                LogUtil.logI("already link");

                isConnect = true;

                //接入输入流
                bufferedInputStream = new BufferedInputStream(new FileInputStream(mDevice.bigData()));
                bufferedOutputStream = new BufferedOutputStream(mSocket.getOutputStream());

                //缓冲空间
                byte[] data = new byte[1024];
                int len;
                //获取数据处理数据
                while ((len = bufferedInputStream.read(data)) != -1) {
                    LogUtil.logI("write again");
                    bufferedOutputStream.write(data, 0, len);
                }

                bufferedOutputStream.flush();
            } catch (Exception e) {
                isConnect = false;
                e.printStackTrace();
                LogUtil.logE("client : connect has error");
            } finally {
                try {
                    if (bufferedInputStream != null)
                        bufferedInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 以客户端的身份上传数据量较小的数据
         *
         * @param mDevice 蓝牙设备帮助类
         */
        private void submitSmallData(BaseDeviceHelper mDevice) {
            BufferedOutputStream bufferedOutputStream;
            BluetoothSocket mSocket;
            try {
                if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.CLIENT_OUTPUT)) {
                    mSocket = mDevice.actClientConnectDevice();
                } else if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.SERVER_OUTPUT)) {
                    mSocket = mDevice.actServerConnectDevice(mAdapter);
                } else return;

                if (mSocket == null) {
                    LogUtil.logI("socket is null");
                    return;
                }

                isConnect = true;

                LogUtil.logI("already link");

                //接入输入流
                bufferedOutputStream = new BufferedOutputStream(mSocket.getOutputStream());

                //获取数据处理数据
                LogUtil.logI("write again");
                bufferedOutputStream.write(mDevice.smallData(), 0, mDevice.smallData().length);
                bufferedOutputStream.flush();

            } catch (Exception e) {
                isConnect = false;
                e.printStackTrace();
                LogUtil.logE("client : connect has error");
                mDevice.closeSocket();
            }
        }


        /**
         * 结束消息队列
         *
         * @param mDevice
         */
        private void closeMQ(BaseDeviceHelper mDevice) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                getLooper().quitSafely();
            } else {
                getLooper().quit();
            }

            removeCallbacksAndMessages(null);

            mDevice.closeSocket();
        }

    }


}
