package alvin.zhiyihealth.com.lib_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by zouyifeng on 08/12/2017.
 * 09:40
 */

public abstract class DeviceBaseHelper {


    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device;

    private BluetoothSocket mSocket;

    private BluetoothServerSocket mServerSocket;

    private String serverName = "";


    public DeviceBaseHelper(BluetoothDevice device, int mConnectType) {
        this.device = device;
        this.mConnectType = mConnectType;

        if (device != null)
            serverName = device.getName();
        else
            serverName = "测试";
    }

    /**
     * 当前布尔值用于是否需要在向蓝牙设备提交完所有数据时清空缓存
     * 即{@link #smallData} 和 {@link #bigData}
     */
    private boolean isClean;

    /**
     * 用于标示当前设备的蓝牙连接状态
     * 蓝牙状态有: {@link #CONNECT_TYPE_CLIENT_INPUT} or {@link #CONNECT_TYPE_SERVER_INPUT}
     */
    private int mConnectType;

    /**
     * 以客户端的形式连接蓝牙服务器
     * <p>读取数据</>
     * {@link #mConnectType}
     */
    public static final int CONNECT_TYPE_CLIENT_INPUT = 0x0000000F;

    /**
     * 以客户端的形式连接蓝牙服务器
     * <p>提交数据</>
     */
    public static final int CONNECT_TYPE_CLIENT_OUTPUT = 0x00000F00;

    /**
     * 包含两者 {@link #CONNECT_TYPE_CLIENT_INPUT}  {@link #CONNECT_TYPE_CLIENT_OUTPUT}
     */
    public static final int CONNECT_TYPE_CLIENT_INPUT_OUTPUT = CONNECT_TYPE_CLIENT_INPUT | CONNECT_TYPE_CLIENT_OUTPUT;

    /**
     * 以服务端的形式等待蓝牙客户端连接
     * <p>读取数据</>
     * {@link #mConnectType}
     */
    public static final int CONNECT_TYPE_SERVER_INPUT = 0x000000F0;

    /**
     * 以服务端的形式等待蓝牙客户端连接
     * <p>提交数据</>
     * {@link #mConnectType}
     */
    public static final int CONNECT_TYPE_SERVER_OUTPUT = 0x000F000;

    /**
     * 包含两者 {@link #CONNECT_TYPE_SERVER_INPUT}  {@link #CONNECT_TYPE_SERVER_OUTPUT}
     */
    public static final int CONNECT_TYPE_SERVER_INPUT_OUTPUT = CONNECT_TYPE_SERVER_INPUT | CONNECT_TYPE_SERVER_OUTPUT;

    /**
     * 判断是否属于当前 连接类型
     *
     * @param mConnectType 连接类型
     * @return 如果是返回 true，否则 false
     */
    public static boolean isConnectType(int mConnectType) {
        return CONNECT_TYPE_CLIENT_INPUT == mConnectType ||
                CONNECT_TYPE_SERVER_INPUT == mConnectType ||
                CONNECT_TYPE_CLIENT_OUTPUT == mConnectType ||
                CONNECT_TYPE_SERVER_OUTPUT == mConnectType ||
                CONNECT_TYPE_CLIENT_INPUT_OUTPUT == mConnectType ||
                CONNECT_TYPE_SERVER_INPUT_OUTPUT == mConnectType;
    }

    /**
     * 传入参数currentType 是否与当前设备的ConnectType匹配
     *
     * @param currentType 当前想要判断的type
     * @return 匹配返回true
     */
    public boolean isCurrentType(int currentType) {
        return (currentType & mConnectType) == currentType;
    }

    /**
     * 当前设备是否需要读取数据
     *
     * @return 是为true
     */
    public boolean isInputType() {
        return ((CONNECT_TYPE_CLIENT_INPUT | CONNECT_TYPE_SERVER_INPUT) & mConnectType) != 0;
    }

    /**
     * 当前设备是否需要提交数据
     *
     * @return 是为true
     */
    public boolean isOutputType() {
        return ((CONNECT_TYPE_CLIENT_OUTPUT | CONNECT_TYPE_SERVER_OUTPUT) & mConnectType) != 0;
    }

