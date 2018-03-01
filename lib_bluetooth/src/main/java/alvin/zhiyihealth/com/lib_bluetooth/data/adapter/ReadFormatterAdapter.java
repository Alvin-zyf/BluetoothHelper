package alvin.zhiyihealth.com.lib_bluetooth.data.adapter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import alvin.zhiyihealth.com.lib_bluetooth.data.ReadFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.listener.InstallDataListen;
import alvin.zhiyihealth.com.lib_bluetooth.listener.ReadDataListener;

/**
 * Created by zouyifeng on 30/01/2018.
 * 19:27
 */

public abstract class ReadFormatterAdapter<T> implements ReadFormatter, InstallDataListen<ReadDataListener<T>> {

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            listener.produceData((T) msg.obj);
        }
    };

    /**
     * 读取数据监听
     */
    protected ReadDataListener<T> listener;

    @Override
    public void setDataListener(ReadDataListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public ReadDataListener<T> getDataListener() {
        return listener;
    }

    /**
     * 将数据转移到UI线程进行操作
     * @param t
     */
    protected void sendDataToListener(T t) {
        if (listener == null) return;

        Message obtain = Message.obtain(mHandler);
        obtain.obj = t;
        obtain.sendToTarget();
    }
}
