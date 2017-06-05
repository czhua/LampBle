package com.chen.lampble.service;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telecom.Call;

import com.clj.fastble.BleManager;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.conn.BleGattCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.ListScanCallback;
import com.clj.fastble.utils.HexUtil;

public class BleService extends Service {

    private BleBinder mBinder = new BleBinder();
    private BleManager bleManager;
    private Handler threadHandler = new Handler(Looper.getMainLooper());
    private Callback mCallback;
    private Callback2 mCallback2;

    private String name;
    private String mac;
    private BluetoothGatt gatt;
    private BluetoothGattService service;
    private BluetoothGattCharacteristic characteristic;
    private int charaProp;


    public BleService() {
    }

    @Override
    public void onCreate() {
        bleManager = new BleManager(this);
        bleManager.enableBluetooth();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bleManager.closeBluetoothGatt();
        return super.onUnbind(intent);
    }

    public class BleBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    public void setScanCallback(Callback callback) {
        mCallback = callback;
    }

    public void setConnectCallback(Callback2 callback) {
        mCallback2 = callback;
    }


    public void scanDevice() {
        resetInfo();

        if(null != mCallback) {
            mCallback.onStartScan();
        }

        boolean b = bleManager.scanDevice(new ListScanCallback(5000) {
            @Override
            public void onScanning(final ScanResult result) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null != mCallback) {
                            mCallback.onScanning(result);
                        }
                    }
                });
            }

            @Override
            public void onScanComplete(ScanResult[] results) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null != mCallback) {
                            mCallback.onScanComplete();
                        }
                    }
                });
            }
        });

        if(!b) {
            if(null != mCallback) {
                mCallback.onScanComplete();
            }
        }

    }

    public void cancelScan() {
        bleManager.cancelScan();
    }

    public void connectDevice(final ScanResult scanResult) {
        if(null != mCallback) {
            mCallback.onConnecting();
        }

        bleManager.connectDevice(scanResult, true, new BleGattCallback() {
            @Override
            public void onNotFoundDevice() {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null != mCallback) {
                            mCallback.onConnectFail();
                        }
                    }
                });
            }

            @Override
            public void onFoundDevice(ScanResult scanResult) {
                name = scanResult.getDevice().getName();
                mac = scanResult.getDevice().getAddress();
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                gatt.discoverServices();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BleService.this.gatt = gatt;
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null != mCallback) {
                            mCallback.onServicesDiscovered();
                        }
                    }
                });
            }

            @Override
            public void onConnectFailure(BleException exception) {

                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null != mCallback) {
                            mCallback.onDisConnected();
                        }
                        if(null != mCallback2) {
                            mCallback2.onDisConnected();
                        }
                    }
                });
            }
        });
    }

    public void read(String uuid_service, String uuid_read, BleCharacterCallback callback) {
        bleManager.readDevice(uuid_service, uuid_read, callback);
    }

    public void write(String uuid_service, String uuid_write, String hex, BleCharacterCallback callback) {
        bleManager.writeDevice(uuid_service, uuid_write, HexUtil.hexStringToBytes(hex), callback);
    }

    public void closeConnect() {
        bleManager.closeBluetoothGatt();
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setService(BluetoothGattService service) {
        this.service = service;
    }

    public BluetoothGattService getService() {
        return service;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharaProp(int charaProp) {
        this.charaProp = charaProp;
    }

    public int getCharaProp() {
        return charaProp;
    }

    private void runOnMainThread(Runnable runnable) {
        if(Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }

    private void resetInfo() {
        name = null;
        mac = null;
        gatt = null;
        service = null;
        characteristic = null;
        charaProp = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bleManager = null;
        mCallback = null;
        mCallback2 = null;
    }

    public interface Callback {
        void onStartScan();

        void onScanning(ScanResult scanResult);

        void onScanComplete();

        void onConnecting();

        void onConnectFail();

        void onDisConnected();

        void onServicesDiscovered();
    }

    public interface Callback2 {
        void onDisConnected();
    }
}
