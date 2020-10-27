package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.MainListTotalVO;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.util.List;

public class MainBottomListAdapter extends RecyclerView.Adapter<MainBottomListAdapter.ItemViewHolder>{

    List<MainListTotalVO> list;
    Context context;
    DecimalFormat formatter;

    public MainBottomListAdapter(List<MainListTotalVO> list, Context context) {
        this.list = list;
        this.context = context;
        formatter = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public MainBottomListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_bottom_list, viewGroup, false);
        return new MainBottomListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainBottomListAdapter.ItemViewHolder itemViewHolder, int i) {
        final MainListTotalVO vo = list.get(i);

        if(vo.getCpInfoVO().getCpLogo() == null){
            Glide.with(context).load(vo.getMainListVO().getImgUrl()).override(216, 216).centerCrop().into(itemViewHolder.cpLogo);
        }else {
            Glide.with(context).load(vo.getCpInfoVO().getCpLogo()).override(216, 216).centerCrop().into(itemViewHolder.cpLogo);
        }


        String theme = vo.getCpInfoVO().getCP_Theme1() + vo.getCpInfoVO().getCP_Theme2();
        if(theme.contains("방문") && theme.contains("포장")){
            itemViewHolder.categoryDiv.setText("방문·포장");
        }else if(theme.contains("방문") && !theme.contains("포장")){
            itemViewHolder.categoryDiv.setText("방문");
        }else if(!theme.contains("방문") && theme.contains("포장")){
            itemViewHolder.categoryDiv.setText("포장");
        }

        itemViewHolder.cpName.setText(vo.getCpInfoVO().getCP_name());
        itemViewHolder.menuName.setText(vo.getMenuVO().getMenuName());
        itemViewHolder.revGrade.setText(String.valueOf(vo.getCpInfoVO().getRev_grade()));
        itemViewHolder.disPrice.setText(formatter.format(vo.getMenuVO().getDisPrice()) + "원");
        itemViewHolder.price.setText(formatter.format(vo.getMenuVO().getPrice()) + "원");
        itemViewHolder.price.setPaintFlags(itemViewHolder.price.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        itemViewHolder.distance.setText(distanceCalculator(vo.getCpInfoVO().getDistance()));

        itemViewHolder.allLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabMenu.class);
                intent.putExtra("context", "page");
                intent.putExtra("info", vo.getCpInfoVO());
                intent.putExtra("longitude", Double.parseDouble(context.getSharedPreferences("user", 0).getString("longitude", "0.0")));
                intent.putExtra("latitude", Double.parseDouble(context.getSharedPreferences("user", 0).getString("latitude", "0.0")));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list.size() > 5){
            return 5;
        }else {
            return list.size();
        }
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
        RoundedImageView cpLogo;
        TextView cpName;
        TextView categoryDiv;
        TextView menuName;
        TextView disPrice;
        TextView price;
        TextView revGrade;
        TextView distance;
        ConstraintLayout allLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.cpLogo = itemView.findViewById(R.id.cpLogo);
            this.cpName = itemView.findViewById(R.id.cpName);
            this.categoryDiv = itemView.findViewById(R.id.categoryDiv);
            this.menuName = itemView.findViewById(R.id.menuName);
            this.disPrice = itemView.findViewById(R.id.disPrice);
            this.price = itemView.findViewById(R.id.price);
            this.revGrade = itemView.findViewById(R.id.revGrade);
            this.distance = itemView.findViewById(R.id.distance);
            this.allLayout = itemView.findViewById(R.id.allLayout);
        }
    }
}
