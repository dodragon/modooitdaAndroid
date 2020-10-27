package com.baobab.user.baobabflyer.server.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baobab.user.baobabflyer.BaobabCpEvent;
import com.baobab.user.baobabflyer.BaobabUpdateCpEvent;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDeleteAdapter extends RecyclerView.Adapter<EventDeleteAdapter.ItemViewHolder> {

    List<EventCpVO> list;
    Context context;
    String div;
    int mainSeq;

    List<String> checkedSerials;

    RetroSingleTon retroSingleTon;

    public EventDeleteAdapter(List<EventCpVO> list, Context context, String div, int mainSeq) {
        this.list = list;
        this.context = context;
        this.div = div;
        this.mainSeq = mainSeq;

        if(div.equals("삭제")){
            checkedSerials = new ArrayList<>();
            ((Activity)context).findViewById(R.id.saveBtn).setOnClickListener(save);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_delete_list, viewGroup, false);
        return new EventDeleteAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        final EventCpVO vo = list.get(i);

        itemViewHolder.eventName.setText(vo.getEventName());
        if(div.equals("삭제")){
            itemViewHolder.toggle.setChecked(false);
            itemViewHolder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        itemViewHolder.layout.setBackground(context.getDrawable(R.drawable.select_background));
                        checkedSerials.add(vo.getEventSerial());
                    }else {
                        itemViewHolder.layout.setBackground(context.getDrawable(R.drawable.search_item_background));
                        checkedSerials.remove(vo.getEventSerial());
                    }
                }
            });
        }else {
            itemViewHolder.toggle.setVisibility(View.GONE);
            itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alert = new AlertDialog.Builder(context)
                            .setIcon(context.getDrawable(R.drawable.logo_and))
                            .setTitle("이벤트")
                            .setMessage(vo.getEventName() + "\n이벤트를 수정하시겠습니까?")
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(context, BaobabUpdateCpEvent.class);
                                    intent.putExtra("vo", vo);
                                    intent.putExtra("mainSeq", mainSeq);
                                    context.startActivity(intent);
                                }
                            })
                            .create();
                    alert.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Gson gson = new Gson();
            String data = gson.toJson(checkedSerials);

            Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).deleteEvent(data);
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            int result = response.body();

                            if(result > 0){
                                Toast.makeText(context, "이벤트 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                ((BaobabCpEvent)BaobabCpEvent.context).onResume();
                                ((Activity)context).onBackPressed();
                            }else {
                                Toast.makeText(context, "다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.d("deleteEventSave :::", "response 내용없음");
                            Toast.makeText(context, "다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Log.d("deleteEventSave :::", "서버로그 확인 필요");
                        Toast.makeText(context, "다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.d("deleteEventSave :::", t.getLocalizedMessage());
                    Toast.makeText(context, "다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView eventName;
        ToggleButton toggle;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            eventName = itemView.findViewById(R.id.eventName);
            toggle = itemView.findViewById(R.id.toggle);
        }
    }
}
