package alvin.zhiyihealth.com.lib_bluetooth.data.string;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import alvin.zhiyihealth.com.lib_bluetooth.data.adapter.ReadFormatterAdapter;
import alvin.zhiyihealth.com.lib_bluetooth.listener.ReadDataListener;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

/**
 * Created by zouyifeng on 30/01/2018.
 * 17:31
 * <p>
 * 字符串数据转化器
 */

public class StringReadFormater extends ReadFormatterAdapter<String>  {

    /**
     * 编码格式
     */
    private String charsetName;

    /**
     * 字节输出流，将数据临时写入内存
     */
    private ByteArrayOutputStream byteStream;

    public StringReadFormater(String charsetName) {
        this.charsetName = charsetName;
    }

    public StringReadFormater(String charsetName, ReadDataListener<String> listener) {
        this.charsetName = charsetName;
        this.listener = listener;
    }

    /**
     * 将数据转转换成字符串
     *
     * @param data 数据源
     * @param len  有效长度, 如果为 len == -1 , data == null
     */
    @Override
    public void readFormat(byte[] data, int len) {
        if (byteStream == null)
            byteStream = new ByteArrayOutputStream();

        if (len == -1) {

            try {
                sendDataToListener(byteStream.toString(charsetName));
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.logE(e.getLocalizedMessage());
            }

            clean();

        } else {
            byteStream.write(data, 0, len);
        }
    }

    @Override
    public void clean() {
        if (byteStream != null)
            byteStream.reset();
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    public void close() throws IOException {
        if (byteStream != null) byteStream.close();

        byteStream = null;
    }
}
