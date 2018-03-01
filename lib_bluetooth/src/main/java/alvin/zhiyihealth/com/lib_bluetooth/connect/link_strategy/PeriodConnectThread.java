package alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

/**
 * Created by zouyifeng on 28/02/2018.
 * 15:33
 */

public abstract class PeriodConnectThread extends BaseConnectThread implements ConnectPeriod {

    @Override
    public void run() {
        super.run();

        try {
            onStart();

            mSocket = mDeviceManager.getDeviceConnector().connect();

            if (mSocket == null) {
                throw new RuntimeException("socket is null");
            }

            onConnected(mSocket);

        } catch (Exception e) {
            LogUtil.logE(e.getLocalizedMessage());

            onError(e);
        } finally {
            disConnect();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onConnected(BluetoothSocket socket) throws IOException {

    }

    @Override
    public void onError(Exception e) {

    }
}
