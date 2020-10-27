package com.baobab.user.baobabflyer.server.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabTurnUpdateEvent;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.listener.OnItemMovementListener;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTurnUpdateAdapter  extends RecyclerView.Adapter<EventTurnUpdateAdapter.ItemViewHolder> implements OnItemMovementListener{

    RetroSingleTon retroSingleTon;

    List<EventCpVO> list;
    Context context;
    OnStartDragListener startDragListener;

    public EventTurnUpdateAdapter(List<EventCpVO> list, Context context, OnStartDragListener startDragListener) {
        this.list = list;
        this.context = context;
        this.startDragListener = startDragListener;

        ((Activity)context).findViewById(R.id.saveBtn).setOnClickListener(save);
    }

    public interface OnStartDragListener {
        void onStartDrag(EventTurnUpdateAdapter.ItemViewHolder viewHolder);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_turn_update_list, viewGroup, false);
        return new EventTurnUpdateAdapter.ItemViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        final EventCpVO vo = list.get(i);

        itemViewHolder.eventName.setText(vo.getEventName());
        itemViewHolder.turnNum.setText(String.valueOf(i+1));
        itemViewHolder.layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN){
                    startDragListener.onStartDrag(itemViewHolder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView eventName;
        TextView turnNum;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            eventName = itemView.findViewById(R.id.eventName);
            turnNum = itemView.findViewById(R.id.turnNum);
        }
    }

    View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i=0;i<list.size();i++) {
                list.get(i).setTurnNum(i+1);
            }

            Gson gson = new Gson();
            String data = gson.toJson(list);
            Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).turnUpdateEvent(data);
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            int result = response.body();

                            if(result > 0){
                                ((BaobabTurnUpdateEvent)BaobabTurnUpdateEvent.context).onResume();
                                Toast.makeText(context, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "죄송합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.d("turnUpdate ::: ", "response 내용없음");
                            Toast.makeText(context, "죄송합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Log.d("turnUpdate ::: ", "서버로그 확인 필요");
                        Toast.makeText(context, "죄송합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.d("turnUpdate ::: ", t.getLocalizedMessage());
                    Toast.makeText(context, "죄송합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
