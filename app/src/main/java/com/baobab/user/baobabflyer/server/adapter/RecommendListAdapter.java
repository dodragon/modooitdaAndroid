package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.List;
import java.util.UUID;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.ItemViewHolder>{

    private List<CPInfoVO> recommendCPVOList;
    private Context context;
    double userLatitude;
    double userLongitude;
    String userLocation;
    CPInfoVO thisVO;

    public RecommendListAdapter(List<CPInfoVO> recommendCPVOList, Context context, double userLatitude, double userLongitude, String userLocation, CPInfoVO thisVO) {
        this.recommendCPVOList = recommendCPVOList;
        this.context = context;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.userLocation = userLocation;
        this.thisVO = thisVO;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recommend_cp_list, viewGroup, false);
        return new RecommendListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final CPInfoVO cpVO = recommendCPVOList.get(i);

        glideUrlResource(context, cpVO.getImg_url(), 150, 110).into(itemViewHolder.mainImg);

        itemViewHolder.cpName.setText(cpVO.getCP_name());
        itemViewHolder.distance.setText("(" + distanceCalcu(cpVO.getDistance()) + ")");

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabMenu.class);
                intent.putExtra("context", "recommend");
                intent.putExtra("userLoc", userLocation);
                intent.putExtra("info", cpVO);
                intent.putExtra("longitude", userLongitude);
                intent.putExtra("latitude", userLatitude);
                intent.putExtra("thisVO", thisVO);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recommendCPVOList.size();
    }

    public String distanceCalcu(double dis) {
        if (dis > 1) {
            dis = Double.parseDouble(String.format("%.2f", dis));
            return dis + "km";
        } else {
            return (int) (dis * 1000) + "m";
        }
    }

    public DrawableRequestBuilder<String> glideUrlResource(Context context, String resource, int width, int height){
        return  Glide.with(context).load(resource).override(width, height).signature(new StringSignature(UUID.randomUUID().toString())).centerCrop().thumbnail(0.1f);
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{

        LinearLayout layout;
        ImageView mainImg;
        TextView cpName;
        TextView distance;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            mainImg = itemView.findViewById(R.id.mainImg);
            cpName = itemView.findViewById(R.id.cpName);
            distance = itemView.findViewById(R.id.distance);
        }
    }
}
