package alvin.zhiyihealth.com.lib_bluetooth.device;

import android.bluetooth.BluetoothDevice;

import alvin.zhiyihealth.com.lib_bluetooth.data.ReadFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.data.WriteFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.data.data_byte.ByteReadFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.data.data_byte.ByteWriteFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.listener.InstallDataListen;
import alvin.zhiyihealth.com.lib_bluetooth.listener.ReadDataListener;
import alvin.zhiyihealth.com.lib_bluetooth.listener.WriteDataListener;
import alvin.zhiyihealth.com.lib_bluetooth.utils.ConnectTypeUtil;

/**
 * Created by zouyifeng on 06/02/2018.
 * 10:58
 */

public class DeviceManagerImpl implements DeviceManager {

    /**
     * 蓝牙设备
     */
    private BluetoothDevice mDevice;

    /**
     * 连接类型 {@link alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType}
     */
    private int mConnectType = -1;

    private ReadFormatter mReadFormatter;

    private WriteFormatter mWriteFormatter;

    private DeviceConnector mDeviceConnector;

    private DeviceManagerImpl() {
    }

    @Override
    public BluetoothDevice getDevice() {
        return mDevice;
    }

    @Override
    public int getConnectType() {
        return mConnectType;
    }

    @Override
    public DeviceConnector getDeviceConnector() {
        return mDeviceConnector;
    }

    @Override
    public ReadFormatter getReadFormatter() {
        return mReadFormatter;
    }

    @Override
    public WriteFormatter getWriteFormatter() {
        return mWriteFormatter;
    }

    public DeviceManagerImpl setReadDataListener(ReadDataListener dataListener) {
        if (mReadFormatter != null && mReadFormatter instanceof InstallDataListen) {
            ((InstallDataListen) mReadFormatter).setDataListener(dataListener);
        }

        return this;
    }

    public DeviceManagerImpl setWriteDataListener(WriteDataListener dataListener) {
        if (mWriteFormatter != null && mWriteFormatter instanceof InstallDataListen) {
            ((InstallDataListen) mWriteFormatter).setDataListener(dataListener);
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceManagerImpl that = (DeviceManagerImpl) o;

        if (mConnectType != that.mConnectType) return false;
        if (mDevice != null ? !mDevice.equals(that.mDevice) : that.mDevice != null) return false;
        if (mReadFormatter != null ? !mReadFormatter.equals(that.mReadFormatter) : that.mReadFormatter != null)
            return false;
        if (mWriteFormatter != null ? !mWriteFormatter.equals(that.mWriteFormatter) : that.mWriteFormatter != null)
            return false;
        return mDeviceConnector != null ? mDeviceConnector.equals(that.mDeviceConnector) : that.mDeviceConnector == null;
    }

    /**
     * 构建者
     */
    public static class Builder {
        Params P;

        public Builder() {
            P = new Params();
        }

        public Builder buildDevice(BluetoothDevice device) {
            P.mDevice = device;
            return this;
        }

        /**
         * 连接类型 {@link alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType}
         */
        public Builder setConnectType(int connectType) {
            P.mConnectType = connectType;

            return this;
        }

        public Builder setReadFormatter(ReadFormatter readFormatter) {
            P.mReadFormatter = readFormatter;

            return this;
        }

        public Builder setWriteFormatter(WriteFormatter writeFormatter) {
            P.mWriteFormatter = writeFormatter;

            return this;
        }

        public Builder setDeviceConnector(DeviceConnector deviceConnector) {
            P.mDeviceConnector = deviceConnector;

            return this;
        }

        public DeviceManagerImpl create() {
            DeviceManagerImpl deviceManager = new DeviceManagerImpl();

            initManager(deviceManager);

            return deviceManager;
        }

        /**
         * 初始化蓝牙设备管理者
         *
         * @param deviceManager
         */
        private void initManager(DeviceManagerImpl deviceManager) {
            deviceManager.mDevice = P.mDevice;

            if (!ConnectTypeUtil.isConnectType(P.mConnectType)) {
                throw new RuntimeException("the ConnectType is wrong,Please use alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType");
            }

            if (P.mDeviceConnector == null) {
                deviceManager.mDeviceConnector = DeviceConnectorImpl.form(deviceManager);
            } else {
                deviceManager.mDeviceConnector = P.mDeviceConnector;
            }

            if (P.mReadFormatter == null) {
                deviceManager.mReadFormatter = new ByteReadFormatter();
            } else {
                deviceManager.mReadFormatter = P.mReadFormatter;
            }

            if (P.mWriteFormatter == null) {
                deviceManager.mWriteFormatter = new ByteWriteFormatter();
            } else {
                deviceManager.mWriteFormatter = P.mWriteFormatter;
            }

        }
    }

    private static class Params {
        /**
         * 蓝牙设备
         */
        private BluetoothDevice mDevice;

        /**
         * 连接类型 {@link alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType}
         */
        private int mConnectType = -1;

        private DeviceConnector mDeviceConnector;

        private ReadFormatter mReadFormatter;

        private WriteFormatter mWriteFormatter;
    }
}
