package com.jumper.openplatformdemo.bledevice;

import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jumper.bluetoothdevicelib.callback.DeviceScanCallback;
import com.jumper.bluetoothdevicelib.device.DeviceEnum;
import com.jumper.bluetoothdevicelib.result.Listener;
import com.jumper.bluetoothdevicelib.result.Result;
import com.jumper.bluetoothdevicelib.result.ResultInterface;
import com.jumper.jumperopenplatform.ble.helper.BleHelper;
import com.jumper.openplatformdemo.R;
import com.jumper.openplatformdemo.util.L;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


@EActivity(R.layout.activity_ble_device_demo)
public class BleDeviceDemoActivity extends AppCompatActivity {

    @ViewById
    ListView listView;

    @ViewById
    Spinner spinner;

    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();

    private MyAdapter myAdapter;

    @ViewById
    TextView tvState, tvResult;


    private DeviceEnum deviceEnum = DeviceEnum.THERMOMETER;

    @AfterViews
    void afterView() {
        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.devicetype, android.R.layout
                .simple_spinner_item);
        //设置下拉列表的风格
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter2 添加到spinner中
        spinner.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (0 == position) {
                    deviceEnum = DeviceEnum.THERMOMETER;
                } else if (1 == position) {
                    deviceEnum = DeviceEnum.OXIMETER;
                } else if (2 == position) {
                    deviceEnum = DeviceEnum.WEIGHINGSCALE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) parent.getItemAtPosition(position);
                BleHelper.getInstance(BleDeviceDemoActivity.this).connect(bluetoothDevice, deviceEnum,
                        new Listener<ResultInterface>() {
                            @Override
                            public void onResult(ResultInterface result) {
                                L.e("onResult = " + result.toString());
                                tvResult.setText("读取结果：" + result.toString());
                                Log.i("Jumper", "读取结果：" + result.toString());
                            }

                            @Override
                            public void onConnectedState(int state) {
                                L.e("最终回调onConnectedState = " + state);
                                tvState.setText("连接状态：" + getStateName(state));
                                Log.i("Jumper", "连接状态：" + getStateName(state));
                            }
                        });
            }
        });

        BleHelper.getInstance(this).startScan(new DeviceScanCallback() {
            @Override
            public void onNewDeviceFind(BluetoothDevice device) {
                bluetoothDevices.add(device);
                myAdapter.notifyDataSetChanged();
            }
        });
    }


    private String getStateName(int state) {
        String stateName = "未知状态";
        switch (state) {
            case Result.STATE_CONNECTED:
                stateName = "已连接";
                break;
            case Result.STATE_DISCONNECTED:
                stateName = "断开连接";
                break;
            case Result.STATE_NOTIFY_SUCCESS:
                stateName = "接收数据中";
                break;
        }
        return stateName;
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bluetoothDevices == null ? 0 : bluetoothDevices.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return bluetoothDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(BleDeviceDemoActivity.this).inflate(R.layout
                        .item_device, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(getItem(position).getName() + ":" + getItem(position).getAddress());
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView textView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleHelper.getInstance(this).destroy();
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(400);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            L.e("ssss " + mName);
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public void disConnect(View view) {
        BleHelper.getInstance(this).disConnect();
    }
}
