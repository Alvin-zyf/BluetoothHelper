package alvin.zhiyihealth.com.lib_bluetooth.listener;

/**
 * Created by zouyifeng on 30/01/2018.
 * 16:50
 *
 * 读取数据回调监听
 */

public interface ReadDataListener<D> extends DataListener {

    /**
     * 此方法被调用时，代表有数据传入
     * @param d 数据
     */
    void produceData(D d);
}
