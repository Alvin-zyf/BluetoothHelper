package alvin.zhiyihealth.com.lib_bluetooth.data.string;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import alvin.zhiyihealth.com.lib_bluetooth.data.adapter.WriteFormatterAdapter;
import alvin.zhiyihealth.com.lib_bluetooth.listener.WriteDataListener;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

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
    public InputStream writeFormat(String t) {
        InputStream input = null;

        try {

            input = new ByteArrayInputStream(charsetName == null ? t.getBytes(charsetName) : t.getBytes());

        } catch (Exception e) {
            LogUtil.logE(e.getLocalizedMessage());
            e.printStackTrace();
        }

        return input;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

}
