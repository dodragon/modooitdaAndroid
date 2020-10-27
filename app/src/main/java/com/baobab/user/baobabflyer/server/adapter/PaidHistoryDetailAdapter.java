package com.baobab.user.baobabflyer.server.adapter;

import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.PayMenusVO;

import java.text.DecimalFormat;
import java.util.List;

public class PaidHistoryDetailAdapter extends RecyclerView.Adapter<PaidHistoryDetailAdapter.ItemViewHolder> {

    List<PayMenusVO> list;
    DecimalFormat format;

    public PaidHistoryDetailAdapter(List<PayMenusVO> list) {
        this.list = list;
        format = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.paid_history_detail_list, viewGroup, false);
        return new PaidHistoryDetailAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        PayMenusVO vo = list.get(i);

        itemViewHolder.menuName.setText(vo.getMenuName());
        itemViewHolder.ea.setText(vo.getEa() + "개");
        if(vo.getPrice() > vo.getDisPrice()){
            itemViewHolder.price.setText(format.format(vo.getPrice()));
            itemViewHolder.price.setPaintFlags(itemViewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        itemViewHolder.disPrice.setText(format.format(vo.getDisPrice()) + "원");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView menuName;
        TextView ea;
        TextView price;
        TextView disPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            menuName = itemView.findViewById(R.id.menuName);
            ea = itemView.findViewById(R.id.ea);
            price = itemView.findViewById(R.id.price);
            disPrice = itemView.findViewById(R.id.disPrice);
        }
    }
}
