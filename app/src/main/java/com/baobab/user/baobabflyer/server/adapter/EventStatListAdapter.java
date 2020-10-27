package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabEventStatisticsDetail;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.EventStatListVO;

import java.text.DecimalFormat;
import java.util.List;

public class EventStatListAdapter extends RecyclerView.Adapter<EventStatListAdapter.ItemViewHolder> {

    List<EventStatListVO> list;
    DecimalFormat format;
    Context context;

    String start;
    String end;

    public EventStatListAdapter(List<EventStatListVO> list, Context context, String start, String end) {
        this.list = list;
        this.context = context;
        this.start = start;
        this.end = end;

        format = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_stat_list, viewGroup, false);
        return new EventStatListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final EventStatListVO vo = list.get(i);

        itemViewHolder.eventName.setText(vo.getEventName());
        itemViewHolder.paidCount.setText(vo.getPaidCount() + "회");
        itemViewHolder.cancelCount.setText(vo.getCancelCount() + "회");
        itemViewHolder.scanCount.setText(vo.getScanCount() + "회");
        itemViewHolder.allPaid.setText(format.format(vo.getAllPaid()) + "원");

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabEventStatisticsDetail.class);
                intent.putExtra("eventSerial", vo.getEventSerial());
                intent.putExtra("start", start);
                intent.putExtra("end", end);
                context.startActivity(intent);
            }
        });
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
