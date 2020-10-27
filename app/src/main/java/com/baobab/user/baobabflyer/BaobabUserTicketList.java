package com.baobab.user.baobabflyer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baobab.user.baobabflyer.server.adapter.UserCouponHistoryListAdapter;
import com.baobab.user.baobabflyer.server.adapter.UserCouponListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.UserTicketHistoryVO;
import com.baobab.user.baobabflyer.server.vo.UserTicketVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabUserTicketList extends AppCompatActivity {

    RetroSingleTon retroSingleTon;
    public static Context CONTEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    public void unUseCoupon(String email) {
        Call<List<UserTicketVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getUserTicket(email);
        call.enqueue(new Callback<List<UserTicketVO>>() {
            @Override
            public void onResponse(Call<List<UserTicketVO>> call, Response<List<UserTicketVO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<UserTicketVO> list = response.body();

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CONTEXT);
                        recyclerView.setLayoutManager(layoutManager);

                        boolean isLinkAndOne = getIntent().getBooleanExtra("isLinkAndOne", false);
                        String selectedSerial = "";
                        if(isLinkAndOne){
                            selectedSerial = getIntent().getStringExtra("serial");
                        }

                        UserCouponListAdapter adapter = new UserCouponListAdapter(list, CONTEXT, selectedSerial);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.d("티켓함(미사용)", "response 내용없음");
                        Intent intent = new Intent(CONTEXT, BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.d("티켓함(미사용)", "서버 로그 확인 필요");
                    Intent intent = new Intent(CONTEXT, BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<UserTicketVO>> call, Throwable t) {
                Log.d("티켓함(미사용)", "통신 에러" + t.getLocalizedMessage());
                Intent intent = new Intent(CONTEXT, BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    public void usedCoupon(String email) {
        Call<List<UserTicketHistoryVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).useTicketHistory(email);
        call.enqueue(new Callback<List<UserTicketHistoryVO>>() {
            @Override
            public void onResponse(Call<List<UserTicketHistoryVO>> call, Response<List<UserTicketHistoryVO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<UserTicketHistoryVO> list = response.body();

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CONTEXT);
                        recyclerView.setLayoutManager(layoutManager);

                        UserCouponHistoryListAdapter adapter = new UserCouponHistoryListAdapter(list, CONTEXT);

                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.d("티켓함(사용)", "response 내용없음");
                        Intent intent = new Intent(CONTEXT, BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.d("티켓함(사용)", "서버 로그 확인 필요");
                    Intent intent = new Intent(CONTEXT, BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<UserTicketHistoryVO>> call, Throwable t) {
                Log.d("티켓함(사용)", "통신 에러" + t.getLocalizedMessage());
                Intent intent = new Intent(CONTEXT, BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    View.OnClickListener mainBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = getSharedPreferences("user", 0).getString("email", "");
            ((Button) v).setTextColor(Color.parseColor("#5c7cfa"));

            switch (v.getId()) {
                case R.id.unUse:
                    unUseCoupon(email);
                    ((Button) findViewById(R.id.used)).setTextColor(Color.BLACK);
                    findViewById(R.id.unUse_line).setVisibility(View.VISIBLE);
                    findViewById(R.id.used_line).setVisibility(View.GONE);
                    break;
                case R.id.used:
                    usedCoupon(email);
                    ((Button) findViewById(R.id.unUse)).setTextColor(Color.BLACK);
                    findViewById(R.id.unUse_line).setVisibility(View.GONE);
                    findViewById(R.id.used_line).setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();

        setContentView(R.layout.activity_baobab_user_coupon_list);
        CONTEXT = this;

        findViewById(R.id.unUse).setOnClickListener(mainBtnListener);
        findViewById(R.id.used).setOnClickListener(mainBtnListener);

        findViewById(R.id.unUse).performClick();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        int pushResult = 0;
        if(extras != null){
            pushResult += extras.getInt("div");
        }
        if (pushResult == 858) {
            findViewById(R.id.used).performClick();
        }
    }

    @Override
    public void onBackPressed(){
        boolean isLink = getIntent().getBooleanExtra("isLink", false);
        boolean isLinkAndOne = getIntent().getBooleanExtra("isLinkAndOne", false);
        if(isLink | isLinkAndOne){
            Intent intent = new Intent(this, BaobabAnterMain.class);
            startActivity(intent);
            finish();
        }else {
            finish();
        }
    }
}
