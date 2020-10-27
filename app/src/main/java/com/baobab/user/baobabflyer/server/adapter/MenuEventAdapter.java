package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;

import java.util.List;

public class MenuEventAdapter extends RecyclerView.Adapter<MenuEventAdapter.ItemViewHolder> {

    List<EventCpVO> list;
    Context context;
    String cpName;

    public MenuEventAdapter(List<EventCpVO> list, Context context, String cpName) {
        this.list = list;
        this.context = context;
        this.cpName = cpName;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_event_list, viewGroup, false);
        return new MenuEventAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        EventCpVO vo = list.get(i);

        itemViewHolder.eventName.setText(vo.getEventName());
        itemViewHolder.salesRating.setText("판매량 : " + vo.getSalesRate());

        RecyclerView recyclerView = itemViewHolder.optionRecyclerView;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        MenuOptionAdapter adapter = new MenuOptionAdapter(vo.getOptionList(), context, cpName, vo.getCpSeq());

        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView eventName;
        TextView salesRating;
        RecyclerView optionRecyclerView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.eventName);
            salesRating = itemView.findViewById(R.id.salesRating);
            optionRecyclerView = itemView.findViewById(R.id.optionRecyclerView);
        }
    }
}
