package alvin.zhiyihealth.com.lib_bluetooth.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import alvin.zhiyihealth.com.lib_bluetooth.utils.ConnectTypeUtil;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;
import alvin.zhiyihealth.com.lib_bluetooth.connect.*;
/**
 * Created by zouyifeng on 08/12/2017.
 * 09:40
 *
 * 封装蓝牙设备对象帮助类 {@link BluetoothDevice}
 */

public abstract class BaseDeviceHelper {


    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private BluetoothDevice device;

    private BluetoothSocket mSocket;

    private BluetoothServerSocket mServerSocket;

    private String serverName = "";

    /**
     * 创建蓝牙设备帮助对象的构造方法
     *
     * @param device 需要链接的蓝牙设备
     * @param mConnectType 链接类型 详情见{@link ConnectType}
     */
    public BaseDeviceHelper(BluetoothDevice device, int mConnectType) {
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
     * 蓝牙状态有: {@link ConnectType}
     */
    private int mConnectType;


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
        LogUtil.logI("server address is " + mAdapter.getAddress());
        if (mServerSocket == null || mSocket == null) {
            mServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(serverName, uuid);
            LogUtil.logI("wait for the client bluetooth");
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
    public void closeClientSocket() throws IOException {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }
    }

    /**
     * 根据当前的设备连接类型进行套接字的关闭
     */
    public synchronized void closeSocket() {
        try {
            if (ConnectTypeUtil.isCurrentType(mConnectType,ConnectType.CLIENT_INPUT)) {
                closeClientSocket();
            } else if (ConnectTypeUtil.isCurrentType(mConnectType,ConnectType.SERVER_INPUT)) {
                closeServerSocket();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("The bluetooth connect close is failed");
        }
    }


    /**
     * 用于返回蓝牙连接状态类型
     *
     * @return 蓝牙状态有: {@link ConnectType}
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
