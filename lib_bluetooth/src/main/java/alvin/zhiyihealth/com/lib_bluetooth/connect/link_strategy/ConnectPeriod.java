package alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

/**
 * Created by zouyifeng on 21/02/2018.
 * 17:15
 */

public interface ConnectPeriod {

    void onStart();

    void onConnected(BluetoothSocket socket) throws IOException;

    void onError(Exception e);
}
