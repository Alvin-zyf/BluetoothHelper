package alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy;

import android.bluetooth.BluetoothSocket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

import alvin.zhiyihealth.com.lib_bluetooth.data.ReadFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.device.DeviceManager;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

/**
 * Created by zouyifeng on 09/02/2018.
 * 22:06
 */

public class InputStrategy implements ConnectStrategy {
    private ConnectThread currentThread;

    @Override
    public void disConnect() {
        synchronized (this) {
            if (currentThread != null && currentThread.currentThreadState()) {
                currentThread.disConnect();
            }

            currentThread = null;
        }
    }

    /**
     * @param executor
     * @param deviceManager
     */
    @Override
    public void connect(Executor executor, DeviceManager deviceManager) {
        synchronized (this) {
            LogUtil.logD("connect start");

            if (currentThread != null) {
                disConnect();
            }

            currentThread = new InputThread();
            currentThread.boundDeviceManager(deviceManager);

            executor.execute(currentThread);
        }
    }

    public InputStrategy() {
    }

    private static class InputThread extends PeriodConnectThread {

        @Override
        public void onConnected(BluetoothSocket socket) throws IOException {
            InputStream in = socket.getInputStream();
            BufferedInputStream bufferIn = new BufferedInputStream(in);

            inputData(bufferIn);
        }

        private void inputData(BufferedInputStream in) throws IOException {

            ReadFormatter mReadFormatter = getDeviceManager().getReadFormatter();
            byte[] data = new byte[1024];
            int len;
            //获取数据处理数据
            while ((len = in.read(data)) != -1) {
                LogUtil.logI("read again");
                mReadFormatter.readFormat(data, len);
            }

            mReadFormatter.readFormat(null, -1);
        }

        @Override
        public void disConnect() {
            super.disConnect();

            if (getDeviceManager() != null) {
                try {
                    getDeviceManager().getReadFormatter().close();
                } catch (IOException e) {
                    LogUtil.logE(e.getLocalizedMessage());
                }
            }
        }

    }
}
