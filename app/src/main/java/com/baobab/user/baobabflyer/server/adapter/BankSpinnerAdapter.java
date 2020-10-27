package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;

public class BankSpinnerAdapter extends BaseAdapter {

    String[] arr;
    Context context;
    LayoutInflater inflater;

    public BankSpinnerAdapter(String[] arr, Context context) {
        this.arr = arr;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arr.length;
    }

    @Override
    public Object getItem(int position) {
        return arr[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.bank_spinner_normal, parent, false);
        }

        if(arr != null){
            String text = arr[position];
            ((TextView)convertView.findViewById(R.id.spinnerText)).setText(text);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView = inflater.inflate(R.layout.bank_spinner_dropdown, parent, false);
        }

        if(arr != null){
            String text = arr[position];
            ((TextView)convertView.findViewById(R.id.spinnerText)).setText(text);
        }

        return convertView;
    }
}
