package alvin.zhiyihealth.com.lib_bluetooth.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import alvin.zhiyihealth.com.lib_bluetooth.BluetoothHelper;

public class BluetoothHelperService extends Service {

    private BluetoothHelper mHelper;

    public BluetoothHelperService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHelper = BluetoothHelper.init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHelper.release();
        mHelper = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new BluetoothHelpBinder(mHelper);
    }

    public static class BluetoothHelpBinder extends Binder {
        BluetoothHelper mHelper;

        BluetoothHelpBinder(BluetoothHelper mHelper) {
            this.mHelper = mHelper;
        }

        public BluetoothHelper getHelper() {
            return mHelper;
        }
    }
}
