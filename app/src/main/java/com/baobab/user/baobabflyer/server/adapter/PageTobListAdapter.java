package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.PageTopListVO;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.util.List;

public class PageTobListAdapter extends RecyclerView.Adapter<PageTobListAdapter.ItemViewHolder>{

    List<PageTopListVO> list;
    Context context;
    String tabDiv;
    DecimalFormat formatter;

    public PageTobListAdapter(List<PageTopListVO> list, Context context, String tabDiv) {
        this.list = list;
        this.context = context;
        this.tabDiv = tabDiv;
        formatter = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.page_top_list, viewGroup, false);
        return new PageTobListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final PageTopListVO vo = list.get(i);

        itemViewHolder.allLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabMenu.class);
                intent.putExtra("context", "page");
                intent.putExtra("info", vo.getInfoVO());
                intent.putExtra("longitude", Double.parseDouble(context.getSharedPreferences("user", 0).getString("longitude", "0.0")));
                intent.putExtra("latitude", Double.parseDouble(context.getSharedPreferences("user", 0).getString("latitude", "0.0")));
                context.startActivity(intent);
            }
        });

        if(vo.getInfoVO().getCpLogo() == null){
            Glide.with(context).load(vo.getInfoVO().getImg_url()).override(216, 216).centerCrop().into(itemViewHolder.cpLogo);
        }else {
            Glide.with(context).load(vo.getInfoVO().getCpLogo()).override(216, 216).centerCrop().into(itemViewHolder.cpLogo);
        }

        if(vo.getInfoVO().getCP_Theme1().contains("안심")){
            itemViewHolder.safetyMark.setVisibility(View.VISIBLE);
        }else {
            itemViewHolder.safetyMark.setVisibility(View.GONE);
        }

        itemViewHolder.revGrade.setText(String.valueOf(vo.getInfoVO().getRev_grade()));
        itemViewHolder.menuName.setText(vo.getMenuVO().getMenuName());
        itemViewHolder.disPrice.setText(formatter.format(vo.getMenuVO().getDisPrice()) + "원");
        itemViewHolder.price.setText(formatter.format(vo.getMenuVO().getPrice()) + "원");
        itemViewHolder.price.setPaintFlags(itemViewHolder.price.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        itemViewHolder.cpName.setText(vo.getInfoVO().getCP_name());
        itemViewHolder.distance.setText(distanceCalculator(vo.getInfoVO().getDistance()));

        if(tabDiv.equals("전체")){
            if(vo.getInfoVO().getCP_Theme1().contains("안심")){
                itemViewHolder.safetyMark.setVisibility(View.VISIBLE);
            }else {
                itemViewHolder.safetyMark.setVisibility(View.GONE);
            }
        }else if(tabDiv.equals("안심")){
            itemViewHolder.safetyMark.setVisibility(View.VISIBLE);
        }else {
            itemViewHolder.safetyMark.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String distanceCalculator(double distance){
        double newDistance = Math.round((distance * 1000) / 1000.0);

        if(newDistance > 1){
            return Math.round((distance * 10) / 10.0) + "km";
        }else if(newDistance == 1){
            return "1.0km";
        }else {
            return Math.round((distance * 1000000) / 1000.0) + "m";
        }
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        ConstraintLayout allLayout;
        RoundedImageView cpLogo;
        LinearLayout safetyMark;
        TextView revGrade;
        TextView cpName;
        TextView menuName;
        TextView disPrice;
        TextView distance;
        TextView price;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.allLayout = itemView.findViewById(R.id.allLayout);
            this.cpLogo = itemView.findViewById(R.id.cpLogo);
            this.cpName = itemView.findViewById(R.id.cpName);
            this.menuName = itemView.findViewById(R.id.menuName);
            this.safetyMark = itemView.findViewById(R.id.safetyMark);
            this.revGrade = itemView.findViewById(R.id.revGrade);
            this.disPrice = itemView.findViewById(R.id.disPrice);
            this.price = itemView.findViewById(R.id.price);
            this.distance = itemView.findViewById(R.id.distance);
        }
    }
}
