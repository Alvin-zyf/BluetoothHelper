package alvin.zhiyihealth.com.lib_bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by zouyifeng on 05/03/2018.
 * 16:11
 */

public class BluetoothPermission {
    private BluetoothPermission(){}

    /**
     * 检查蓝牙权限是否足够
     *
     * @param activity
     */
    public static void applySearchPermission(Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            int sdkInt = Build.VERSION.SDK_INT;
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 提示权限已经被禁用 且不在提示
                return;
            }

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
        }
    }

    /**
     * 让当前的蓝牙设备对外暴露
     *
     * @param context 将设备对外暴露需要申请，而申请需要打开一个activity弹窗
     */
    public static void applyOtherDeviceCanFindMine(Activity context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.getScanMode() !=
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                context.startActivity(discoverableIntent);
            }
        }
    }
}
