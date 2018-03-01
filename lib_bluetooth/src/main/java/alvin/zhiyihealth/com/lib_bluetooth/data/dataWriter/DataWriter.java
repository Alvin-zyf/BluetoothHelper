package alvin.zhiyihealth.com.lib_bluetooth.data.dataWriter;

/**
 * Created by zouyifeng on 27/02/2018.
 * 23:15
 */

public interface DataWriter {

    /**
     * 写出数据
     * @param data 数据
     * @param delayMillis 延迟时间
     * @param id 写出数据ID
     * @return 写出状态
     */
    int writeOutData(Object data, long delayMillis, int id);
}
