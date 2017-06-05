package com.chen.lampble.entity;

import android.bluetooth.BluetoothDevice;

/**
 * Created by zehua_chen on 2017/6/5.
 */

public class ScanResultEntity {

    private BluetoothDevice mDevice;
    private byte[] mScanRecord;
    private long mTimestampNano;
    private int mRssi;


    public ScanResultEntity(BluetoothDevice mDevice, byte[] mScanRecord, long mTimestampNano, int mRssi) {
        this.mDevice = mDevice;
        this.mScanRecord = mScanRecord;
        this.mTimestampNano = mTimestampNano;
        this.mRssi = mRssi;
    }


    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }

    public byte[] getmScanRecord() {
        return mScanRecord;
    }

    public void setmScanRecord(byte[] mScanRecord) {
        this.mScanRecord = mScanRecord;
    }

    public long getmTimestampNano() {
        return mTimestampNano;
    }

    public void setmTimestampNano(long mTimestampNano) {
        this.mTimestampNano = mTimestampNano;
    }

    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int mRssi) {
        this.mRssi = mRssi;
    }
}
