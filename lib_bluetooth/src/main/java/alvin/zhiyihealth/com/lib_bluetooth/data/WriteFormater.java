package alvin.zhiyihealth.com.lib_bluetooth.data;

import java.io.InputStream;

/**
 * Created by zouyifeng on 30/01/2018.
 * 16:45
 */

public interface WriteFormater<D> {

    /**
     * t 为数据源，将数据源转换成流之后返回
     * @param t 数据源
     * @return 带有数据的输入流
     */
    InputStream writeFormat(D t);
}
