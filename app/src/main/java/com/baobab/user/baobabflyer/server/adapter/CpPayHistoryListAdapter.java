package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabCpPayHistoryDetail;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class CpPayHistoryListAdapter extends RecyclerView.Adapter<CpPayHistoryListAdapter.ItemViewHolder> {

    List<PaymentVO> list;
    Context context;
    DecimalFormat decimalFormat;
    SimpleDateFormat simpleDateFormat;

    public CpPayHistoryListAdapter(List<PaymentVO> list, Context context) {
        this.list = list;
        this.context = context;

        decimalFormat = new DecimalFormat("###,###");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cp_pay_history_list, viewGroup, false);
        return new CpPayHistoryListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final PaymentVO vo = list.get(i);

        itemViewHolder.menus.setText(vo.getGoods());
        if(vo.getPayStatus().equals("결제완료")){
            itemViewHolder.status.setText("상태 : 결제완료");
        }else {
            itemViewHolder.status.setText("상태 : 결제취소");
        }

        itemViewHolder.date.setText(simpleDateFormat.format(vo.getCurDate()));
        itemViewHolder.detail.setPaintFlags(itemViewHolder.detail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        itemViewHolder.paidCount.setText(decimalFormat.format(vo.getTotalDisPrice()) + "원");

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabCpPayHistoryDetail.class);
                intent.putExtra("orderNum", vo.getOrderNum());
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
        TextView menus;
        TextView date;
        TextView detail;
        TextView paidCount;
        TextView status;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            menus = itemView.findViewById(R.id.menus);
            date = itemView.findViewById(R.id.date);
            detail = itemView.findViewById(R.id.detail);
            paidCount = itemView.findViewById(R.id.paidCount);
            status = itemView.findViewById(R.id.status);
        }
    }
}
