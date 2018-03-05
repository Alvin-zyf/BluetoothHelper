package alvin.zhiyihealth.com.lib_bluetooth.device;

import android.bluetooth.BluetoothSocket;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by zouyifeng on 30/01/2018.
 * 14:56
 * <p>
 * 蓝牙设备连接器
 */

public interface DeviceConnector extends Closeable {

    /**
     * 连接蓝牙设备
     *
     * @return 返回一个连接成功的套接字
     */
    BluetoothSocket connect() throws IOException;

    /**
     * 将对象恢复至初始状态
     */
    void reset();

    /**
     * 当前是否连接
     * @return true连接 false没连接
     */
    boolean isConnected();

    ConnectState getState();

    /**
     * 主要用于 关闭套接字
     *
     * @throws IOException
     */
    @Override
    void close() throws IOException;

    enum ConnectState{
        /**
         * 准备连接
         */
        READY,

        /**
         * 等待连接
         */
        WAITE,

        /**
         * 已连接
         */
        CONNECTED,

        /**
         * 断开连接
         */
        UNCONNECTED
    }
}
