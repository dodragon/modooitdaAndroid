package com.baobab.user.baobabflyer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.baobab.user.baobabflyer.server.adapter.NoticeListAdapter;
import com.baobab.user.baobabflyer.server.vo.NoticeVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabNotice extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_notice);

        searchNotice(getSharedPreferences("user", 0).getString("divCode", ""));

        findViewById(R.id.bankBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void searchNotice(String readDiv){
        Call<List<NoticeVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getNoti(readDiv);
        call.enqueue(new Callback<List<NoticeVO>>() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<List<NoticeVO>> call, Response<List<NoticeVO>> response) {
                if(response.isSuccessful()){
                    if(response != null){
                        Log.d("공지사항 검색 ", "통신완료");
                        List<NoticeVO> notiList = response.body();

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabNotice.this);
                        recyclerView.setLayoutManager(layoutManager);

                        NoticeListAdapter adapter = new NoticeListAdapter(notiList, BaobabNotice.this);
                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    }else {
                        Log.d("공지사항 검색 ", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("공지사항 검색 ", "서버에러");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<NoticeVO>> call, Throwable t) {
                Log.d("공지사항 검색 ", "통신에러 : " + t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }
}