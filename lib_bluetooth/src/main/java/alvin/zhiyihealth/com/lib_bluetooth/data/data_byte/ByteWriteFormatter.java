package alvin.zhiyihealth.com.lib_bluetooth.data.data_byte;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import alvin.zhiyihealth.com.lib_bluetooth.data.adapter.WriteFormatterAdapter;

/**
 * Created by zouyifeng on 06/02/2018.
 * 11:36
 * <p>
 */

public class ByteWriteFormatter extends WriteFormatterAdapter<byte[]> {

    @Override
    public InputStream writeFormat(byte[] t) throws IOException {
        return new ByteArrayInputStream(t);
    }

    @Override
    public long sizeOf(byte[] bytes) {
        return bytes.length;
    }
}
