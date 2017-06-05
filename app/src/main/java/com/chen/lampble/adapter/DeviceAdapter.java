package com.chen.lampble.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chen.lampble.R;
import com.chen.lampble.entity.ScanResultEntity;
import com.clj.fastble.data.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zehua_chen on 2017/6/5.
 */

public class DeviceAdapter extends BaseAdapter {

    private Context context;
    private List<ScanResult> resultEntityList;

    public DeviceAdapter(Context context) {
        this.context = context;
        this.resultEntityList = new ArrayList<>();
    }

    public DeviceAdapter(Context context, List<ScanResult> resultEntityList) {
        this.context = context;
        this.resultEntityList = resultEntityList;
    }

    public void addData(ScanResult entity) {
        resultEntityList.add(entity);
    }

    public void clear() {
        resultEntityList.clear();
    }

    @Override
    public int getCount() {
        return resultEntityList.size();
    }

    @Override
    public ScanResult getItem(int position) {
        if (position > resultEntityList.size()) {
            return null;
        }
        return resultEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.adapter_scan_result, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txt_mac = (TextView) convertView.findViewById(R.id.txt_mac);
            holder.txt_rssi = (TextView) convertView.findViewById(R.id.txt_rssi);
        }

        ScanResult result = resultEntityList.get(position);
        BluetoothDevice device = result.getDevice();
        String name = device.getName();
        String mac = device.getAddress();
        int rssi = result.getRssi();
        holder.txt_name.setText(name);
        holder.txt_mac.setText(mac);
        holder.txt_rssi.setText(String.valueOf(rssi));
        return convertView;
    }

    class ViewHolder {
        TextView txt_name;
        TextView txt_mac;
        TextView txt_rssi;
    }
}
