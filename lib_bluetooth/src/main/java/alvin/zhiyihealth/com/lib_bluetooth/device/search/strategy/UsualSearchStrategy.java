package alvin.zhiyihealth.com.lib_bluetooth.device.search.strategy;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * Created by zouyifeng on 05/03/2018.
 * 10:52
 * <p>
 * 普通搜索策略 不做任何处理
 */

public class UsualSearchStrategy implements SearchStrategy {
    /**
     * 蓝牙设备集合
     */
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    @Override
    public void strategy(BluetoothDevice device) {
        devices.add(device);

        if (sl != null) {
            sl.onSearch(devices);
        }
    }


    private OnSearchListener sl;

    public void setOnSearchListener(OnSearchListener sl) {
        this.sl = sl;
    }

    public interface OnSearchListener extends SearchStrategy.OnSearchListener<ArrayList<BluetoothDevice>> {
    }
}
