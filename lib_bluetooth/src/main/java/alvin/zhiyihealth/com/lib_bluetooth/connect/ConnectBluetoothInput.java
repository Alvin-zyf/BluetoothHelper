package alvin.zhiyihealth.com.lib_bluetooth.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.IOException;

import alvin.zhiyihealth.com.lib_bluetooth.utils.ConnectTypeUtil;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;
import alvin.zhiyihealth.com.lib_bluetooth.helper.BaseDeviceHelper;

/**
 * Created by zouyifeng on 08/12/2017.
 * 10:33
 * <p>
 * 专门建立蓝牙连接的线程,用于读取数据
 */

public class ConnectBluetoothInput extends ConnectBluetooth {
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            mDevice.processData((byte[]) msg.obj, msg.arg1);
        }
    };

    /**
     * 作为服务端使用的套接字
     */
    private BluetoothServerSocket mServerSocket;

    public ConnectBluetoothInput(BaseDeviceHelper mDevice, BluetoothAdapter mAdapter) {
        super(mDevice, mAdapter);
    }

    /**
     * 取消连接操作
     */
    private void disConnect() {
        try {
            if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.CLIENT_INPUT)) {
                if (mDevice.getSocket() != null) {
                    mDevice.getSocket().close();
                    mDevice.setSocket(null);
                }
            } else if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.SERVER_INPUT)) {
                if (mDevice.getServerSocket() != null) {
                    mDevice.getServerSocket().close();
                    mDevice.setServerSocket(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("The bluetooth connect close is failed");
        }
    }

    @Override
    public void run() {
        if (!isConnect) return;

        if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.CLIENT_INPUT)) {
            startLinkServer();
        } else if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.SERVER_INPUT)) {
            createServer();
        }


    }

    /**
     * 建立服务端等待客户端连接
     */
    private void createServer() {
        while (isConnect) {
            BufferedInputStream bufferedInputStream = null;
            BluetoothSocket mSocket = null;

            try {
                if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.SERVER_INPUT)) {
                    mSocket = mDevice.actServerConnectDevice(mAdapter);
                }

                if (mSocket == null) {
                    LogUtil.logI("socket is null");
                    return;
                }
                LogUtil.logI("already link");

                bufferedInputStream = new BufferedInputStream(mSocket.getInputStream());
                readData(bufferedInputStream);

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.logE("server : has error");
            } finally {
                mDevice.closeSocket();
            }
        }
    }

    /**
     * 进行客户端连接
     */
    private void startLinkServer() {
        BufferedInputStream bufferedInputStream = null;
        BluetoothSocket mSocket = null;
        try {
            if (ConnectTypeUtil.isCurrentType(mDevice.getConnectType(),ConnectType.CLIENT_INPUT)) {
                mSocket = mDevice.actClientConnectDevice();
            }

            if (mSocket == null) {
                LogUtil.logI("socket is null");
                return;
            }

            LogUtil.logI("already link");

            //接入输入流
            bufferedInputStream = new BufferedInputStream(mSocket.getInputStream());

            //缓冲空间
            readData(bufferedInputStream);
        } catch (Exception e) {
            e.printStackTrace();
            isConnect = false;
            LogUtil.logE("client : connect has error");
        } finally {
            mDevice.closeSocket();
        }
    }

    /**
     * 读取数据
     *
     * @param bufferedInputStream 输入流
     * @throws IOException
     */
    private void readData(BufferedInputStream bufferedInputStream) throws IOException {
        //缓冲空间
        byte[] data = new byte[1024];
        int len;
        synchronized (ConnectBluetoothInput.class) {
            while (isConnect) {
                //获取数据处理数据
                while ((len = bufferedInputStream.read(data)) != -1) {
                    LogUtil.logI("read again");
                    Message obtain = Message.obtain(handler);
                    obtain.arg1 = len;
                    obtain.obj = data;
                    obtain.sendToTarget();
                    obtain.sendToTarget();
                }
            }
        }
    }




    public BaseDeviceHelper getDeviceHelper() {
        return mDevice;
    }
}
