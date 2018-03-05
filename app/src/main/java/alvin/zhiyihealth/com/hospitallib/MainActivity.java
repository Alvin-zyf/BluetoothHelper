package alvin.zhiyihealth.com.hospitallib;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import alvin.zhiyihealth.com.lib_bluetooth.BluetoothPermission;
import alvin.zhiyihealth.com.lib_bluetooth.connect.ConnectType;
import alvin.zhiyihealth.com.lib_bluetooth.connect.contorl.ConnectController;
import alvin.zhiyihealth.com.lib_bluetooth.connect.contorl.ConnectControllerImpl;
import alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy.InputStrategy;
import alvin.zhiyihealth.com.lib_bluetooth.connect.link_strategy.OutputStrategy;
import alvin.zhiyihealth.com.lib_bluetooth.data.dataWriter.WriteOutLinker;
import alvin.zhiyihealth.com.lib_bluetooth.data.string.StringReadFormater;
import alvin.zhiyihealth.com.lib_bluetooth.data.string.StringWriteFormater;
import alvin.zhiyihealth.com.lib_bluetooth.device.DeviceManagerImpl;
import alvin.zhiyihealth.com.lib_bluetooth.device.search.BluetoothSearcherImpl;
import alvin.zhiyihealth.com.lib_bluetooth.device.search.strategy.PartTimeSearchStrategy;
import alvin.zhiyihealth.com.lib_bluetooth.listener.ReadDataListener;
import alvin.zhiyihealth.com.lib_bluetooth.listener.WriteDataListener;
import alvin.zhiyihealth.com.lib_bluetooth.utils.LogUtil;

public class MainActivity extends AppCompatActivity {

    private ConnectControllerImpl controller;
    private BluetoothSearcherImpl searcher;
    private DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //查找ID
        RecyclerView mBlueList = findViewById(R.id.mBlueList);
        Button mSearch = findViewById(R.id.search);
        Button mStartServer = findViewById(R.id.startServer);
        Button mSendMsg = findViewById(R.id.sendMsg);

        /***************************蓝牙框架核心调用***********************************/

        //创建蓝牙连接控制者
        controller = new ConnectControllerImpl
                .Builder()
                .boundInputThread(new InputStrategy())
                .boundOutputThread(new OutputStrategy())
                .build();

        //创建蓝牙搜索结果监听器
        PartTimeSearchStrategy.OnSearchListener sl = new PartTimeSearchStrategy.OnSearchListener() {
            @Override
            public void onSearch(ArrayList<BluetoothDevice> t) {
                adapter.getData().addAll(t);
                adapter.notifyDataSetChanged();
            }
        };

        //创建蓝牙搜索结果处理策略
        PartTimeSearchStrategy searchStrategy = new PartTimeSearchStrategy();
        searchStrategy.setOnSearchListener(sl);

        //创建蓝牙设备搜索器
        searcher = BluetoothSearcherImpl
                .from(this)
                .setSearchStrategy(searchStrategy);


        //启用搜索器
        BluetoothPermission.applySearchPermission(this, REQUEST_PERMISSION_BT);

        searcher.launch();

        LogUtil.logD("prepare finished");

        /***************************蓝牙框架核心调用***********************************/

        //初始化控件
        mBlueList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceAdapter(controller);
        mBlueList.setAdapter(adapter);

        /********开始搜索*******/
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * startScan() 开始搜索
                * stopScan() 停止搜索
                * */
                searcher.startScan();
            }
        });

        /********以自身为蓝牙服务器启动*********/
        mStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothPermission.applyOtherDeviceCanFindMine(MainActivity.this);

                /*
                * 创建设备管理者
                * 将连接模式修改为 ConnectType.SERVER_INPUT_OUTPUT
                * */
                DeviceManagerImpl deviceManager = new DeviceManagerImpl
                        .Builder()
                        .setConnectType(ConnectType.SERVER_INPUT_OUTPUT)
                        .setReadFormatter(new StringReadFormater("utf-8"))
                        .setWriteFormatter(new StringWriteFormater("utf-8"))
                        .create();

                //设置数据读写监听
                //设置读取数据监听
                deviceManager.setReadDataListener(new ReadDataListener<String>() {

                    @Override
                    public void produceData(String s) {
                        LogUtil.logD(s);
                    }
                });
                //设置写出数据监听
                deviceManager.setWriteDataListener(new WriteDataListener() {
                    @Override
                    public void success(int id) {

                    }

                    @Override
                    public void progress(int id, long total, long currentPro) {

                    }

                    @Override
                    public boolean enableProgress() {
                        return false;
                    }

                    @Override
                    public void failed(int id) {

                    }
                });

                //通过当前的 DeviceManager 建立连接
                controller.connect(deviceManager);
            }
        });

        /********写出数据*********/
        mSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取数据写出者
                WriteOutLinker linker = controller.getWriteLinker();
                //写出数据  数据ID
                linker.writeData("hello", 1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.release();
    }

    static class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> implements View.OnClickListener {
        private ArrayList<BluetoothDevice> data = new ArrayList<>();
        private ConnectController controller;

        public DeviceAdapter(ArrayList<BluetoothDevice> data, ConnectController controller) {
            this.data = data;
            this.controller = controller;
        }

        public DeviceAdapter(ConnectController controller) {
            this.controller = controller;
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
            if (data == null) return 0;

            return data.size();
        }

        public ArrayList<BluetoothDevice> getData() {
            return data;
        }

        /***********已客户端的形式连接蓝牙服务器**************/
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            BluetoothDevice bluetoothDevice = data.get(position);

            //已客户端的形式连接
            DeviceManagerImpl deviceManager = new DeviceManagerImpl
                    .Builder()
                    .setConnectType(ConnectType.CLIENT_INPUT_OUTPUT)
                    .buildDevice(bluetoothDevice)
                    .setReadFormatter(new StringReadFormater("utf-8"))
                    .setWriteFormatter(new StringWriteFormater("utf-8"))
                    .create();

            //设置数据读写监听
            //设置读取数据监听
            deviceManager.setReadDataListener(new ReadDataListener<String>() {

                @Override
                public void produceData(String s) {
                    LogUtil.logD(s);
                }
            });
            //设置写出数据监听
            deviceManager.setWriteDataListener(new WriteDataListener() {
                @Override
                public void success(int id) {

                }

                @Override
                public void progress(int id, long total, long currentPro) {

                }

                @Override
                public boolean enableProgress() {
                    return false;
                }

                @Override
                public void failed(int id) {

                }
            });

            controller.connect(deviceManager);
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


    final int REQUEST_PERMISSION_BT = 100;

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            int sdkInt = Build.VERSION.SDK_INT;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 提示权限已经被禁用 且不在提示
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
                    // 请求权限成功
                    LogUtil.logI("request permission success");
                } else {
                    // 提示权限已经被禁用

                    LogUtil.logI("request permission failed");

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
