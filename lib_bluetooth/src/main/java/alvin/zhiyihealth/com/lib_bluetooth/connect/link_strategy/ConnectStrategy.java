package alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy;

import java.util.concurrent.Executor;

import alvin.zhiyihealth.com.lib_bluetooth.device.DeviceManager;

/**
 * Created by zouyifeng on 09/02/2018.
 * 22:05
 */

public interface ConnectStrategy {

    /**
     * 断开连接
     */
    void disConnect();

    /**
     * 连接蓝牙设备
     * @param executor 线程池
     * @param deviceManager 设备管理者
     */
    void connect(Executor executor, DeviceManager deviceManager);

    interface ConnectThread extends Runnable {
        /**
         * 断开连接
         */
        void disConnect();

        /**
         * 当前线程状态
         *
         * @return true 为运行 ，否则为false
         */
        boolean currentThreadState();

        void boundDeviceManager(DeviceManager deviceManager);

    }
}
