package com.baobab.user.baobabflyer.server.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.MenuVO;

import java.text.DecimalFormat;
import java.util.List;

public class MenuDetailOptionListAdapter extends RecyclerView.Adapter<MenuDetailOptionListAdapter.ItemViewHolder>{

    List<MenuVO> list;
    DecimalFormat format;

    public MenuDetailOptionListAdapter(List<MenuVO> list) {
        this.list = list;

        format = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_detail_option_list, viewGroup, false);
        return new MenuDetailOptionListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        MenuVO vo = list.get(i);

        itemViewHolder.option.setText(vo.getMenu_option());
        itemViewHolder.price.setText(format.format(vo.getMenu_dis_price()) + "원");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView option;
        TextView price;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            option = itemView.findViewById(R.id.option);
            price = itemView.findViewById(R.id.price);
        }
    }
}
