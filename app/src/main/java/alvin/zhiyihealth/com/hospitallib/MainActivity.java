package alvin.zhiyihealth.com.hospitallib;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import alvin.zhiyihealth.com.lib_bluetooth.BluetoothHelper;
import alvin.zhiyihealth.com.lib_bluetooth.DeviceBaseHelper;
import alvin.zhiyihealth.com.lib_bluetooth.Utils;
import alvin.zhiyihealth.com.lib_bluetooth.service.BluetoothHelperService;

public class MainActivity extends AppCompatActivity {

    private BluetoothHelper bluetoothHelper;
    private DeviceAdapter adapter;
    private RecyclerView mBlueList;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothHelper = ((BluetoothHelperService.BluetoothHelpBinder) service).getHelper();
            bluetoothHelper.addOnHelperListener(new BluetoothHelper.OnHelperListener() {
                @Override
                public void onSearchValueChange(ArrayList<BluetoothDevice> deviceList) {
                    if (adapter == null) {
                        adapter = new DeviceAdapter(deviceList, bluetoothHelper);
                        mBlueList.setAdapter(adapter);
                    } else {
                        adapter.setData(deviceList);
                    }
                }

                @Override
                public void isBluetoothOpen(boolean isOpen) {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, BluetoothHelperService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        mBlueList = findViewById(R.id.mBlueList);
        mBlueList.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.startServer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceBaseHelper deviceBaseHelper = new DeviceBaseHelper(null, DeviceBaseHelper.CONNECT_TYPE_SERVER_INPUT_OUTPUT) {
                    @Override
                    public void processData(byte[] data, int len) {
                        try {
                            Utils.logI(new String(data, 0, len, "utf-8"));
                        } catch (Exception e) {

                        }
                    }
                };

                bluetoothHelper.isFoundMind(MainActivity.this);
                bluetoothHelper.startBluetoothServer(deviceBaseHelper);
            }
        });

        findViewById(R.id.sendMsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothHelper.getDeviceHelper() != null) {
                    try {
                        bluetoothHelper.submitSmallData("你好啊".getBytes("utf-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermission();
                bluetoothHelper.openBluetooth(MainActivity.this);
                boolean a = bluetoothHelper.startScan();

            }
        });
    }

    static class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> implements View.OnClickListener {
        private ArrayList<BluetoothDevice> data;
        BluetoothHelper bluetoothHelper;

        public DeviceAdapter(ArrayList<BluetoothDevice> data, BluetoothHelper bluetoothHelper) {
            this.data = data;
            this.bluetoothHelper = bluetoothHelper;
        }

        public void setData(ArrayList<BluetoothDevice> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DeviceHolder(View.inflate(parent.getContext(), R.layout.item_device, null));
        }

        @Override
        public void onBindViewHolder(DeviceHolder holder, int position) {
            holder.mName.setText(data.get(position).getName());
            holder.mAddress.setText(data.get(position).getAddress());
            holder.mName.setOnClickListener(this);
            holder.mName.setTag(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            BluetoothDevice bluetoothDevice = data.get(position);

            DeviceBaseHelper deviceBaseHelper = new DeviceBaseHelper(bluetoothDevice, DeviceBaseHelper.CONNECT_TYPE_CLIENT_INPUT_OUTPUT) {
                @Override
                public void processData(byte[] data, int len) {
                    try {
                        Utils.logI(new String(data, 0, len, "utf-8"));
                    } catch (Exception e) {

                    }
                }
            };

            bluetoothHelper.connectDevice(deviceBaseHelper);
        }
    }

    static class DeviceHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final TextView mAddress;

        public DeviceHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.mName);
            mAddress = itemView.findViewById(R.id.mAddress);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothHelper.release();
        unbindService(connection);
    }

    final int REQUEST_PERMISSION_BT = 100;

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            int sdkInt = Build.VERSION.SDK_INT;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //TODO 提示权限已经被禁用 且不在提示
                return;
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_BT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_BT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO 请求权限成功
                    Utils.logI("request permission success");
                } else {
                    //TODO 提示权限已经被禁用

                    Utils.logI("request permission failed");

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
