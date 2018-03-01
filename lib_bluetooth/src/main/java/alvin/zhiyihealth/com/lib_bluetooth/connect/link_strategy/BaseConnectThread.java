package alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import alvin.zhiyihealth.com.lib_bluetooth.device.DeviceManager;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

/**
 * Created by zouyifeng on 21/02/2018.
 * 16:17
 */

public abstract class BaseConnectThread implements ConnectStrategy.ConnectThread {
    Thread mMyThread;

    DeviceManager mDeviceManager;

    BluetoothSocket mSocket;

    @Override
    public void run() {
        mMyThread = Thread.currentThread();
    }

    @Override
    public boolean currentThreadState() {
        return mMyThread != null && mMyThread.isAlive();
    }


    @Override
    public void boundDeviceManager(DeviceManager deviceManager) {
        mDeviceManager = deviceManager;
    }


    @Override
    public void disConnect() {

        try {
            if (mMyThread != null) {
                mMyThread.interrupt();
                mMyThread = null;
            }

            if (mDeviceManager.getDeviceConnector() != null)
                mDeviceManager.getDeviceConnector().close();

            if (mSocket != null)
                mSocket.close();

            mSocket = null;
        } catch (IOException e) {
            LogUtil.logE(e.getLocalizedMessage());
        }
    }

    public DeviceManager getDeviceManager() {
        return mDeviceManager;
    }
}
