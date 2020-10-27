package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.util.MenuInfoDialog;
import com.baobab.user.baobabflyer.server.util.MenuSelectDialog;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;

import java.text.DecimalFormat;
import java.util.List;

public class MenuMenuAdapter extends RecyclerView.Adapter<MenuMenuAdapter.ItemViewHolder> {

    List<EventCpMenuVO> list;
    DecimalFormat format;
    Context context;

    String cpName;
    int cpSeq;

    String eventSerial;

    public MenuMenuAdapter(List<EventCpMenuVO> list, Context context, String cpName, int cpSeq, String eventSerial) {
        this.list = list;
        this.context = context;

        this.cpName = cpName;
        this.cpSeq = cpSeq;
        this.eventSerial = eventSerial;

        format = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_menu_list, viewGroup, false);
        return new MenuMenuAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final EventCpMenuVO vo = list.get(i);

        itemViewHolder.menuName.setText(vo.getMenuName());
        itemViewHolder.disPrice.setText(format.format(vo.getDisPrice()) + "원");
        if(vo.getPercentAge() == 0){
            itemViewHolder.percentAge.setVisibility(View.GONE);
            itemViewHolder.price.setVisibility(View.GONE);
        }else {
            itemViewHolder.percentAge.setText(vo.getPercentAge() + "%");
            itemViewHolder.price.setText(format.format(vo.getPrice()) + "원");
            itemViewHolder.price.setPaintFlags(itemViewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if(vo.getMenuInfo().length() <= 0){
            itemViewHolder.detailLayout.setVisibility(View.GONE);
        }else{
            itemViewHolder.detailLayout.setVisibility(View.VISIBLE);
            itemViewHolder.detailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MenuInfoDialog dialog = MenuInfoDialog.newInstance(context, vo.getOptionSerial(), vo);
                    dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
                    dialog.show(((BaobabMenu)context).getSupportFragmentManager(), "");
                }
            });
        }

        itemViewHolder.menuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = context.getSharedPreferences("user", 0);
                if(spf.getString("email", "").equals("")){
                    Toast.makeText(context, "로그인 이후 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                }else {
                    MenuSelectDialog dialog = MenuSelectDialog.newInstance(cpSeq, context, cpName, true, eventSerial, vo.getOptionSerial(), vo.getSeqNum());
                    dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
                    dialog.show(((BaobabMenu)context).getSupportFragmentManager(), "");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView menuName;
        TextView percentAge;
        TextView disPrice;
        TextView price;
        RelativeLayout detailLayout;
        LinearLayout menuLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            menuName = itemView.findViewById(R.id.menuName);
            percentAge = itemView.findViewById(R.id.percentAge);
            disPrice = itemView.findViewById(R.id.disPrice);
            price = itemView.findViewById(R.id.price);
            detailLayout = itemView.findViewById(R.id.detailLayout);
            menuLayout = itemView.findViewById(R.id.menuLayout);
        }
    }
}
