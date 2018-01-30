package alvin.zhiyihealth.com.lib_bluetooth.listener;

/**
 * Created by zouyifeng on 30/01/2018.
 * 16:50
 *
 * 写出数据的回调监听
 */

public interface WriteDataListener {

    /**
     * 写出成功调用的方法
     */
    void success();

    /**
     * 写出失败调用的方法
     */
    void failed();
}
