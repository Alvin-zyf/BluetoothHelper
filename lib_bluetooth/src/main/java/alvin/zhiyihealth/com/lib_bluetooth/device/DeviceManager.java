package alvin.zhiyihealth.com.lib_bluetooth.device;

import android.bluetooth.BluetoothDevice;

import alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType;
import alvin.zhiyihealth.com.lib_bluetooth.data.ReadFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.data.WriteFormatter;

/**
 * Created by zouyifeng on 30/01/2018.
 * 15:47
 */

public interface DeviceManager {

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

    /**
     * 获取连接者
     * @return DeviceConnector 对象
     */
    DeviceConnector getDeviceConnector();

    /**
     *
     * @return 返回一个 读取数据解析对象
     */
    ReadFormatter getReadFormatter();

    /**
     *
     * @return 返回一个 数据写出转换对象
     */
    WriteFormatter getWriteFormatter();
}
