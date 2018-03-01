package alvin.zhiyihealth.com.lib_bluetooth.connect.contorl;

import alvin.zhiyihealth.com.lib_bluetooth.device.DeviceManager;

/**
 * Created by zouyifeng on 09/02/2018.
 * 00:30
 */

public interface ConnectController {

    /**
     * 连接蓝牙设备
     */
    void connect(DeviceManager device);

    /**
     * 断开蓝牙设备
     */
    void disConnect();

    /**
     * 释放线程池，调用该方法后不可继续使用该对象
     */
    void release();
}
