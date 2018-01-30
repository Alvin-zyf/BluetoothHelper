package alvin.zhiyihealth.com.lib_bluetooth.data;

import alvin.zhiyihealth.com.lib_bluetooth.listener.InstallDataListen;
import alvin.zhiyihealth.com.lib_bluetooth.listener.WriteDataListener;

/**
 * Created by zouyifeng on 30/01/2018.
 * 19:27
 */

public abstract class WriteFormatterAdapter<T> implements WriteFormatter<T>,InstallDataListen<WriteDataListener> {

    /**
     * 读取数据监听
     */
    protected WriteDataListener listener;

    @Override
    public void setDataListener(WriteDataListener listener) {
        this.listener = listener;
    }

    @Override
    public WriteDataListener getDataListener() {
        return listener;
    }
}
