package alvin.zhiyihealth.com.lib_bluetooth;

import android.util.Log;

/**
 * Created by zouyifeng on 08/12/2017.
 * 10:54
 */

public class Utils {
    private static final String TAG = "lib_bluetooth";

    static int LOG_ROOT;
    static final int LOG_D = 0x0000000F;
    static final int LOG_I = 0x000000F0;
    static final int LOG_E = 0x00000F00;

    public static void logD(String mag) {
        if ((LOG_ROOT & LOG_D) == LOG_D)
            Log.d(TAG, mag);
    }

    public static void logE(String mag) {
        if ((LOG_ROOT & LOG_E) == LOG_E)
            Log.e(TAG, mag);
    }

    public static void logI(String mag) {
        if ((LOG_ROOT & LOG_I) == LOG_I)
            Log.i(TAG, mag);
    }
}
