package alvin.zhiyihealth.com.lib_bluetooth.data.data_byte;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import alvin.zhiyihealth.com.lib_bluetooth.data.adapter.ReadFormatterAdapter;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

/**
 * Created by zouyifeng on 06/02/2018.
 * 11:35
 * <p>
 */

public class ByteReadFormatter extends ReadFormatterAdapter<byte[]> {

    private ByteArrayOutputStream stream;

    @Override
    public void readFormat(byte[] data, int len) {

        if (len == -1) {

            sendDataToListener(stream.toByteArray());

            stream.reset();

        } else {
            if (stream == null)
                stream = new ByteArrayOutputStream();

            stream.write(data, 0, len);
        }
    }

    @Override
    public void clean() {
        if (stream == null) return;

        stream.reset();
    }

    @Override
    public void close() throws IOException {
        if (stream == null) return;

        try {
            stream.close();
            stream = null;
        } catch (IOException e) {
            LogUtil.logE(e.getLocalizedMessage());
        }
    }
}
