package alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

/**
 * Created by zouyifeng on 21/02/2018.
 * 17:15
 *
 * 建立连接的生命周期
 */

public interface ConnectPeriod {

    /**
     * 开始连接
     */
    void onStart();

    /**
     * 连接成功
     * @param socket 与连接设备建立连接所返回的套接字
     */
    void onConnected(BluetoothSocket socket) throws IOException;

    /**
     * 在连接过程中发生异常
     * @param e
     */
    void onError(Exception e);
}
