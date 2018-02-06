package alvin.zhiyihealth.com.lib_bluetooth.data.adapter;

import alvin.zhiyihealth.com.lib_bluetooth.data.ReadFormatter;
import alvin.zhiyihealth.com.lib_bluetooth.listener.InstallDataListen;
import alvin.zhiyihealth.com.lib_bluetooth.listener.ReadDataListener;

/**
 * Created by zouyifeng on 30/01/2018.
 * 19:27
 */

public abstract class ReadFormatterAdapter<T> implements ReadFormatter,InstallDataListen<ReadDataListener<T>> {

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
}
