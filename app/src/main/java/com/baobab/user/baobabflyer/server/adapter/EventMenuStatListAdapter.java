package com.baobab.user.baobabflyer.server.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.MenuSpecListVO;

import java.text.DecimalFormat;
import java.util.List;

public class EventMenuStatListAdapter extends RecyclerView.Adapter<EventMenuStatListAdapter.ItemViewHolder> {

    List<MenuSpecListVO> list;
    DecimalFormat format;

    public EventMenuStatListAdapter(List<MenuSpecListVO> list) {
        this.list = list;
        format = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public EventMenuStatListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_stat_list, viewGroup, false);
        return new EventMenuStatListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventMenuStatListAdapter.ItemViewHolder itemViewHolder, int i) {
        MenuSpecListVO vo = list.get(i);

        itemViewHolder.eventName.setText(vo.getMenuName());
        itemViewHolder.paidCount.setText(vo.getPaidEa() + "회");
        itemViewHolder.scanCount.setText(vo.getScanEa() + "회");
        itemViewHolder.cancelCount.setText(vo.getCancelEa() + "회");
        itemViewHolder.allPaid.setText(format.format(vo.getAllPaid()) + "원");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView eventName;
        TextView paidCount;
        TextView scanCount;
        TextView cancelCount;
        TextView allPaid;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            eventName = itemView.findViewById(R.id.eventName);
            paidCount = itemView.findViewById(R.id.paidCount);
            scanCount = itemView.findViewById(R.id.scanCount);
            cancelCount = itemView.findViewById(R.id.cancelCount);
            allPaid = itemView.findViewById(R.id.allPaid);
        }
    }
}
