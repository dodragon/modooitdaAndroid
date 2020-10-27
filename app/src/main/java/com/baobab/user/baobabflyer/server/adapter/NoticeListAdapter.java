package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabNoticeDetail;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.NoticeVO;

import java.text.SimpleDateFormat;
import java.util.List;

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.ItemViewHolder>{

    List<NoticeVO> list;
    Context context;
    SimpleDateFormat format;

    public NoticeListAdapter(List<NoticeVO> list, Context context) {
        this.list = list;
        this.context = context;

        format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_list, viewGroup, false);
        return new NoticeListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final NoticeVO vo = list.get(i);

        itemViewHolder.title.setText(vo.getTitle());
        itemViewHolder.date.setText(format.format(vo.getNoti_date()));

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabNoticeDetail.class);
                intent.putExtra("notiVO", vo);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        RelativeLayout layout;
        TextView title;
        TextView date;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
        }
    }
}
