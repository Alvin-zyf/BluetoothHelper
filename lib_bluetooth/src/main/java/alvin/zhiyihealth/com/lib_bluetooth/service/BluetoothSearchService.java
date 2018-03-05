package alvin.zhiyihealth.com.lib_bluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import alvin.zhiyihealth.com.lib_bluetooth.receiver.BluetoothReceiver;

/**
 * Created by zouyifeng on 02/03/2018.
 * 19:43
 */

public class BluetoothSearchService extends Service {
    private BroadcastReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IBinderImpl(mReceiver);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(this);
    }

    /**
     * 注册广播接收者 接收蓝牙设备广播
     */
    private void registerReceiver(Context context) {
        if (mReceiver == null) {
            mReceiver = new BluetoothReceiver();
            IntentFilter filter = new IntentFilter();
            //发现设备
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            //蓝牙设备连接状态发生改变时调用
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            //蓝牙状态发生改变
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            context.registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    public static class IBinderImpl extends Binder {
        private BroadcastReceiver mReceiver;

        public IBinderImpl(BroadcastReceiver receiver) {
            this.mReceiver = receiver;
        }

        public void setOnBlueToothStateListener(BluetoothReceiver.OnBlueToothStateListener onBlueToothStateListener) {
            if (mReceiver instanceof BluetoothReceiver) {
                ((BluetoothReceiver) mReceiver).setOnBlueToothStateListener(onBlueToothStateListener);
            }
        }
    }
}
