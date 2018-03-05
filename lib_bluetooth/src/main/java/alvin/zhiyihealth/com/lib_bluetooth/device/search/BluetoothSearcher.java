package alvin.zhiyihealth.com.lib_bluetooth.device.search;

/**
 * Created by alvin on 01/03/2018.
 * 16:52
 * <p>
 * 蓝牙搜索器
 */

public interface BluetoothSearcher {

    /**
     * 启动蓝牙搜索者
     */
    void launch();

    /**
     * 停止蓝牙搜索者
     */
    void cease();

    /**
     * 开始搜索
     */
    void startScan();

    /**
     * 停止搜索
     */
    void stopScan();
}
