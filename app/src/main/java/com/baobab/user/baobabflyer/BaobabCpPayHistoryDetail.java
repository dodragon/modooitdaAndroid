package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.PaidHistoryDetailAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.CpPaidHistoryVO;
import com.baobab.user.baobabflyer.server.vo.PayMenusVO;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpPayHistoryDetail extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    String orderNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    public void settingAccounts(List<PayMenusVO> list, String ticketSerial){
        DecimalFormat format = new DecimalFormat("###,###");
        int allPaid = 0;
        int account = 0;
        for(int i=0;i<list.size();i++){
            allPaid += (list.get(i).getPrice());
            account += (list.get(i).getDisPrice());
        }

        ((TextView)findViewById(R.id.allPaid)).setText(format.format(allPaid) + "원");
        ((TextView)findViewById(R.id.discount)).setText(format.format(allPaid - account) + "원");
        ((TextView)findViewById(R.id.account)).setText(format.format(account) + "원");

        ((TextView)findViewById(R.id.orderNum)).setText(orderNum);
        ((TextView)findViewById(R.id.ticketNum)).setText(ticketSerial);
    }

    public void setListView(List<PayMenusVO> list){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        PaidHistoryDetailAdapter adapter = new PaidHistoryDetailAdapter(list);

        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_baobab_cp_pay_history_detail);
            orderNum = extras.getString("orderNum");
        } else {
            orderNum = getIntent().getStringExtra("orderNum");
        }
        callServer();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void callServer(){
        Call<CpPaidHistoryVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).paidHistory(orderNum);
        call.enqueue(new Callback<CpPaidHistoryVO>() {
            @Override
            public void onResponse(Call<CpPaidHistoryVO> call, Response<CpPaidHistoryVO> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        CpPaidHistoryVO vo = response.body();
                        settingAccounts(vo.getPayMenusVOS(), vo.getTicketSerial());
                        setListView(vo.getPayMenusVOS());
                    }else {
                        Log.d("결제승인내역 Detail", "response 내용없음");
                        Toast.makeText(BaobabCpPayHistoryDetail.this, "서버에서 내용을 받아오지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT);
                        finish();
                    }
                }else {
                    Log.d("결제승인내역 Detail", "서버로그 확인 필요");
                    Toast.makeText(BaobabCpPayHistoryDetail.this, "서버에서 내용을 받아오지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CpPaidHistoryVO> call, Throwable t) {
                Log.d("결제승인내역 Detail", t.getLocalizedMessage());
                Toast.makeText(BaobabCpPayHistoryDetail.this, "서버에서 내용을 받아오지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT);
                finish();
            }
        });
    }
}
