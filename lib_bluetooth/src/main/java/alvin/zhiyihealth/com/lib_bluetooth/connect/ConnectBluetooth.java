package alvin.zhiyihealth.com.lib_bluetooth.connect;

import android.bluetooth.BluetoothAdapter;

import alvin.zhiyihealth.com.lib_bluetooth.DeviceBaseHelper;

/**
 * Created by zouyifeng on 14/12/2017.
 * 20:47
 */

public abstract class ConnectBluetooth implements Runnable {

    DeviceBaseHelper mDevice;

    BluetoothAdapter mAdapter;

    boolean isConnect = false;

    public ConnectBluetooth(DeviceBaseHelper mDevice, BluetoothAdapter mAdapter) {
        this.mDevice = mDevice;
        this.mAdapter = mAdapter;

        if (!DeviceBaseHelper.isConnectType(mDevice.getConnectType()))
            throw new RuntimeException("DeviceBaseHelper's ConnectType isn't CONNECT_TYPE_CLIENT_INPUT or CONNECT_TYPE_SERVER");
    }

    public Thread getCurrentRunThread() {
        return Thread.currentThread();
    }

    /**
     * 设置连接状态
     *
     * @param connect
     */
    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    /**
     * 当前连接状态
     *
     * @return
     */
    public boolean isConnect() {
        return isConnect;
    }

}

