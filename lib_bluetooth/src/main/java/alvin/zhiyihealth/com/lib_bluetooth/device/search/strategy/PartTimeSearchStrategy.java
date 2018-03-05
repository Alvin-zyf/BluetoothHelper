package alvin.zhiyihealth.com.lib_bluetooth.device.search.strategy;

import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;

import java.util.ArrayList;

/**
 * Created by zouyifeng on 05/03/2018.
 * 10:31
 *
 * 时间段搜索策略
 * 根据 {@link #partTime} 进行限时搜索
 * 避免频繁返回
 */

public class PartTimeSearchStrategy implements SearchStrategy {

    /**
     * 蓝牙设备集合
     */
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    /**
     * 单次搜索时间
     */
    private long partTime = 3000;

    private long time;

    @Override
    public void strategy(BluetoothDevice device) {
        if (devices.size() == 0) {
            time = SystemClock.currentThreadTimeMillis();
        }

        devices.add(device);

        boolean isTimeOut = SystemClock.currentThreadTimeMillis() - time > partTime;

        if (isTimeOut) {
            ArrayList<BluetoothDevice> clone = (ArrayList<BluetoothDevice>) devices.clone();
            devices.clear();

            if (sl != null) {
                sl.onSearch(clone);
            }
        }
    }

    /**
     * 设置搜索时间 单位毫秒
     * <p>
     * 举个例子 假设设置3000毫秒，搜索每3000返回一次
     *
     * @param partTime
     */
    public void setPartTime(long partTime) {
        this.partTime = partTime;
    }

    private OnSearchListener sl;

    public void setSl(OnSearchListener sl) {
        this.sl = sl;
    }

    public interface OnSearchListener extends SearchStrategy.OnSearchListener<ArrayList<BluetoothDevice>> {
    }
}
