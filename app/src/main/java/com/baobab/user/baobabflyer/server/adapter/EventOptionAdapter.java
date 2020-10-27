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

public class EventOptionAdapter extends RecyclerView.Adapter<EventOptionAdapter.ItemViewHolder> {

    List<EventCpOptionVO> list;
    Context context;
    int cpSeq;
    int mainSeq;

    public EventOptionAdapter(List<EventCpOptionVO> list, Context context, int cpSeq, int mainSeq) {
        this.list = list;
        this.context = context;
        this.cpSeq = cpSeq;
        this.mainSeq = mainSeq;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_option_list, viewGroup, false);
        return new EventOptionAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        EventCpOptionVO vo = list.get(i);

        itemViewHolder.optionName.setText(vo.getOptionName());

        RecyclerView recyclerView = itemViewHolder.recyclerView;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        EventMenuAdapter adapter = new EventMenuAdapter(vo.getMenuList(), context, cpSeq, mainSeq);

        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView optionName;
        RecyclerView recyclerView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            optionName = itemView.findViewById(R.id.optionName);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}
