package alvin.zhiyihealth.com.lib_bluetooth.connect;

import android.bluetooth.BluetoothAdapter;

import alvin.zhiyihealth.com.lib_bluetooth.helper.BaseDeviceHelper;
import alvin.zhiyihealth.com.lib_bluetooth.utils.ConnectTypeUtil;

/**
 * Created by zouyifeng on 14/12/2017.
 * 20:47
 *
 * 蓝牙链接抽象类,用于执行链接需要的准备工作，最终获取socket
 */

public abstract class ConnectBluetooth implements Runnable {

    BaseDeviceHelper mDevice;

    BluetoothAdapter mAdapter;

    boolean isConnect = false;

    public ConnectBluetooth(BaseDeviceHelper mDevice, BluetoothAdapter mAdapter) {
        this.mDevice = mDevice;
        this.mAdapter = mAdapter;

        if (!ConnectTypeUtil.isConnectType(mDevice.getConnectType()))
            throw new RuntimeException("BaseDeviceHelper's ConnectTypeUtil isn't CLIENT_INPUT or CONNECT_TYPE_SERVER");
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

