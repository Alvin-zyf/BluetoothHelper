package alvin.zhiyihealth.com.lib_bluetooth.helper;

import android.bluetooth.BluetoothDevice;

import alvin.zhiyihealth.com.lib_bluetooth.data.ReadFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.data.WriteFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.data.data_byte.ByteReadFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.data.data_byte.ByteWriteFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.utils.ConnectTypeUtil;

/**
 * Created by zouyifeng on 06/02/2018.
 * 10:58
 */

public class DeviceManagerImpl implements DeviceManager{

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

    private DeviceManagerImpl(){}

    @Override
    public BluetoothDevice getDevice() {
        return mDevice;
    }

    @Override
    public int getConnectType() {
        return mConnectType;
    }

    @Override
    public ReadFormatter getReadFormatter() {
        return mReadFormatter;
    }

    @Override
    public WriteFormatter getWriteFormatter() {
        return mWriteFormatter;
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

        public DeviceManager create() {
            DeviceManagerImpl deviceManager = new DeviceManagerImpl();

            initManager(deviceManager);

            return deviceManager;
        }

        /**
         * 初始化蓝牙设备管理者
         * @param deviceManager
         */
        private void initManager(DeviceManagerImpl deviceManager) {
            deviceManager.mDevice = P.mDevice;

            if (!ConnectTypeUtil.isConnectType(P.mConnectType)) {
                throw new RuntimeException("the ConnectType is wrong,Please use alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType");
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

        private ReadFormatter mReadFormatter;

        private WriteFormatter mWriteFormatter;
    }
}
