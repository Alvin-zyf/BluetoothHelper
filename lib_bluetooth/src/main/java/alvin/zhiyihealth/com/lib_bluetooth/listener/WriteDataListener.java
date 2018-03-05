package alvin.zhiyihealth.com.lib_bluetooth.listener;

/**
 * Created by zouyifeng on 30/01/2018.
 * 16:50
 * <p>
 * 写出数据的回调监听
 */

public interface WriteDataListener extends DataListener {

    /**
     * 写出成功调用的方法
     *
     * @param id 写出数据的标示
     */
    void success(int id);

    /**
     * 当前写出进度
     *
     * @param id         写出数据标示
     * @param total      数据总量
     * @param currentPro 当前写出量
     */
    void progress(int id, long total, long currentPro);

    /**
     * 是否调用进度方法
     * @return false 不可调用 ， 反之可调用
     */
    boolean enableProgress();

    /**
     * 写出失败调用的方法
     *
     * @param id 写出的失败标示
     */
    void failed(int id);
}
