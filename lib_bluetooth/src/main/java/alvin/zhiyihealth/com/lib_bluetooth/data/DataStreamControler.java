package alvin.zhiyihealth.com.lib_bluetooth.data;

import java.io.Closeable;

/**
 * Created by zouyifeng on 30/01/2018.
 * 16:24
 *
 * 流操控者
 */

public interface DataStreamControler extends Closeable{

    /**
     * 读取数据
     */
    void read();


    /**
     * 写出数据
     */
    void write();
}
