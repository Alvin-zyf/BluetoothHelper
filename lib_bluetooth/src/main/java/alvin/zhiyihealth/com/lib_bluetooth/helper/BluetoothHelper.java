package alvin.zhiyihealth.com.lib_bluetooth.helper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import alvin.zhiyihealth.com.lib_bluetooth.receiver.BluetoothReceiver;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;
import alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectBluetoothInput;
import alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectBluetoothOutputMQ;
import alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType;
import alvin.zhiyihealth.com.lib_bluetooth.utils.ConnectTypeUtil;

/**
 * Created by zouyifeng on 07/12/2017.
 * 15:14
 * <p>
 * 蓝牙设备接入帮助类
 */
@Deprecated
public final class BluetoothHelper {
    private static final int BLUETOOTH_REQUEST_CODE = 0x000000FF;

    private Context mContext;

    private BluetoothAdapter mBlueAdapter;

    private BaseDeviceHelper mBaseDeviceHelper;

    private ArrayList<BluetoothDevice> mDeviceList;

    /**
     * 线程池线程个数
     */
    private int threadCount = 2;

    /**
     * 蓝牙状态，搜索设备监听者
     */
    private BluetoothReceiver mReceiver;

    /**
     * 专门用于处于蓝牙连接的帮助类，继承与 Runnable
     * 通过 {@link #mThreadPool} 开启线程
     */
    private ConnectBluetoothInput mConnectInput;

    /**
     * 并发数为2的线程池
     */
    private final ExecutorService mThreadPool;

    private ConnectBluetoothOutputMQ mConnectOutput;

