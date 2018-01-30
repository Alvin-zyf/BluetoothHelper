package alvin.zhiyihealth.com.lib_bluetooth.helper;

import android.bluetooth.BluetoothDevice;

import alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType;

/**
 * Created by zouyifeng on 30/01/2018.
 * 15:47
 */

public interface DeviceImprove {

    /**
     * 返回一个蓝牙设备对象
     *
     * @return {@link BluetoothDevice}
     */
    BluetoothDevice getDevice();

    /**
     * 用于返回蓝牙连接状态类型
     *
     * @return 蓝牙状态有: {@link ConnectType}
     */
    int getConnectType();

    
}
