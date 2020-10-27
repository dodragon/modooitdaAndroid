package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.util.ImageUtil;
import com.baobab.user.baobabflyer.server.vo.ReviewsSelectVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ItemViewHolder> {

    List<ReviewsSelectVO> list;
    Context context;
    ImageUtil imageUtil;

    public ReviewsAdapter(List<ReviewsSelectVO> list, Context context) {
        this.list = list;
        this.context = context;

        imageUtil = new ImageUtil();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_recylerview, viewGroup, false);
        return new ReviewsAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        ReviewsSelectVO vo = list.get(i);

        Glide.with(context.getApplicationContext()).load(vo.getProfileImg()).asBitmap().override(225, 225).centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                itemViewHolder.profileImg.setImageBitmap(imageUtil.getCircularBitmap(resource));
            }
        });

        itemViewHolder.nickName.setText(vo.getNickName());
        itemViewHolder.insertDate.setText(new SimpleDateFormat("yy.MM.dd").format(vo.getInsertDate()));
        itemViewHolder.payMenus.setText(vo.getPayMenus());
        itemViewHolder.content.setText(vo.getContent());

        if(vo.getImgUrls().size() == 0){
            itemViewHolder.imgRecyclerView.setVisibility(View.GONE);
        }else {
            RecyclerView recyclerView = itemViewHolder.imgRecyclerView;
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

            ReviewImgSetAdapter adapter = new ReviewImgSetAdapter(vo.getImgUrls(), context);

            recyclerView.removeAllViewsInLayout();
            recyclerView.setAdapter(adapter);
        }

        for(int j=0;j<vo.getScore();j++){
            itemViewHolder.starLayout.addView(makeAllStar(R.drawable.star_trek_blue));
        }

        if(i == (getItemCount() - 1)){
            itemViewHolder.line.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private ImageView makeAllStar(int star){
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
        layoutParams.setMargins(0, 0, 3, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(star);

        return imageView;
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        ImageView profileImg;
        TextView nickName;
        TextView payMenus;
        TextView insertDate;
        TextView content;
        RecyclerView imgRecyclerView;
        View line;
        LinearLayout starLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profileImg);
            nickName = itemView.findViewById(R.id.nickName);
            payMenus = itemView.findViewById(R.id.payMenus);
            insertDate = itemView.findViewById(R.id.insertDate);
            content = itemView.findViewById(R.id.content);
            imgRecyclerView = itemView.findViewById(R.id.image_recycler);
            line = itemView.findViewById(R.id.line);
            starLayout = itemView.findViewById(R.id.starLayout);
        }
    }
}
