package alvin.zhiyihealth.com.lib_bluetooth.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import alvin.zhiyihealth.com.lib_bluetooth.utils.ConnectTypeUtil;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

/**
 * Created by zouyifeng on 28/02/2018.
 * 09:17
 */

public class DeviceConnectorImpl implements DeviceConnector {

    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private DeviceManager mDevice;

    private BluetoothSocket mSocket;

    private BluetoothServerSocket mServerSocket;

    private String serverName = "";

    private BluetoothAdapter mAdapter;

    private ConnectState mState;

    private DeviceConnectorImpl() {
    }

    public static DeviceConnector form(DeviceManager device) {
        DeviceConnectorImpl deviceConnector = new DeviceConnectorImpl();

        deviceConnector.mAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceConnector.mDevice = device;
        deviceConnector.mState = ConnectState.READY;

        return deviceConnector;
    }

    @Override
    public BluetoothSocket connect() throws IOException {
        mState = ConnectState.WATIE;

        if (mSocket != null && mSocket.isConnected())
            return mSocket;

        if (ConnectTypeUtil.isClient(mDevice.getConnectType()))
            return actClientConnectDevice(mDevice);


        if (ConnectTypeUtil.isServer(mDevice.getConnectType()))
            return actServerConnectDevice();


        return null;
    }

    @Override
    public void reset() {
        if (mSocket == null && mServerSocket == null) {
            mState = ConnectState.READY;
        } else {
            try {
                close();
            } catch (IOException e) {
                mSocket = null;
                mServerSocket = null;
                mState = ConnectState.READY;
                LogUtil.logE(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public boolean isConnected() {
        if (mSocket != null)
            return mSocket.isConnected();
        else
            return false;
    }

    @Override
    public ConnectState getState() {
        return mState;
    }

    /**
     * 以客户端的身份连接蓝牙设备
     * 此方法会阻塞，请在异步调用
     *
     * @return 返回一个与服务端连接成功的套接字
     * @throws IOException 连接失败会抛出异常
     */
    public BluetoothSocket actClientConnectDevice(DeviceManager device) throws IOException {
        synchronized (this) {
            if (mSocket == null) {

                try {
                    mSocket = device.getDevice().createRfcommSocketToServiceRecord(uuid);
                    mSocket.connect();
                    mState = ConnectState.CONNECTED;
                } catch (IOException e) {
                    close();
                    throw e;
                }

            }
            return mSocket;
        }
    }

    /**
     * 以服务端的身份连接蓝牙设备
     * 此方法会阻塞至有蓝牙设备连接
     *
     * @return 返回与客户端连接成功的套接字
     * @throws IOException 连接失败会抛出异常
     */
    public BluetoothSocket actServerConnectDevice() throws IOException {
        synchronized (this) {
            LogUtil.logI("server address is " + mAdapter.getAddress());
            if (mServerSocket == null || mSocket == null) {
                try {

                    mServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(serverName, uuid);
                    LogUtil.logI("wait for the client bluetooth");
                    mSocket = mServerSocket.accept();
                    mState = ConnectState.CONNECTED;

                } catch (IOException e) {
                    close();
                    throw e;
                }
            }
            return mSocket;
        }
    }

    /**
     * 根据当前的设备连接类型进行套接字的关闭
     */
    @Override
    public void close() throws IOException {
        if (ConnectTypeUtil.isClient(mDevice.getConnectType())) {
            closeClientSocket();
        } else if (ConnectTypeUtil.isServer(mDevice.getConnectType())) {
            closeServerSocket();
        }

        mState = ConnectState.UNCONNECTED;
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


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
