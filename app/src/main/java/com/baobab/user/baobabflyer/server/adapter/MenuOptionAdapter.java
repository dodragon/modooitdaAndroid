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
import com.baobab.user.baobabflyer.server.vo.EventCpOptionVO;

import java.util.List;

public class MenuOptionAdapter extends RecyclerView.Adapter<MenuOptionAdapter.ItemViewHolder> {

    List<EventCpOptionVO> list;
    Context context;

    String cpName;
    int cpSeq;

    public MenuOptionAdapter(List<EventCpOptionVO> list, Context context, String cpName, int cpSeq) {
        this.list = list;
        this.context = context;
        this.cpName = cpName;
        this.cpSeq = cpSeq;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_option_list, viewGroup, false);
        return new MenuOptionAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        EventCpOptionVO vo = list.get(i);

        itemViewHolder.optionName.setText(vo.getOptionName());

        RecyclerView recyclerView = itemViewHolder.menuRecyclerView;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        MenuMenuAdapter adapter = new MenuMenuAdapter(vo.getMenuList(), context, cpName, cpSeq, vo.getEventSerial());

        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(adapter);

        if(i + 1 == getItemCount()){
            itemViewHolder.line.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView optionName;
        RecyclerView menuRecyclerView;
        View line;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            optionName = itemView.findViewById(R.id.optionName);
            menuRecyclerView = itemView.findViewById(R.id.menuRecyclerView);
            line = itemView.findViewById(R.id.line);
        }
    }
}
