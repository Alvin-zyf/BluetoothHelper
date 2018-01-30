package alvin.zhiyihealth.com.lib_bluetooth.utils;

import alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType;

/**
 * Created by zouyifeng on 29/01/2018.
 * 20:45
 * <p>
 * 链接蓝牙的类型工具类 {@link ConnectType}
 */

public final class ConnectTypeUtil{

    private ConnectTypeUtil(){}

    /**
     * 判断是否属于当前 连接类型
     *
     * @param mConnectType 连接类型
     * @return 如果是返回 true，否则 false
     */
    public static boolean isConnectType(int mConnectType) {
        return ConnectType.CLIENT_INPUT == mConnectType ||
                ConnectType.SERVER_INPUT == mConnectType ||
                ConnectType.CLIENT_OUTPUT == mConnectType ||
                ConnectType.SERVER_OUTPUT == mConnectType ||
                ConnectType.CLIENT_INPUT_OUTPUT == mConnectType ||
                ConnectType.SERVER_INPUT_OUTPUT == mConnectType;
    }

    /**
     * 传入参数currentType 是否与当前设备的ConnectType匹配
     *
     * @param currentType 当前想要判断的type
     * @param connectType 当前需要匹配的链接类型 {@link ConnectType}
     * @return 匹配返回true
     */
    public static boolean isCurrentType(int currentType, int connectType) {
        return (currentType & connectType) == currentType;
    }

    /**
     * 当前设备是否需要读取数据
     *
     * @return 是为true
     */
    public static boolean isInputType(int currentType) {
        return ((ConnectType.CLIENT_INPUT | ConnectType.SERVER_INPUT) & currentType) != 0;
    }

    /**
     * 当前设备是否需要提交数据
     *
     * @return 是为true
     */
    public static boolean isOutputType(int currentType) {
        return ((ConnectType.CLIENT_OUTPUT | ConnectType.SERVER_OUTPUT) & currentType) != 0;
    }

    /**
     * 当前设备是客户端吗
     *
     * @return 是为true
     */
    public static boolean isClient(int currentType) {
        return (ConnectType.CLIENT_INPUT_OUTPUT & currentType) != 0;
    }

    /**
     * 当前设备是服务端吗
     *
     * @return 是为true
     */
    public static boolean isServer(int currentType) {
        return (ConnectType.SERVER_INPUT_OUTPUT & currentType) != 0;
    }

}
