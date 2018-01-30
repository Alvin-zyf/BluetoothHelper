package alvin.zhiyihealth.com.lib_bluetooth.data.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import alvin.zhiyihealth.com.lib_bluetooth.data.WriteFormatterAdapter;

/**
 * Created by zouyifeng on 30/01/2018.
 * 19:18
 */

public class FileWriteFormater extends WriteFormatterAdapter<File> {

    @Override
    public InputStream writeFormat(File t) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(t);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return inputStream;
    }
}
