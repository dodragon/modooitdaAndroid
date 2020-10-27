package com.baobab.user.baobabflyer.server.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabStaffSign;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.CpStaffVO;

import java.text.SimpleDateFormat;
import java.util.List;

public class EmpUpdateListAdapter extends RecyclerView.Adapter<EmpUpdateListAdapter.ItemViewHolder> {

    List<CpStaffVO> list;
    Context context;
    SimpleDateFormat format;

    public EmpUpdateListAdapter(List<CpStaffVO> list, Context context) {
        this.list = list;
        this.context = context;

        format = new SimpleDateFormat("입사일 : yyyy-MM-dd");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emp_update_list, viewGroup, false);
        return new EmpUpdateListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final CpStaffVO vo = list.get(i);

        itemViewHolder.memberName.setText(vo.getStaffName() + rtnRank(vo.getDivCode()));
        if(vo.getDivCode().equals("c-01-01")){
            itemViewHolder.updateLayout.setVisibility(View.GONE);
        }else {
            itemViewHolder.updateLayout.setVisibility(View.VISIBLE);
            itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BaobabStaffSign.class);
                    intent.putExtra("cpSeq", vo.getCpSeq());
                    intent.putExtra("context", "update");
                    intent.putExtra("vo", vo);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
        }
        itemViewHolder.joinDate.setText(format.format(vo.getRegistDate()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String rtnRank(String code){
        if(code.equals("c-02-02")){
            return "매니저";
        }else if(code.equals("c-02-03")){
            return "직원";
        }else {
            return "사장님";
        }
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        LinearLayout updateLayout;
        TextView memberName;
        TextView joinDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            updateLayout = itemView.findViewById(R.id.updateLayout);
            memberName = itemView.findViewById(R.id.memberName);
            joinDate = itemView.findViewById(R.id.joinDate);
        }
    }
}
