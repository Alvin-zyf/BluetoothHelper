package alvin.zhiyihealth.com.lib_bluetooth.data.string;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import alvin.zhiyihealth.com.lib_bluetooth.data.adapter.WriteFormatterAdapter;
import alvin.zhiyihealth.com.lib_bluetooth.listener.WriteDataListener;

/**
 * Created by zouyifeng on 30/01/2018.
 * 19:03
 */

public class StringWriteFormater extends WriteFormatterAdapter<String> {
    /**
     * 编码格式
     */
    private String charsetName;

    public StringWriteFormater(String charsetName) {
        this.charsetName = charsetName;
    }

    public StringWriteFormater(String charsetName, WriteDataListener listener) {
        this.charsetName = charsetName;
        this.listener = listener;
    }

    @Override
    public InputStream writeFormat(String t) throws IOException {
        return new ByteArrayInputStream(charsetName == null ? t.getBytes(charsetName) : t.getBytes());
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    public long sizeOf(String s) {
        return s.getBytes().length;
    }
}
