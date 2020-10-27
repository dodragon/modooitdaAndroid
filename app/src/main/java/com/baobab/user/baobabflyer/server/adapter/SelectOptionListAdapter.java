package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;
import com.baobab.user.baobabflyer.server.vo.EventCpOptionVO;

import java.util.List;

public class SelectOptionListAdapter  extends RecyclerView.Adapter<SelectOptionListAdapter.ItemViewHolder> {

    List<EventCpOptionVO> list;
    View optionBtn;
    View menuBtn;
    View menuLayout;
    TextView optionTv;
    RecyclerView menuRecyclerView;
    Context context;
    RecyclerView selectedRecyclerView;
    Button sellBtn;
    List<EventCpMenuVO> selectedList;
    SelectedMenuAdapter selectedMenuAdapter;

    boolean isSelected;
    String selectedO;
    int selectedM;

    public SelectOptionListAdapter(List<EventCpOptionVO> list, View optionBtn, View menuBtn, View menuLayout, TextView optionTv, RecyclerView menuRecyclerView, Context context, RecyclerView selectedRecyclerView, Button sellBtn,
                                   List<EventCpMenuVO> selectedList, SelectedMenuAdapter selectedMenuAdapter, boolean isSelected, String selectedO, int selectedM) {
        this.list = list;
        this.optionBtn = optionBtn;
        this.menuBtn = menuBtn;
        this.menuLayout = menuLayout;
        this.optionTv = optionTv;
        this.menuRecyclerView = menuRecyclerView;
        this.context = context;
        this.selectedRecyclerView = selectedRecyclerView;
        this.sellBtn = sellBtn;
        this.selectedList = selectedList;
        this.selectedMenuAdapter = selectedMenuAdapter;

        this.isSelected = isSelected;
        this.selectedO = selectedO;
        this.selectedM = selectedM;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_event_list, viewGroup, false);
        return new SelectOptionListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final EventCpOptionVO vo = list.get(i);

        itemViewHolder.optionNum.setText("옵션 " + (i+1));
        itemViewHolder.optionName.setText(vo.getOptionName());

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLayout.setVisibility(View.VISIBLE);
                optionTv.setText(vo.getOptionName());

                RecyclerView recyclerView = menuRecyclerView;
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);

                SelectMenuListAdapter adapter = new SelectMenuListAdapter(vo.getMenuList(), menuBtn, context, selectedRecyclerView, sellBtn, selectedList, selectedMenuAdapter, isSelected, selectedM);

                recyclerView.removeAllViewsInLayout();
                recyclerView.setAdapter(adapter);

                optionBtn.performClick();
            }
        });

        if(isSelected && vo.getOptionSerial().equals(selectedO)){
            itemViewHolder.layout.performClick();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView optionNum;
        TextView optionName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            optionNum = itemView.findViewById(R.id.eventNum);
            optionName = itemView.findViewById(R.id.eventName);
        }
    }
}
