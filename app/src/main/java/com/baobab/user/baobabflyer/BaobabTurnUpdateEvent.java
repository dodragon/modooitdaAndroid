package com.baobab.user.baobabflyer;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.EventTurnUpdateAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.EventTurnItemTouchHelper;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabTurnUpdateEvent extends AppCompatActivity implements EventTurnUpdateAdapter.OnStartDragListener {

    RetroSingleTon retroSingleTon;

    ItemTouchHelper itemTouchHelper;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_turn_update_event);

        activityStart();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void activityStart(){
        context = this;
        int cpSeq = getIntent().getIntExtra("cpSeq", 0);

        Call<List<EventCpVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).cpEvent(cpSeq);
        call.enqueue(new Callback<List<EventCpVO>>() {
            @Override
            public void onResponse(Call<List<EventCpVO>> call, Response<List<EventCpVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<EventCpVO> list = response.body();

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabTurnUpdateEvent.this);
                        recyclerView.setLayoutManager(layoutManager);

                        EventTurnUpdateAdapter adapter = new EventTurnUpdateAdapter(list, BaobabTurnUpdateEvent.this, BaobabTurnUpdateEvent.this);
                        EventTurnItemTouchHelper callBack = new EventTurnItemTouchHelper(adapter);
                        itemTouchHelper = new ItemTouchHelper(callBack);
                        itemTouchHelper.attachToRecyclerView(recyclerView);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    }else {
                        Log.d("turnUpdateEvent ::", "response 내용없음");
                        Toast.makeText(BaobabTurnUpdateEvent.this, "죄송합니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }else {
                    Log.d("turnUpdateEvent ::", "서버로그 확인 필요");
                    Toast.makeText(BaobabTurnUpdateEvent.this, "죄송합니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<List<EventCpVO>> call, Throwable t) {
                Log.d("turnUpdateEvent ::", t.getLocalizedMessage());
                Toast.makeText(BaobabTurnUpdateEvent.this, "죄송합니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        activityStart();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onStartDrag(EventTurnUpdateAdapter.ItemViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }
}
