package alvin.zhiyihealth.com.lib_bluetooth.device.search;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import alvin.zhiyihealth.com.lib_bluetooth.BluetoothPermission;
import alvin.zhiyihealth.com.lib_bluetooth.device.search.strategy.SearchStrategy;
import alvin.zhiyihealth.com.lib_bluetooth.device.search.strategy.UsualSearchStrategy;
import alvin.zhiyihealth.com.lib_bluetooth.receiver.BluetoothReceiver;
import alvin.zhiyihealth.com.lib_bluetooth.service.BluetoothSearchService;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

/**
 * Created by zouyifeng on 01/03/2018.
 * 16:59
 */

public class BluetoothSearcherImpl implements BluetoothSearcher {

    /**
     * 搜素策略
     */
    private SearchStrategy mStrategy;

    private SearchStrategy.OnSearchListener sl;

    /**
     * 上下文 用于启动广播 与 服务
     */
    private Context mContext;

    /**
     * 蓝牙适配器，用于搜索蓝牙设备
     */
    private BluetoothAdapter mBlueAdapter;

    /**
     * 广播接受者，配合适配器获取附近蓝牙设备
     */
    private BroadcastReceiver mReceiver;

    /**
     * 是否启用后台服务，使广播接受者的生命周期更加长久
     */
    private boolean mServiceEnabled;

    private BluetoothStateListener mBlueToothListener;

    /**
     * service连接信息
     */
    private static BluetoothServiceConnection conn;

    private BluetoothSearcherImpl() {
    }

    /**
     * 获取蓝牙搜索者
     *
     * @param context 用于打开广播接受者  或者是 服务
     */
    public static BluetoothSearcherImpl from(Context context) {
        BluetoothSearcherImpl searcher = new BluetoothSearcherImpl();
        searcher.mContext = context;
        searcher.mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        searcher.mBlueToothListener = new BluetoothStateListener();
        searcher.mStrategy = new UsualSearchStrategy();
        searcher.setSearchStrategy(searcher.mStrategy);

        LogUtil.logD("create a searcher of bluetooth ");

        return searcher;
    }

    /**
     * 设置策略 {@link SearchStrategy}
     */
    public BluetoothSearcherImpl setSearchStrategy(SearchStrategy strategy) {
        if (strategy == null) return this;

        mStrategy = strategy;
        mBlueToothListener.setStrategy(strategy);
        return this;
    }

    /**
     * 是否启用后台服务长久启动广播
     *
     * @param enabled true 是
     */
    public BluetoothSearcherImpl serviceEnabled(boolean enabled) {
        mServiceEnabled = enabled;
        return this;
    }

    public BluetoothSearcherImpl setOnSearchListener(SearchStrategy.OnSearchListener sl) {
        return this;
    }

    @Override
    public void launch() {
        LogUtil.logD("launch start");

        if (mContext instanceof Activity) {
            BluetoothPermission.applySearchPermission((Activity) mContext, 0x0000FFFF);
        }

        boolean openState = mBlueAdapter.enable();

        /*
         * 蓝牙设备未打开需要打开蓝牙设备
         */
        if (!mBlueAdapter.isEnabled() && !openState) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivity(intent);
        }

        if (mServiceEnabled) {
            startService();
        } else {
            registerReceiver();
        }
    }

    /**
     * 注册广播接收者 接收蓝牙设备广播
     */
    private void registerReceiver() {
        LogUtil.logD("register receiver");

        if (mReceiver == null) {
            mReceiver = new BluetoothReceiver(mBlueToothListener);
            IntentFilter filter = new IntentFilter();
            //发现设备
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            //蓝牙设备连接状态发生改变时调用
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            //蓝牙状态发生改变
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            mContext.registerReceiver(mReceiver, filter);
        }
    }

    /**
     * 启动服务，在服务中启动广播接受者
     */
    private void startService() {
        LogUtil.logD("start service");

        //需要以绑定服务的形式启动服务，获取IBinder
        if (conn == null || !conn.isConnect()) {
            Intent intent = new Intent(mContext, BluetoothSearchService.class);
            conn = new BluetoothServiceConnection();
            mContext.bindService(intent, conn, Service.BIND_AUTO_CREATE);
        }

        conn.setBlueToothListener(mBlueToothListener);
    }

    @Override
    public void cease() {
        if (mServiceEnabled) {
            if (conn != null)
                mContext.unbindService(conn);

        } else {
            if (mReceiver != null)
                mContext.unregisterReceiver(mReceiver);
        }

        stopScan();
    }

    /**
     * 断开搜索服务
     *
     * @param context
     */
    public static void stopSearchService(Context context) {
        if (conn != null && conn.isConnect())
            context.unbindService(conn);
    }

    @Override
    public void startScan() {
        LogUtil.logD("start scan");
        mBlueAdapter.startDiscovery();
    }

    @Override
    public void stopScan() {
        mBlueAdapter.cancelDiscovery();
    }


    private static class BluetoothServiceConnection implements ServiceConnection {
        private BluetoothSearchService.IBinderImpl binder;

        /**
         * 蓝牙状态监听者
         */
        private BluetoothReceiver.OnBlueToothStateListener mBlueToothListener;

        /**
         * 当前服务连接者是否连接服务
         * true连接
         */
        private boolean isConnect;

        public void setBlueToothListener(BluetoothReceiver.OnBlueToothStateListener blueToothListener) {
            if (binder != null)
                binder.setOnBlueToothStateListener(blueToothListener);
            else
                this.mBlueToothListener = blueToothListener;
        }

        public boolean isConnect() {
            return isConnect;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isConnect = true;
            if (service != null && service instanceof BluetoothSearchService.IBinderImpl) {
                binder = (BluetoothSearchService.IBinderImpl) service;
                ((BluetoothSearchService.IBinderImpl) service).setOnBlueToothStateListener(mBlueToothListener);
                mBlueToothListener = null;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnect = false;
        }
    }


    private static class BluetoothStateListener implements BluetoothReceiver.OnBlueToothStateListener {
        /**
         * 搜索结果处理策略
         */
        private SearchStrategy mStrategy;

        public SearchStrategy getStrategy() {
            return mStrategy;
        }

        public void setStrategy(SearchStrategy strategy) {
            this.mStrategy = strategy;
        }

        @Override
        public void findDevice(BluetoothDevice scanDevice) {
            mStrategy.strategy(scanDevice);
        }

        @Override
        public void bluetoothState(int state) {

        }

        @Override
        public void deviceDisconnect(BluetoothDevice scanDevice) {

        }

    }

}
