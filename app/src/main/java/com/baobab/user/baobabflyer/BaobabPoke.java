package com.baobab.user.baobabflyer;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.PokeAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.ShowPokeVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabPoke extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_call);

        String email = getSharedPreferences("user", 0).getString("email", "");
        final SharedPreferences locationSpf = getSharedPreferences("gps", MODE_PRIVATE);;
        double longitude = Double.parseDouble(locationSpf.getString("longitude", "0"));
        double latitude = Double.parseDouble(locationSpf.getString("latitude", "0"));

        findViewById(R.id.allCheckLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.checkbox_all).performClick();
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Call<List<ShowPokeVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).showPork(email, longitude, latitude);
        call.enqueue(new Callback<List<ShowPokeVO>>() {
            @Override
            public void onResponse(Call<List<ShowPokeVO>> call, Response<List<ShowPokeVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<ShowPokeVO> pokeList = response.body();

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabPoke.this);
                        recyclerView.setLayoutManager(layoutManager);

                        PokeAdapter adapter = new PokeAdapter(pokeList, (CheckBox) findViewById(R.id.checkbox_all), BaobabPoke.this, locationSpf, (ImageView) findViewById(R.id.deleteBtn));
                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    }else {
                        Log.d("포크리스트", "response 내용없음");
                        Toast.makeText(BaobabPoke.this, "조회된 찜한 내역이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d("포크리스트", "서버로그 확인 필요");
                    Toast.makeText(getApplicationContext(), "찜한 내역을 불러오는 중 문제가 발생했습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<ShowPokeVO>> call, Throwable t) {
                Log.d("포크리스트", t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "찜한 내역을 불러오는 중 문제가 발생했습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}