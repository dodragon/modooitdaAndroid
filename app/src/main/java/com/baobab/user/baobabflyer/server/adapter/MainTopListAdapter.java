package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.MainListTotalVO;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class MainTopListAdapter extends RecyclerView.Adapter<MainTopListAdapter.ItemViewHolder>{

    List<MainListTotalVO> list;
    Context context;

    public MainTopListAdapter(List<MainListTotalVO> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_top_list, viewGroup, false);
        return new MainTopListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final MainListTotalVO vo = list.get(i);

        DecimalFormat formatter = new DecimalFormat("###,###");

        itemViewHolder.cpName.setText(vo.getCpInfoVO().getCP_name());
        itemViewHolder.salesEa.setText(String.valueOf(vo.getTotalSales()));
        itemViewHolder.disPrice.setText(formatter.format(vo.getMenuVO().getDisPrice()) + "원");
        itemViewHolder.price.setText(formatter.format(vo.getMenuVO().getPrice()) + "원");
        itemViewHolder.price.setPaintFlags(itemViewHolder.price.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        Glide.with(context).load(vo.getMainListVO().getImgUrl()).override(320, 336).centerCrop().into(itemViewHolder.cpImg);

        itemViewHolder.cpImg.setBackground(context.getDrawable(R.drawable.imgview_all_round));
        itemViewHolder.cpImg.setClipToOutline(true);

        if(i == getItemCount() - 1){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.rightMargin = (int)dpToPixels(24f, context);
            layoutParams.leftMargin = (int)dpToPixels(24f, context);
            itemViewHolder.allLayout.setLayoutParams(layoutParams);
        }

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
        return list.size();
    }

    public float dpToPixels(float dp, Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    /*public float pixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }*/

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        ImageView cpImg;
        TextView salesEa;
        TextView cpName;
        TextView disPrice;
        TextView price;
        LinearLayout layout;
        LinearLayout allLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.cpImg = itemView.findViewById(R.id.cpImg);
            this.salesEa = itemView.findViewById(R.id.salesEa);
            this.cpName = itemView.findViewById(R.id.cpName);
            this.disPrice = itemView.findViewById(R.id.disPrice);
            this.price = itemView.findViewById(R.id.price);
            this.layout = itemView.findViewById(R.id.layout);
            this.allLayout = itemView.findViewById(R.id.allLayout);
        }
    }
}
