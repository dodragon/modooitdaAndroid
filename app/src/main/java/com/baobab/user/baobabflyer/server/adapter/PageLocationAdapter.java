package com.baobab.user.baobabflyer.server.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabPage;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.PageNeedVO;

public class PageLocationAdapter  extends RecyclerView.Adapter<PageLocationAdapter.ItemViewHolder>{

    String[] locationArr;
    PageNeedVO pageNeedVO;
    Context context;

    public PageLocationAdapter(String[] locationArr, PageNeedVO pageNeedVO, Context context) {
        this.locationArr = locationArr;
        this.pageNeedVO = pageNeedVO;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.page_location, viewGroup, false);
        return new PageLocationAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.location.setText(locationArr[i]);
        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = itemViewHolder.location.getText().toString();

                if(location.equals("전체")){
                    location = "";
                }

                BaobabPage baobabPage = (BaobabPage) BaobabPage.activity;
                pageNeedVO.setLocation(location);
                Intent intent = new Intent(context, BaobabPage.class);
                intent.putExtra("pageVO", pageNeedVO);
                intent.putExtra("userLocation", itemViewHolder.location.getText().toString());
                context.startActivity(intent);
                baobabPage.finish();
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationArr.length;
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView location;
        LinearLayout layout;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            location = itemView.findViewById(R.id.location);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
