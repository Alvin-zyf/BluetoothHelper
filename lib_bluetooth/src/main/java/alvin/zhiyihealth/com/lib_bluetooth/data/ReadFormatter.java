package alvin.zhiyihealth.com.lib_bluetooth.data;

import java.io.Closeable;

/**
 * Created by zouyifeng on 30/01/2018.
 * 16:25
 *
 * 数据转化器，写入写出时可通过当前接口拦截并做数据转换
 */

public interface ReadFormatter extends Closeable {

    /**
     *  传入data 和 len 进行数据转换
     *
     * @param data 数据源
     * @param len 有效长度, 如果为 len == -1 , data == null
     */
    void readFormat(byte[] data,int len);

    /**
     * 清空缓存数据
     */
    void clean();

}