    /**
     * 初始化蓝牙帮助者
     * 注意在android 6.0 及以上 需要动态注册 ACCESS_COARSE_LOCATION 权限
     * <p>
     * 注意：本API只支持全局一个对象，如果创建多个帮助者进行蓝牙连接会出现
     * 意想不到的情况，当前创建的对象要在上下文的生命周期方法中及时释放掉，即上下文
     * 调用onDestroy生命周期方法时要调用{@link #release()}
     * <p>
     * 可以使用activity启动帮助者，也可使用service启动帮助者。
     * service有一个默认实现{@link alvin.zhiyihealth.com.lib_bluetooth.service.BluetoothHelperService}
     * 减少工作量
     *
     * @param context 当前上下文主要用于注册广播
     * @return 返回蓝牙帮助者
     */
    public static BluetoothHelper init(@NonNull Context context) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null) return null;

        return new BluetoothHelper(context, adapter);
    }

    private BluetoothHelper(Context context, BluetoothAdapter adapter) {
        //配置日志权限
        LogUtil.LOG_ROOT = LogUtil.LOG_D | LogUtil.LOG_E | LogUtil.LOG_I;


        mContext = context;
        mBlueAdapter = adapter;
        mDeviceList = new ArrayList<>();
        listeners = new ArrayList<>();
        mThreadPool = Executors.newFixedThreadPool(threadCount);


        registerReceiver();
    }

    /**
     * 注册广播接收者 接收蓝牙设备广播
     */
    private void registerReceiver() {
        if (mReceiver == null) {
            mReceiver = new BluetoothReceiver(mBlueToothListener);
            mReceiver.setOnBlueToothStateListener(mBlueToothListener);
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

    private BluetoothReceiver.OnBlueToothStateListener mBlueToothListener
            = new BluetoothReceiver.OnBlueToothStateListener() {
        @Override
        public void findDevice(BluetoothDevice scanDevice) {
            mDeviceList.add(scanDevice);

            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onSearchValueChange(mDeviceList);
            }

        }


        @Override
        public void bluetoothState(int state) {
            boolean isOpenBluetooth = false;

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    LogUtil.logI("bluetooth on");

                    isOpenBluetooth = true;
                    break;

                case BluetoothAdapter.STATE_OFF:
                    LogUtil.logI("bluetooth off");
                    closeThread();

                    isOpenBluetooth = false;
                    break;
            }

            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).isBluetoothOpen(isOpenBluetooth);
            }

        }

        @Override
        public void deviceDisconnect(BluetoothDevice scanDevice) {
//            if (mConnectInput != null) {
//                closeCurrentRunningInput();
//            }
        }
    };

    /**
     * 开启蓝牙，默认直接开启，如果个别设备无法直接开启，则弹窗开启。
     * 开启之前会将之前扫描的设备清楚
     *
     * @param context 如果mBlueAdapter.enable()不能打开蓝牙，需要用当前传入的activity进行跳转
     */
    public void openBluetooth(Activity context) {
        if (isRelease) return;

        mDeviceList.clear();
        if (mBlueAdapter.isEnabled()) return;

        boolean openState = mBlueAdapter.enable();

        /*
         * 蓝牙设备未打开需要打开蓝牙设备
         */
        if (!mBlueAdapter.isEnabled() && !openState) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(intent);
        }
    }

    /**
     * 关闭蓝牙搜索
     */
    public boolean cancelScan() {
        return isRelease || mBlueAdapter != null && mBlueAdapter.cancelDiscovery();

    }

    /**
     * 开始扫描附近设备
     *
     * @return 开启成功为true，并且会发现设备会回调  {@link #listeners}
     */
    public boolean startScan() {
        return !isRelease && mBlueAdapter != null && mBlueAdapter.startDiscovery();
    }

    /**
     * 让当前的蓝牙设备对外暴露
     *
     * @param context 将设备对外暴露需要申请，而申请需要打开一个activity弹窗
     */
    public void isFoundMind(Activity context) {
        if (isRelease) return;

        if (mBlueAdapter.isEnabled()) {
            if (mBlueAdapter.getScanMode() !=
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                context.startActivity(discoverableIntent);
            }
        }
    }

    /**
     * 获取已经匹配过的蓝牙设备，且转换成ArrayList方便遍历
     *
     * @return 已绑定过的蓝牙设备集合
     */
    public ArrayList<BluetoothDevice> getAreadyBondedDevice() {
        if (isRelease) return null;

        ArrayList<BluetoothDevice> devices = new ArrayList<>(mBlueAdapter.getBondedDevices().size());

        devices.addAll(mBlueAdapter.getBondedDevices());

        return devices;
    }

    /**
     * 以客户端的形式连接蓝牙服务器
     *
     * @param mDeviceHelper 设备帮助者
     * @return 连接蓝牙设备流程正常 返回true，出现问题返回false
     */
    public boolean connectDevice(BaseDeviceHelper mDeviceHelper) {
        if (isRelease) return false;

        if (!mBlueAdapter.isEnabled() || !ConnectTypeUtil.isClient(mDeviceHelper.getConnectType())) return false;

        /*
         * 首先检查当前是否线程正在连接蓝牙设备，如果有的情况下关闭当前连接设备。
         * 如果连接设备与传入设备相同，则直接跳出连接方法
         */

        if (mConnectInput != null && ConnectTypeUtil.isCurrentType(mDeviceHelper.getConnectType(), ConnectType.CLIENT_INPUT)) {
            //当连接设备没有改变时，不做任何改变
            if (mDeviceHelper.getDevice().equals(mConnectInput.getDeviceHelper().getDevice()) &&
                    mConnectInput.isConnect()) {
                LogUtil.logI("Current device already connect,new device is same with old device");
                return true;
            }

            if (!closeCurrentRunningInput()) {
                Toast.makeText(mContext, "切换蓝牙设备失败，请重新尝试", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (mConnectOutput != null && ConnectTypeUtil.isCurrentType(mDeviceHelper.getConnectType(), ConnectType.CLIENT_OUTPUT)) {
            if (mDeviceHelper.getDevice().equals(mConnectOutput.getDeviceHelper().getDevice()) &&
                    mConnectOutput.isConnect()) {
                LogUtil.logI("Current device already connect,new device is same with old device");
                return true;
            }
        }

        //进行配对
        mBaseDeviceHelper = mDeviceHelper;

        startStreamThread(mDeviceHelper);

        return true;
    }

    /**
     * 保存当前需要建立连接的设备，
     * 根据当前设备的连接类型创建新的数据读写线程。
     * 然后使用线程池执行线程任务
     */
    private void startStreamThread(BaseDeviceHelper mDeviceHelper) {

        if (ConnectTypeUtil.isInputType(mDeviceHelper.getConnectType())) {
            mConnectInput = new ConnectBluetoothInput(mDeviceHelper, mBlueAdapter);
            mConnectInput.setConnect(true);
            mThreadPool.execute(mConnectInput);
        }

        if (ConnectTypeUtil.isOutputType(mDeviceHelper.getConnectType())) {
            if (mConnectOutput == null) {
                mConnectOutput = new ConnectBluetoothOutputMQ(mDeviceHelper, mBlueAdapter);
                mThreadPool.execute(mConnectOutput);
            } else {
                mConnectOutput.setDeviceHelper(mDeviceHelper);
            }
        } else {
            if (mConnectOutput != null) {
                mConnectOutput.closeMQ();
                mConnectOutput = null;
            }
        }
    }

    /**
     * 启动蓝牙服务器等待连接
     *
     * @param mDeviceHelper 当前对象的主要作用是连接蓝牙设备与数据处理
     * @return 开启成功返回true
     */
    public boolean startBluetoothServer(BaseDeviceHelper mDeviceHelper) {
        if (isRelease) return false;

        if (!mBlueAdapter.isEnabled() || !ConnectTypeUtil.isServer(mDeviceHelper.getConnectType())) return false;

        if (mBaseDeviceHelper != null && ConnectTypeUtil.isInputType(mBaseDeviceHelper.getConnectType()))
            closeCurrentRunningInput();

        if (mBaseDeviceHelper != null && ConnectTypeUtil.isOutputType(mBaseDeviceHelper.getConnectType()))
            closeCurrentRunningOutput();

        mBaseDeviceHelper = mDeviceHelper;

        startStreamThread(mDeviceHelper);

        return true;
    }

    /**
     * 向蓝牙设备提交数据，以File文件为主
     *
     * @param data 数据源，{@link File} 通过File对象从本地获取数据
     */
    public void submitBigData(File data) {
        if (isRelease) return;

        if (mBaseDeviceHelper != null) {
            mBaseDeviceHelper.setBigData(data);
            mConnectOutput.submitData(false);
        }
    }

    /**
     * 向蓝牙设备提交数据，以byte数组为主
     *
     * @param data 数据源，直接提交byte数组
     */
    public void submitSmallData(byte[] data) {
        if (isRelease) return;

        if (mBaseDeviceHelper != null) {
            mBaseDeviceHelper.setSmallData(data);
            mConnectOutput.submitData(true);
        }
    }

    /**
     * 关闭当前运行的读取线程
     *
     * @return 如果关闭成功或者当前没有蓝牙设备连接返回 true
     */
    public boolean closeCurrentRunningInput() {
        boolean isSocketClose = false;

        try {
            if (mConnectInput != null) {
                mConnectInput.setConnect(false);
                mConnectInput.getDeviceHelper().closeSocket();
            }
            isSocketClose = true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("Close socket error");

            return false;
        } finally {
            if (!isSocketClose)
                if (mConnectInput != null && mConnectInput.getCurrentRunThread() != null) {
                    mConnectInput.getCurrentRunThread().interrupt();
                }
            mConnectInput = null;
        }

        return true;
    }

    /**
     * 关闭写出流
     */
    public void closeCurrentRunningOutput() {
        if (mConnectOutput != null)
            mConnectOutput.closeMQ();
        mConnectOutput = null;
    }

    public BaseDeviceHelper getDeviceHelper() {
        return mBaseDeviceHelper;
    }

    public BluetoothAdapter getmBlueAdapter() {
        return mBlueAdapter;
    }

    private boolean isRelease;

    /**
     * 需要在Activity的onDestroy中调用该方法
     */
    public void release() {
        if (isRelease) return;

        isRelease = true;

        mContext.unregisterReceiver(mReceiver);

        closeThread();

        mDeviceList.clear();

        mBlueAdapter.cancelDiscovery();

        mDeviceList = null;
        listeners = null;
        mContext = null;
        mBlueToothListener = null;
        mBaseDeviceHelper = null;
        mBlueAdapter = null;
    }

    /**
     * 关闭运行中的线程
     */
    public void closeThread() {
        closeCurrentRunningInput();
        closeCurrentRunningOutput();
        mThreadPool.shutdown();
    }

    /**
     * 主要用于监听蓝牙状态，以及返回数据处理，和动画处理
     */
    private ArrayList<OnHelperListener> listeners;

    public void addOnHelperListener(OnHelperListener onHelperListener) {
        if (isRelease) return;

        listeners.add(onHelperListener);
    }

    public void removeOnHelperListener(OnHelperListener onHelperListener) {
        if (isRelease) return;

        listeners.remove(onHelperListener);
    }

    public interface OnHelperListener {
        void onSearchValueChange(ArrayList<BluetoothDevice> deviceList);

        void isBluetoothOpen(boolean isOpen);
    }

}
