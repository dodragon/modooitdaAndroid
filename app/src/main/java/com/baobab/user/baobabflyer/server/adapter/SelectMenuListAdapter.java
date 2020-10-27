package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;

import java.text.DecimalFormat;
import java.util.List;

public class SelectMenuListAdapter extends RecyclerView.Adapter<SelectMenuListAdapter.ItemViewHolder>{

    List<EventCpMenuVO> list;
    View menuBtn;
    Context context;
    RecyclerView selectedRecyclerView;
    Button sellBtn;

    DecimalFormat format;

    List<EventCpMenuVO> selectedList;
    SelectedMenuAdapter selectedAdapter;

    boolean isSelected;
    int selectedM;

    public SelectMenuListAdapter(List<EventCpMenuVO> list, View menuBtn, Context context, RecyclerView selectedRecyclerView, Button sellBtn, List<EventCpMenuVO> selectedList, SelectedMenuAdapter selectedAdapter, boolean isSelected,
                                 int selectedM) {
        this.list = list;
        this.menuBtn = menuBtn;
        this.context = context;
        this.selectedRecyclerView = selectedRecyclerView;
        this.sellBtn = sellBtn;
        this.selectedList = selectedList;
        this.selectedAdapter = selectedAdapter;

        this.isSelected = isSelected;
        this.selectedM = selectedM;

        format = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_menu_list, viewGroup, false);
        return new SelectMenuListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final EventCpMenuVO vo = list.get(i);

        itemViewHolder.menuName.setText(vo.getMenuName());
        itemViewHolder.price.setText(format.format(vo.getPrice()) + "원");
        itemViewHolder.price.setPaintFlags(itemViewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        itemViewHolder.disPrice.setText(format.format(vo.getDisPrice()) + "원");

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectedList.contains(vo)){
                    selectedList.add(0, vo);
                    selectedAdapter.notifyDataSetChanged();
                }
            }
        });

        if(isSelected && vo.getSeqNum() == selectedM){
            itemViewHolder.layout.performClick();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView menuName;
        TextView price;
        TextView disPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            menuName = itemView.findViewById(R.id.menuName);
            price = itemView.findViewById(R.id.price);
            disPrice = itemView.findViewById(R.id.disPrice);
        }
    }
}
