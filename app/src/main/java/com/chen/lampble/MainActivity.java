package com.chen.lampble;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.chen.lampble.adapter.DeviceAdapter;
import com.chen.lampble.service.BleService;
import com.clj.fastble.data.ScanResult;

public class MainActivity extends AppCompatActivity {

    private BleService mBleService;
    private DeviceAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        bindService();
    }

    private void initView() {
        mAdapter = new DeviceAdapter(this);
        mListView = (ListView) findViewById(R.id.list_device);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(null != mBleService) {
                    mBleService.cancelScan();
                    mBleService.connectDevice(mAdapter.getItem(position));
                }
            }
        });
    }

    private void bindService() {
        if(null == mBleService) {
            Intent bindIntent = new Intent(this, BleService.class);
            bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            mBleService.scanDevice();
        }
    }

    private void unbindService() {
        unbindService(mServiceConnection);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBleService = ((BleService.BleBinder) service).getService();
            mBleService.setScanCallback(callback);
            mBleService.scanDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleService = null;
        }
    };

    private BleService.Callback callback = new BleService.Callback() {
        @Override
        public void onStartScan() {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanning(ScanResult scanResult) {
            mAdapter.addData(scanResult);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanComplete() {

        }

        @Override
        public void onConnecting() {

        }

        @Override
        public void onConnectFail() {
            Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisConnected() {
            Toast.makeText(MainActivity.this, "连接断开", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onServicesDiscovered() {

        }
    };
}
