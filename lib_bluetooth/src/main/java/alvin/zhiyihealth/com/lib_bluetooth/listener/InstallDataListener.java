package alvin.zhiyihealth.com.lib_bluetooth.listener;

/**
 * Created by zouyifeng on 30/01/2018.
 * 19:40
 */

public interface InstallDataListener<T extends DataListener> {

    /**
     * 设置数据监听器
     *
     * @param listener
     */
    void setDataListener(T listener);

    /**
     * 获取数据监听器
     *
     * @return
     */
    T getDataListener();
}
