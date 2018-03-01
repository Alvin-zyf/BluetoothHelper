package alvin.zhiyihealth.com.lib_bluetooth.data.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import alvin.zhiyihealth.com.lib_bluetooth.data.adapter.WriteFormatterAdapter;

/**
 * Created by zouyifeng on 30/01/2018.
 * 19:18
 */

public class FileWriteFormatter extends WriteFormatterAdapter<File> {

    @Override
    public InputStream writeFormat(File t) throws IOException {
        return new FileInputStream(t);
    }

    @Override
    public long sizeOf(File file) {
        return file.length();
    }
}
