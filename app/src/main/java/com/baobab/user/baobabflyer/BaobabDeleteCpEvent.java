package com.baobab.user.baobabflyer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.EventDeleteAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabDeleteCpEvent extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_delete_cp_event);

        int cpSeq = getIntent().getIntExtra("cpSeq", 0);

        Call<List<EventCpVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).cpAllEvent(cpSeq);
        call.enqueue(new Callback<List<EventCpVO>>() {
            @Override
            public void onResponse(Call<List<EventCpVO>> call, Response<List<EventCpVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<EventCpVO> list = response.body();

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabDeleteCpEvent.this);
                        recyclerView.setLayoutManager(layoutManager);

                        EventDeleteAdapter adapter = new EventDeleteAdapter(list, BaobabDeleteCpEvent.this, "삭제", 0);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    }else {
                        Log.d("eventDelete :::", "response 내용없음");
                        Toast.makeText(BaobabDeleteCpEvent.this, "데이터가 없거나 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }else {
                    Log.d("eventDelete :::", "서버로그 확인 필요");
                    Toast.makeText(BaobabDeleteCpEvent.this, "데이터가 없거나 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<List<EventCpVO>> call, Throwable t) {
                Log.d("eventDelete :::", t.getLocalizedMessage());
                Toast.makeText(BaobabDeleteCpEvent.this, "데이터가 없거나 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
