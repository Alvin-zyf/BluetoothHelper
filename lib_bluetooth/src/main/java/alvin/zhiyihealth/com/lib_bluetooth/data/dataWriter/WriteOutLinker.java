package alvin.zhiyihealth.com.lib_bluetooth.data.dataWriter;

/**
 * Created by zouyifeng on 27/02/2018.
 * 23:26
 */

public class WriteOutLinker {
    private WriteOutLinker() {
    }

    private DataWriter dataWriter;

    public static WriteOutLinker link(DataWriter dataWriter) {
        WriteOutLinker linker = new WriteOutLinker();

        linker.dataWriter = dataWriter;

        return linker;
    }

    public int writeData(Object data, long delayMillis, int id) {
        return dataWriter.writeOutData(data, delayMillis, id);
    }

    public int writeData(Object data, int id) {
        return dataWriter.writeOutData(data, 0, id);
    }
}
