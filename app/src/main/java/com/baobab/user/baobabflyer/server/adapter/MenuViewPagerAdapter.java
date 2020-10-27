package com.baobab.user.baobabflyer.server.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baobab.user.baobabflyer.R;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class MenuViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private String[] imgArr;

    public MenuViewPagerAdapter(Context mContext, String[] imgArr) {
        this.mContext = mContext;
        this.imgArr = imgArr;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.menu_view_pager, null);

        ImageView imageView = view.findViewById(R.id.imgview);

        Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        Glide.with(mContext).load(imgArr[position]).centerCrop().into(imageView);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return imgArr.length;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }
}
