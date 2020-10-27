package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.util.ImageUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ReviewImgSetAdapter extends RecyclerView.Adapter<ReviewImgSetAdapter.ItemViewHolder> {

    List<String> imgUrls;
    Context context;
    ImageUtil imageUtil;

    public ReviewImgSetAdapter(List<String> imgUrls, Context context) {
        this.imgUrls = imgUrls;
        this.context = context;

        imageUtil = new ImageUtil();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_img_recyclerview, viewGroup, false);
        return new ReviewImgSetAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        Glide.with(context).load(imgUrls.get(i)).override(312, 216).centerCrop().into(itemViewHolder.reviewImg);
    }

    @Override
    public int getItemCount() {
        return imgUrls.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        RoundedImageView reviewImg;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewImg = itemView.findViewById(R.id.revImgs);
        }
    }
}
