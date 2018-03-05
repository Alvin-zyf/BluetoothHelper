package alvin.zhiyihealth.com.lib_bluetooth.connect.contorl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy.ConnectStrategy;
import alvin.zhiyihealth.com.lib_bluetooth.data.dataWriter.DataWriter;
import alvin.zhiyihealth.com.lib_bluetooth.data.dataWriter.WriteOutLinker;
import alvin.zhiyihealth.com.lib_bluetooth.device.DeviceManager;
import alvin.zhiyihealth.com.lib_bluetooth.utils.ConnectTypeUtil;

/**
 * Created by zouyifeng on 09/02/2018.
 * 00:47
 */

public class ConnectControllerImpl implements ConnectController {


    private ExecutorService mThreadPool;
    private final int threadCount = 2;

    /**
     * 读取策略
     */
    private ConnectStrategy mInputStrategy;

    /**
     * 写出策略
     */
    private ConnectStrategy mOutputStrategy;

    private DeviceManager mCurrentDM;

    private ConnectControllerImpl() {

    }

    private void init() {
        mThreadPool = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public void connect(DeviceManager device) {
        if (device == null)
            throw new RuntimeException("device is null");

        final int connectType = device.getConnectType();

        if (!ConnectTypeUtil.isConnectType(connectType))
            throw new RuntimeException("device ConnectType is wrong");

        if (ConnectTypeUtil.isClient(connectType) &&
                ConnectTypeUtil.isServer(connectType)
                )
            throw new RuntimeException("The device do not have Client and Server both");

        if (isSame(device)) return;


        //根据Type创建进行线程进行读写 1.可读可写 2.只可读 3.只可写
        if (ConnectTypeUtil.isInputType(connectType)) {
            mInputStrategy.connect(mThreadPool, device);
        }
        if (ConnectTypeUtil.isOutputType(connectType)) {
            mOutputStrategy.connect(mThreadPool, device);
        }
    }

    /**
     * 当前的设备管理者 是否和 传入的设备管理者一致
     *
     * @return true 一致，否则false
     */
    private boolean isSame(DeviceManager device) {
        if (mCurrentDM.equals(device)) {

            switch (device.getDeviceConnector().getState()) {
                case CONNECTED:
                case WATIE:
                    return true;

                case UNCONNECTED:
                    device.getDeviceConnector().reset();
                    return false;
            }

        }

        mCurrentDM = device;
        return false;
    }

    public WriteOutLinker getWriteLinker() {
        if (mOutputStrategy instanceof DataWriter) {
            return WriteOutLinker.link((DataWriter) mOutputStrategy);
        }

        return null;
    }

    @Override
    public void disConnect() {
        mInputStrategy.disConnect();
        mOutputStrategy.disConnect();
    }

    @Override
    public void release() {
        disConnect();

        mThreadPool.shutdownNow();

        mThreadPool = null;
    }


    public final static class Builder {
        Params P;

        public Builder() {
            P = new Params();
        }

        public Builder boundInputThread(ConnectStrategy inputStrategy) {
            P.inputStrategy = inputStrategy;

            return this;
        }

        public Builder boundOutputThread(ConnectStrategy outputStrategy) {
            P.outputStrategy = outputStrategy;

            return this;
        }

        public ConnectControllerImpl build() {
            ConnectControllerImpl connectController = new ConnectControllerImpl();

            apply(connectController);

            connectController.init();

            return connectController;
        }

        private void apply(ConnectControllerImpl connectController) {
            connectController.mInputStrategy = P.inputStrategy;
            connectController.mOutputStrategy = P.outputStrategy;
        }
    }

    private static class Params {
        ConnectStrategy inputStrategy;

        ConnectStrategy outputStrategy;
    }
}
