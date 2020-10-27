package com.baobab.user.baobabflyer.server.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.ReviewInsert;
import com.baobab.user.baobabflyer.server.util.ImageUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

public class ReviewGridViewAdapter extends BaseAdapter {

    List<Uri> imgs;
    Context context;
    LayoutInflater inf;

    private static int PICK_IMAGE_MULTIPLE = 1;

    public ReviewGridViewAdapter(List<Uri> imgs, Context context) {
        this.imgs = imgs;
        this.context = context;
        inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imgs.size() + 1;
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
            convertView = inf.inflate(R.layout.review_input_img, null);
        }

        if(position + 1 == getCount()){
            if(imgs.size() == 9){
                convertView.findViewById(R.id.all_layout).setVisibility(View.GONE);
            }else {
                convertView.findViewById(R.id.img_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageMultiPick();
                    }
                });
                convertView.findViewById(R.id.add_btn).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.img).setVisibility(View.GONE);
                convertView.findViewById(R.id.clear_btn).setVisibility(View.GONE);
            }
        }else {
            final ImageView imageView = convertView.findViewById(R.id.img);
            Glide.with(context).load(imgs.get(position)).asBitmap().override(250, 250).centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    ImageUtil util = new ImageUtil();
                    imageView.setImageBitmap(util.getRoundedCornerBitmap(resource, 40));
                }
            });

            convertView.findViewById(R.id.img_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgs.remove(position);
                    ((ReviewInsert)ReviewInsert.context).uriList = imgs;
                    notifyDataSetChanged();
                }
            });

            convertView.findViewById(R.id.add_btn).setVisibility(View.GONE);
        }
        return convertView;
    }

    public void imageMultiPick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        ((Activity)context).startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
    }
}
