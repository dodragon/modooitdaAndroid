package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baobab.user.baobabflyer.NonCp;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.PageBotListVO;

import java.util.List;

public class PageBottomListAdapter extends RecyclerView.Adapter<PageBottomListAdapter.ItemViewHolder>{

    List<PageBotListVO> list;
    Context context;

    public PageBottomListAdapter(List<PageBotListVO> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.page_bottom_list, viewGroup, false);
        return new PageBottomListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final PageBotListVO vo = list.get(i);

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NonCp.class);
                intent.putExtra("vo", vo);
                context.startActivity(intent);
            }
        });

        if(vo.getSafeDiv().equals("안심")){
            itemViewHolder.safetyMark.setVisibility(View.VISIBLE);
        }else {
            itemViewHolder.safetyMark.setVisibility(View.GONE);
        }

        itemViewHolder.cpName.setText(vo.getCpName());
        itemViewHolder.distance.setText(distanceCalculator(vo.getDistance()));
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        ConstraintLayout layout;
        TextView cpName;
        TextView distance;
        ImageView safetyMark;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.layout = itemView.findViewById(R.id.layout);
            this.cpName = itemView.findViewById(R.id.cpName);
            this.distance = itemView.findViewById(R.id.distance);
            this.safetyMark = itemView.findViewById(R.id.safetyMark);
        }
    }
}
