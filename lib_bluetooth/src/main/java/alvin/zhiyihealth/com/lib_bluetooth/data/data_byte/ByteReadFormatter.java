package alvin.zhiyihealth.com.lib_bluetooth.data.data_byte;

import java.io.IOException;

import alvin.zhiyihealth.com.lib_bluetooth.data.adapter.ReadFormatterAdapter;

/**
 * Created by zouyifeng on 06/02/2018.
 * 11:35
 * <p>
 */

public class ByteReadFormatter extends ReadFormatterAdapter<byte[]> {


    @Override
    public void readFormat(byte[] data, int len) {

        if (len == -1) {
            return;
        }

        byte[] newData = new byte[len];
        System.arraycopy(data, 0, newData, 0, len);
        sendDataToListener(newData);
    }

    @Override
    public void clean() {
    }

    @Override
    public void close() throws IOException {

    }
}
