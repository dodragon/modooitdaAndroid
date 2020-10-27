package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeEventAdapter extends PagerAdapter {

    RetroSingleTon retroSingleTon;

    List<EventCpVO> list;
    Context context;
    int mainSeq;

    boolean isFirst;

    public MakeEventAdapter(List<EventCpVO> list, Context context, int mainSeq) {
        this.list = list;
        this.context = context;
        this.mainSeq = mainSeq;

        isFirst = true;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.event_cp_list, null);

        final EventCpVO vo = list.get(position);

        ((TextView)view.findViewById(R.id.eventName)).setText(vo.getEventName());

        Switch onOffSwitch = view.findViewById(R.id.onOffSwitch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFirst = false;

                String onAndOff;
                if(isChecked){
                    onAndOff = "open";
                }else {
                    onAndOff = "close";
                }

                setOnOff(vo.getEventSerial(), onAndOff, vo.getCpSeq(), buttonView);
            }
        });

        if(vo.getEventStatus().equals("open")){
            onOffSwitch.setChecked(true);
        }else {
            onOffSwitch.setChecked(false);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        EventOptionAdapter adapter = new EventOptionAdapter(vo.getOptionList(), context, vo.getCpSeq(), mainSeq);

        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(adapter);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    public void setOnOff(String eventSerial, final String onOff, int cpSeq, final CompoundButton button){
        Call<String> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).eventOnOff(eventSerial, onOff, mainSeq, cpSeq);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        String result = response.body();
                        if(!isFirst){
                            if(result.equals("success")){
                                if(onOff.equals("open")){
                                    Toast.makeText(context, "해당 이벤트를 오픈 하였습니다. 소비자에게 노출됩니다.", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, "해당 이벤트를 닫았습니다. 소비자에게 노출되지 않습니다..", Toast.LENGTH_SHORT).show();
                                }
                            }else if(result.equals("isMain")){
                                Toast.makeText(context, "메인 항목이 설정되있는 이벤트는 off를 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                button.setChecked(true);
                            }else if(result.equals("allClose")){
                                Toast.makeText(context, "다른 이벤트 들이 모두 off상태 입니다.", Toast.LENGTH_SHORT).show();
                                button.setChecked(true);
                            }
                        }
                    }else {
                        Toast.makeText(context, "on/off 실패", Toast.LENGTH_SHORT).show();
                        Log.d("이벤트 OnOff", "response 내용없음");
                    }
                }else {
                    Toast.makeText(context, "on/off 실패", Toast.LENGTH_SHORT).show();
                    Log.d("이벤트 OnOff", "서버로그 확인 필요");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("이벤트 OnOff", t.getLocalizedMessage());
            }
        });
    }
}
