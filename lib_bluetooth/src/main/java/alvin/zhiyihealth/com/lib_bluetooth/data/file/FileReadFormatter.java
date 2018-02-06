package alvin.zhiyihealth.com.lib_bluetooth.data.file;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import alvin.zhiyihealth.com.lib_bluetooth.data.adapter.ReadFormatterAdapter;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

/**
 * Created by zouyifeng on 30/01/2018.
 * 19:17
 * <p>
 */

public class FileReadFormatter extends ReadFormatterAdapter<File> implements Closeable {


    public FileReadFormatter(FileNameFactory fileNameFactory, String dirPath) {
        this(fileNameFactory, dirPath, "");
    }

    public FileReadFormatter(FileNameFactory fileNameFactory, String dirPath, String nameSuffix) {
        this.nameSuffix = nameSuffix;
        this.fileNameFactory = fileNameFactory;

        dir = new File(dirPath);

        if (dir.exists() && dir.isDirectory()) {

        } else {
            dir.mkdirs();
        }
    }

    /**
     * 后缀名
     */
    private String nameSuffix;

    /**
     * 文件名生成器
     */
    private FileNameFactory fileNameFactory;


    /**
     * 文件目录路径
     */
    private File dir;

    /**
     * 当前正在操作的文件
     */
    private File currentFile;

    /**
     * 文件输出流对象
     */
    private FileOutputStream outputStream;

    @Override
    public void readFormat(byte[] data, int len) {

        if (len == -1) {
            finishedWrite();

        } else {
            createAndWriteFile(data, len);
        }
    }

    /**
     * 文件写出完毕
     */
    private void finishedWrite() {
        try {
            outputStream.flush();

            if (listener != null) {
                listener.produceData(currentFile);
                currentFile = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.logE(e.getLocalizedMessage());
            close();
        }
    }

    /**
     * 创建与写文件
     */
    private void createAndWriteFile(byte[] data, int len) {
        try {
            if (outputStream == null) {
                String fileName = fileNameFactory.createName() +
                        '.' +
                        nameSuffix;

                currentFile = new File(dir, fileName);

                if (!currentFile.mkdir()) currentFile.mkdirs();

                outputStream = new FileOutputStream(currentFile);
            }

            outputStream.write(data, 0, len);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.logE(e.getLocalizedMessage());

            outputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.logE(e.getLocalizedMessage());
            close();
        }
    }

    @Override
    public void clean() {
        close();
        outputStream = null;
    }

    @Override
    public void close() {
        if (outputStream == null) return;

        try {
            outputStream.close();
        } catch (IOException e) {
            LogUtil.logE(e.getLocalizedMessage());
        }
    }

    /**
     * 文件名字生成器
     */
    public abstract static class FileNameFactory {

        public abstract String createName();
    }

    /**
     * 将时间戳作为名字
     */
    public static class TimeMillisNameFactory extends FileNameFactory {

        @Override
        public String createName() {
            return String.valueOf(System.currentTimeMillis());
        }
    }

}
