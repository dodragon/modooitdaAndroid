package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baobab.user.baobabflyer.AdWebViewActivity;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.AdImgVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;

import java.util.List;
import java.util.UUID;

public class MainAdAdapter extends PagerAdapter {

    List<AdImgVO> list;
    Context context;

    public MainAdAdapter(List<AdImgVO> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ad_slide, null);

        final AdImgVO item = list.get(position);

        final ImageView imageView = v.findViewById(R.id.ad_imgV);
        Glide.with(context).load(item.getSm_img()).signature(new StringSignature(UUID.randomUUID().toString())).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                imageView.setBackground(resource);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getBig_img() != null){
                    Intent intent = new Intent(context, AdWebViewActivity.class);
                    intent.putExtra("url", item.getBig_img());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object o) {
        container.removeView((View) o);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
