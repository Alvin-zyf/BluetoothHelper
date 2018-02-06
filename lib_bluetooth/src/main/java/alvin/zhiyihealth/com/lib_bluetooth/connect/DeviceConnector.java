package alvin.zhiyihealth.com.lib_bluetooth.connect;

import android.bluetooth.BluetoothSocket;

import java.io.Closeable;
import java.io.IOException;

import alvin.zhiyihealth.com.lib_bluetooth.helper.DeviceManager;

/**
 * Created by zouyifeng on 30/01/2018.
 * 14:56
 *
 * 蓝牙设备连接器
 */

public interface DeviceConnector extends Closeable{

    /**
     * 连接蓝牙设备
     *
     * @return 返回一个连接成功的套接字
     */
    BluetoothSocket connect(DeviceManager device) throws IOException;

    /**
     * 主要用于 关闭套接字
     * @throws IOException
     */
    @Override
    void close() throws IOException;
}
