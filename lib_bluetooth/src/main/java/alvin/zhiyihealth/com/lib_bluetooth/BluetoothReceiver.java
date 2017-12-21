package alvin.zhiyihealth.com.lib_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zouyifeng on 07/12/2017.
 * 16:51
 * <p>
 * 专门用于接收蓝牙广播的广播接收者
 */

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Utils.logI(action);

        if (onBlueToothStateListener == null) return;

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {//每扫描到一个设备，系统都会发送此广播。
            findNewDevice(intent);

        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            //监听蓝牙的状态 如：开关
            blueToothStateChange(intent);

        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //蓝牙设备连接断开时调用的方法
            deviceDisconnect(intent);
        }
    }

    /**
     * 当蓝牙设备断开时调用的方法，通过回调通知蓝牙设备断开连接
     * @param intent
     */
    private void deviceDisconnect(Intent intent) {
        BluetoothDevice cancelDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Utils.logI("connect state is " + cancelDevice);

        if (onBlueToothStateListener != null) {
            onBlueToothStateListener.deviceDisconnect(cancelDevice);
        }
    }

    /**
     * 发现新设备配对时调用
     *
     * @param intent 数据源
     */
    private void findNewDevice(Intent intent) {
        //获取蓝牙设备
        BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        BluetoothClass bluetoothClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
//
//        Utils.logI("name is " + scanDevice.getName() + ", type is " + Integer.toHexString(bluetoothClass.getMajorDeviceClass())
//                + "class is " + bluetoothClass.getDeviceClass()
//        );

        if (onBlueToothStateListener != null)
            onBlueToothStateListener.findDevice(scanDevice);
    }

    /**
     * 当蓝牙设备状态发生改变时调用此函数
     *
     * @param intent 数据源
     */
    private void blueToothStateChange(Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

        if (onBlueToothStateListener != null)
            onBlueToothStateListener.bluetoothState(state);
    }

    public BluetoothReceiver(OnBlueToothStateListener onBlueToothStateListener) {
        this.onBlueToothStateListener = onBlueToothStateListener;
    }

    /**
     * 监听蓝牙发出的任何事件
     */
    private OnBlueToothStateListener onBlueToothStateListener;

    public void setOnBlueToothStateListener(OnBlueToothStateListener onBlueToothStateListener) {
        this.onBlueToothStateListener = onBlueToothStateListener;
    }

    public interface OnBlueToothStateListener {
        void findDevice(BluetoothDevice scanDevice);

        void bluetoothState(int state);

        void deviceDisconnect(BluetoothDevice scanDevice);
    }
}
