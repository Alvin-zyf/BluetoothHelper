package alvin.zhiyihealth.com.lib_bluetooth.device.search.strategy;

import android.bluetooth.BluetoothDevice;

/**
 * Created by zouyifeng on 01/03/2018.
 * 16:54
 *
 * 搜索结果处理策略
 */

public interface SearchStrategy {

    /**
     * 每当搜索一个蓝牙设备时回调用此方法，会出现重复数据，在此方法中可以调用
     * {@link OnSearchListener} 中的 onSearch方法回调给用户
     * 此方法存在被频繁调用的可能
     *
     * 此策略最初目的使为了过滤和整理数据
     * @param device
     */
    void strategy(BluetoothDevice device);

    /**
     * 搜索监听器
     * @param <D> 数据类型
     */
    interface OnSearchListener<D>{

        /**
         * 搜索到数据的回调方法
         * @param t
         */
        void onSearch(D t);
    }
}
