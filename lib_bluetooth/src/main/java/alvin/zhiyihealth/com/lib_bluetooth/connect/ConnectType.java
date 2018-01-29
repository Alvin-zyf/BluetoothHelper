package alvin.zhiyihealth.com.lib_bluetooth.connect;

/**
 * Created by zouyifeng on 29/01/2018.
 * 20:55
 */

public interface ConnectType {
    /**
     * 以客户端的形式连接蓝牙服务器
     * <p>读取数据</>
     */
    int CLIENT_INPUT = 0x0000000F;

    /**
     * 以客户端的形式连接蓝牙服务器
     * <p>提交数据</>
     */
    int CLIENT_OUTPUT = 0x00000F00;

    /**
     * 包含两者 {@link #CLIENT_INPUT}  {@link #CLIENT_OUTPUT}
     */
    int CLIENT_INPUT_OUTPUT = CLIENT_INPUT | CLIENT_OUTPUT;

    /**
     * 以服务端的形式等待蓝牙客户端连接
     * <p>读取数据</>
     */
    int SERVER_INPUT = 0x000000F0;

    /**
     * 以服务端的形式等待蓝牙客户端连接
     * <p>提交数据</>
     */
    int SERVER_OUTPUT = 0x000F000;

    /**
     * 包含两者 {@link #SERVER_INPUT}  {@link #SERVER_OUTPUT}
     */
    int SERVER_INPUT_OUTPUT = SERVER_INPUT | SERVER_OUTPUT;

}
