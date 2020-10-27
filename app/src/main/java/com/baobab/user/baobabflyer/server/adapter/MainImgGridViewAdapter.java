package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baobab.user.baobabflyer.BaobabMainImgUpload;
import com.baobab.user.baobabflyer.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.List;
import java.util.UUID;

public class MainImgGridViewAdapter extends BaseAdapter {

    List<Object> imgs;
    Context context;
    int displayWidth;
    androidx.appcompat.app.AlertDialog alert;

    LayoutInflater inf;

    public MainImgGridViewAdapter(List<Object> imgs, Context context, int displayWidth, androidx.appcompat.app.AlertDialog alert) {
        this.imgs = imgs;
        this.context = context;
        this.displayWidth = displayWidth;
        this.alert = alert;

        inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public Object getItem(int position) {
        return imgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inf.inflate(R.layout.main_img_gridview, null);
        }

        ImageView imgv = convertView.findViewById(R.id.img);
        imgv.setLayoutParams(new RelativeLayout.LayoutParams(displayWidth, displayWidth));
        imgv.setVisibility(View.VISIBLE);

        Glide.clear(imgv);
        Glide.with(context).load(imgs.get(position)).override(displayWidth, displayWidth).centerCrop().signature(new StringSignature(UUID.randomUUID().toString())).into(imgv);

        convertView.findViewById(R.id.img_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaobabMainImgUpload.url = imgs.get(position).toString();
                alert.show();
            }
        });

        return convertView;
    }
}
