package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.PushPayVO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabPushPayHistory extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_push_pay_history);

        requestData(getSharedPreferences("user", 0).getString("email", ""));
    }

    public void requestData(String email){
        Call<List<PushPayVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).pushPaySelect(email);
        call.enqueue(new Callback<List<PushPayVO>>() {
            @Override
            public void onResponse(Call<List<PushPayVO>> call, Response<List<PushPayVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<PushPayVO> list = response.body();

                        LinearLayout motherLayout = findViewById(R.id.mother);

                        for(int i=0;i<list.size();i++){
                            motherLayout.addView(makeList(list.get(i)));
                        }
                    }else {
                        Log.d("서버 에러 : ", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("서버 에러 : ", "서버로그확인필요");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<PushPayVO>> call, Throwable t) {
                Log.d("통신 실패 : ", t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    public LinearLayout makeList(PushPayVO vo){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size10 = Math.round( 10 * dm.density );

        LinearLayout linearLayout = new LinearLayout(BaobabPushPayHistory.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(size10, size10, size10, size10);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setElevation(size10/2);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor(Color.WHITE);

        LinearLayout textLayout = new LinearLayout(BaobabPushPayHistory.this);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(textLayout);

        TextView menuTv = new TextView(BaobabPushPayHistory.this);
        LinearLayout.LayoutParams menuTvParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        menuTvParam.setMargins(size10/2, size10/2, size10/2, size10/2);
        menuTv.setLayoutParams(menuTvParam);
        menuTv.setText(vo.getMenu_name());
        menuTv.setTextColor(Color.BLACK);
        menuTv.setTypeface(Typeface.DEFAULT_BOLD);
        menuTv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        menuTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textLayout.addView(menuTv);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        TextView dateTv = new TextView(BaobabPushPayHistory.this);
        LinearLayout.LayoutParams dateTvParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dateTvParam.setMargins(size10/2, size10/2, size10/2, size10/2);
        dateTv.setLayoutParams(dateTvParam);
        dateTv.setText(simpleDateFormat.format(vo.getBuy_date()));
        dateTv.setTextColor(Color.rgb(153, 56, 0));
        dateTv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        dateTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textLayout.addView(dateTv);

        DecimalFormat formatter = new DecimalFormat("###,###");

        TextView priceTv = new TextView(BaobabPushPayHistory.this);
        LinearLayout.LayoutParams priceTvParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        priceTvParam.rightMargin = size10;
        priceTv.setLayoutParams(priceTvParam);
        priceTv.setText(formatter.format(vo.getPay()) + "원");
        priceTv.setTextColor(Color.RED);
        priceTv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        priceTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        priceTv.setGravity(Gravity.CENTER);
        linearLayout.addView(priceTv);

        return linearLayout;
    }

    @Override
    public void onBackPressed(){
        /*Intent intent = new Intent(BaobabPushPayHistory.this, BaobabStatisticsMain.class);
        intent.putExtra("cpName", getIntent().getStringExtra("cpName"));
        intent.putExtra("cpSeq", getIntent().getIntExtra("cpSeq", 0));
        startActivity(intent);
        finish();*/
    }
}
