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
import com.baobab.user.baobabflyer.server.vo.EventCpVO;

import java.util.List;

public class SelectEventListAdapter extends RecyclerView.Adapter<SelectEventListAdapter.ItemViewHolder>{

    List<EventCpVO> list;
    View eventBtn;
    View optionBtn;
    View menuBtn;
    View optionLayout;
    View menuLayout;
    TextView eventTv;
    TextView optionTv;
    RecyclerView optionRecyclerView;
    RecyclerView menuRecyclerView;
    Context context;
    RecyclerView selectedRecyclerView;
    Button sellBtn;
    List<EventCpMenuVO> selectedList;
    SelectedMenuAdapter selectedMenuAdapter;

    boolean isSelected;
    String selectedE;
    String selectedO;
    int selectedM;

    public SelectEventListAdapter(List<EventCpVO> list, View eventBtn, View optionBtn, View menuBtn, View optionLayout, View menuLayout, TextView eventTv, TextView optionTv, RecyclerView optionRecyclerView,
                                  RecyclerView menuRecyclerView, Context context, RecyclerView selectedRecyclerView, Button sellBtn, List<EventCpMenuVO> selectedList, SelectedMenuAdapter selectedMenuAdapter,
                                  boolean isSelected, String selectedE, String selectedO, int selectedM) {
        this.list = list;
        this.eventBtn = eventBtn;
        this.optionBtn = optionBtn;
        this.menuBtn = menuBtn;
        this.optionLayout = optionLayout;
        this.menuLayout = menuLayout;
        this.eventTv = eventTv;
        this.optionTv = optionTv;
        this.optionRecyclerView = optionRecyclerView;
        this.menuRecyclerView = menuRecyclerView;
        this.context = context;
        this.selectedRecyclerView = selectedRecyclerView;
        this.sellBtn = sellBtn;
        this.selectedList = selectedList;
        this.selectedMenuAdapter = selectedMenuAdapter;

        this.isSelected = isSelected;
        this.selectedE = selectedE;
        this.selectedO = selectedO;
        this.selectedM = selectedM;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_event_list, viewGroup, false);
        return new SelectEventListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        final EventCpVO vo = list.get(i);

        itemViewHolder.eventNum.setText("이벤트 " + (i+1));
        itemViewHolder.eventName.setText(vo.getEventName());

        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionLayout.setVisibility(View.VISIBLE);
                menuLayout.setVisibility(View.GONE);
                eventTv.setText(vo.getEventName());

                RecyclerView recyclerView = optionRecyclerView;
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);

                SelectOptionListAdapter adapter = new SelectOptionListAdapter(vo.getOptionList(), optionBtn, menuBtn, menuLayout, optionTv, menuRecyclerView, context, selectedRecyclerView, sellBtn, selectedList, selectedMenuAdapter,
                        isSelected, selectedO, selectedM);

                recyclerView.removeAllViewsInLayout();
                recyclerView.setAdapter(adapter);

                eventBtn.performClick();
            }
        });

        if(isSelected && vo.getEventSerial().equals(selectedE)){
            itemViewHolder.layout.performClick();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView eventNum;
        TextView eventName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            eventNum = itemView.findViewById(R.id.eventNum);
            eventName = itemView.findViewById(R.id.eventName);
        }
    }
}