    /**
     * 当前设备是客户端吗
     *
     * @return 是为true
     */
    public boolean isClient() {
        return (CONNECT_TYPE_CLIENT_INPUT_OUTPUT & mConnectType) != 0;
    }

    /**
     * 当前设备是服务端吗
     *
     * @return 是为true
     */
    public boolean isServer() {
        return (CONNECT_TYPE_SERVER_INPUT_OUTPUT & mConnectType) != 0;
    }

    /**
     * 以客户端的身份连接蓝牙设备
     * 此方法会阻塞，请在异步调用
     *
     * @return 返回一个与服务端连接成功的套接字
     * @throws IOException 连接失败会抛出异常
     */
    public synchronized BluetoothSocket actClientConnectDevice() throws IOException {
        if (mSocket == null) {
            mSocket = device.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
        }
        return mSocket;
    }

    /**
     * 以服务端的身份连接蓝牙设备
     * 此方法会阻塞至有蓝牙设备连接
     *
     * @return 返回与客户端连接成功的套接字
     * @throws IOException 连接失败会抛出异常
     */
    public synchronized BluetoothSocket actServerConnectDevice(BluetoothAdapter mAdapter) throws IOException {
        Utils.logI("server address is " + mAdapter.getAddress());
        if (mServerSocket == null || mSocket == null) {
            mServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(serverName, uuid);
            Utils.logI("wait for the client bluetooth");
            mSocket = mServerSocket.accept();
        }

        return mSocket;
    }

    /**
     * 关闭服务套接字
     *
     * @throws IOException
     */
    public void closeServerSocket() throws IOException {
        if (mServerSocket != null) {
            mServerSocket.close();
            mServerSocket = null;
            mSocket = null;
        }
    }

    /**
     * 关闭跟客户端连接的套接字
     *
     * @throws IOException
     */
    public void closeSocket() throws IOException {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }
    }

    /**
     * 根据当前的设备连接类型进行套接字的关闭
     */
    public synchronized void closeAllSocket() {
        try {
            if (isCurrentType(DeviceBaseHelper.CONNECT_TYPE_CLIENT_INPUT)) {
                closeSocket();
            } else if (isCurrentType(DeviceBaseHelper.CONNECT_TYPE_SERVER_INPUT)) {
                closeServerSocket();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.logE("The bluetooth connect close is failed");
        }
    }


    /**
     * 用于返回蓝牙连接状态类型
     *
     * @return 蓝牙状态有: {@link #CONNECT_TYPE_CLIENT_INPUT} or {@link #CONNECT_TYPE_SERVER_INPUT}
     */
    public int getConnectType() {
        return mConnectType;
    }

    /**
     * 解析数据
     *
     * @param data 数据源
     * @param len  数据有效长度
     */
    public void processData(byte[] data, int len) {
    }

    /**
     * 小数据源缓冲区
     */
    private byte[] smallData;

    /**
     * 向蓝牙设备提交数据
     *
     * @return 返回一个字节数组，即字节化后的数据源，此方法只适合可一次性读取到内存中的文件
     * 若大文件传输请使用 {@link #bigData()}
     */
    public byte[] smallData() {
        return smallData;
    }

    /**
     * 设置数据源
     *
     * @param smallData
     */
    public void setSmallData(byte[] smallData) {
        this.smallData = smallData;
    }

    /**
     * 大数据源 文件对象
     */
    private File bigData;

    /**
     * 向蓝牙提交大数据文件
     *
     * @return 返回一个File对象，框架会自动读取到当前文件进行传输
     */
    public File bigData() {
        return bigData;
    }

    public void setBigData(File bigData) {
        this.bigData = bigData;
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        isClean = clean;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BluetoothSocket getSocket() {
        return mSocket;
    }

    public void setSocket(BluetoothSocket mSocket) {
        this.mSocket = mSocket;
    }

    public BluetoothServerSocket getServerSocket() {
        return mServerSocket;
    }

    public void setServerSocket(BluetoothServerSocket mServerSocket) {
        this.mServerSocket = mServerSocket;
    }
}
