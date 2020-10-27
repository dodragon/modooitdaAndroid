package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabMenu;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.util.MenuInfoDialog;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;

import java.text.DecimalFormat;
import java.util.List;

public class SelectedMenuAdapter extends RecyclerView.Adapter<SelectedMenuAdapter.ItemViewHolder> {

    public static List<EventCpMenuVO> list;
    Context context;
    Button sellBtn;
    EventCpMenuVO selectedVO;
    DecimalFormat format;

    public SelectedMenuAdapter(List<EventCpMenuVO> list, Context context, Button sellBtn, EventCpMenuVO selectedVO) {
        this.list = list;
        this.context = context;
        this.sellBtn = sellBtn;
        this.selectedVO = selectedVO;

        format = new DecimalFormat("###,###");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.selected_menu, viewGroup, false);
        return new SelectedMenuAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, final int i) {
        final EventCpMenuVO vo = list.get(i);

        if(i == 0){
            Toast.makeText(context, vo.getMenuName() + "가 주문함에 담겼습니다", Toast.LENGTH_SHORT).show();
        }

        itemViewHolder.menuName.setText(vo.getMenuName());
        itemViewHolder.ea.setText(String.valueOf(vo.getSelectedEa()));
        itemViewHolder.price.setText(format.format(vo.getDisPrice() * vo.getSelectedEa()) + "원");

        itemViewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curEa = Integer.parseInt(itemViewHolder.ea.getText().toString());
                curEa += 1;
                itemViewHolder.ea.setText(String.valueOf(curEa));
                int price = vo.getDisPrice() * curEa;
                itemViewHolder.price.setText(format.format(price) + "원");
                sellBtn.setText(makeSellBtnText(vo.getDisPrice(), true));
                sellBtn.setBackgroundColor(Color.parseColor("#5c7cfa"));
                list.get(i).setSelectedEa(curEa);
            }
        });

        itemViewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curEa = Integer.parseInt(itemViewHolder.ea.getText().toString());
                if(curEa == 1){
                    Toast.makeText(context, "1개 이하로 구매 하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    curEa = curEa - 1;
                    itemViewHolder.ea.setText(String.valueOf(curEa));
                    int price = vo.getDisPrice() * curEa;
                    itemViewHolder.price.setText(format.format(price) + "원");
                    sellBtn.setText(makeSellBtnText(vo.getDisPrice(), false));
                    list.get(i).setSelectedEa(curEa);
                }
            }
        });

        itemViewHolder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(i).setSelectedEa(0);
                list.remove(vo);
                notifyDataSetChanged();
                sellBtn.setText(clearSellBtnText(Integer.parseInt(itemViewHolder.price.getText().toString().replaceAll("원", "").replaceAll(",", ""))));
            }
        });

        itemViewHolder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuInfoDialog dialog = MenuInfoDialog.newInstance(context, vo.getOptionSerial(), vo);
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
                dialog.show(((BaobabMenu)context).getSupportFragmentManager(), "");
            }
        });

        if(vo.getSelectedEa() == 0){
            itemViewHolder.add.performClick();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String makeSellBtnText(int onePrice, boolean isAdd){
        String text = sellBtn.getText().toString();
        if(text.equals("구매하기")){
            return format.format(onePrice) + "원 구매하기";
        }else {
            int pastPrice = Integer.parseInt(text.split(" ")[0].replaceAll("원", "").replaceAll(",", ""));
            if(isAdd){
                return format.format(pastPrice + onePrice) + "원 구매하기";
            }else {
                return format.format(pastPrice - onePrice) + "원 구매하기";
            }
        }
    }

    public String clearSellBtnText(int minusPrice){
        String text = sellBtn.getText().toString();
        int pastPrice = Integer.parseInt(text.split(" ")[0].replaceAll("원", "").replaceAll(",", ""));
        int curPrice = pastPrice - minusPrice;

        if(curPrice <= 0){
            sellBtn.setBackgroundColor(Color.rgb(216, 220, 229));
           return "구매하기";
        }else {
            return format.format(curPrice) + "원 구매하기";
        }
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView menuName;
        ImageView clear;
        ImageView del;
        ImageView add;
        TextView ea;
        TextView price;
        LinearLayout info;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            menuName = itemView.findViewById(R.id.menuName);
            clear = itemView.findViewById(R.id.clear);
            del = itemView.findViewById(R.id.del);
            add = itemView.findViewById(R.id.add);
            ea = itemView.findViewById(R.id.ea);
            price = itemView.findViewById(R.id.price);
            info = itemView.findViewById(R.id.info);
        }
    }
}
