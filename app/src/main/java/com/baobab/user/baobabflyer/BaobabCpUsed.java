package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.vo.AutoMappingVO;
import com.baobab.user.baobabflyer.server.singleTons.BizcallSingleTon;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.Scanner;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpUsed extends AppCompatActivity {

    RetroSingleTon retroSingleTon;
    BizcallSingleTon bizcallSingleTon;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss");
    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    String orderNum;
    PaymentVO vo;
    String backContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());

        //리스트 중복시 아래 삭제 고민 필요
        orderNum = getIntent().getStringExtra("orderNum");
        getList(orderNum);
    }

    public void getList(String orderNum){
        Button scanner = findViewById(R.id.scanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabCpUsed.this, Scanner.class);
                startActivity(intent);
            }
        });

        Call<PaymentVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).payDetail(orderNum);
        call.enqueue(new Callback<PaymentVO>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(Call<PaymentVO> call, Response<PaymentVO> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d("통신 완료 : ", "결제내역 디테일 리스트 완료");
                        vo = response.body();
                        settingUI(vo);
                        LinearLayout callBtn = findViewById(R.id.call);
                        callBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                findUserPhone(vo.getEmail());
                            }
                        });
                    }else {
                        Log.d("통신 실패 : ", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("통신 실패 : ", "서버에러");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PaymentVO> call, Throwable t) {
                Log.d("통신 실패 : ", "통신에러 : " + t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void settingUI(PaymentVO vo){
        TextView orderNum = findViewById(R.id.orderNum);
        orderNum.setText("예약번호 : " + vo.getOrderNum());

        TextView payDate = findViewById(R.id.payDate);
        payDate.setText("결제일 : " + dateFormat.format(vo.getPayDate()));

        if(vo.getUseDate() != null){
            TextView useDate = findViewById(R.id.useDate);
            useDate.setText("사용일 : " + dateFormat.format(vo.getUseDate()));
        }

        TextView consumerId = findViewById(R.id.consumerId);
        consumerId.setText("고객계정 : " + vo.getEmail());

        TextView used = findViewById(R.id.used);
        used.setText(vo.getUsed());

        TextView totalPrice = findViewById(R.id.totalPrice);
        totalPrice.setText(decimalFormat.format(vo.getTotalPrice()) + "원");

        TextView totalDisPrice = findViewById(R.id.totalDisPrice);
        totalDisPrice.setText(decimalFormat.format(vo.getTotalPrice() - vo.getTotalDisPrice()) + "원");

        TextView totalSales = findViewById(R.id.totalSales);
        totalSales.setText(decimalFormat.format(vo.getTotalDisPrice()) + "원");



        makeList(new String[]{"구구구", "구구"}, null);
    }

    View.OnClickListener doReceipt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            v.setVisibility(View.GONE);
            Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).receipt(orderNum);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("통신 완료 : ", "완료");
                    Toast.makeText(BaobabCpUsed.this, "접수가 완료되었습니다.", Toast.LENGTH_LONG).show();

                    Call<Integer> pushCustomer = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).payPush("[접수] 접수가 완료되었습니다.", "주문번호 : " + vo.getOrderNum(),
                            vo.getEmail(), vo.getCpSeq(), "customer");
                    pushCustomer.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    Log.d("payUsed push 통신완료", "완료");
                                    int result = response.body();
                                    if(result > 0){
                                        Log.d("payUsed push 통신완료", "send push complete");
                                    }else {
                                        Log.d("payUsed push 통신완료", "서버로그 확인 필");
                                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                        intent.putExtra("status", "오류");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else {
                                    Log.d("payUsed push 통신실패", "response 내용없음");
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                Log.d("payUsed push 통신실패", "서버로그 확인 필");
                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                intent.putExtra("status", "오류");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.d("payUsed push 통신실패", t.getLocalizedMessage());
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    });

                    Intent intent = new Intent(BaobabCpUsed.this, BaobabCpUsed.class);
                    intent.putExtra("orderNum", orderNum);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("통신 실패 : ", "통신에러 : " + t.getLocalizedMessage());
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            });
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void makeList(String[] menus, String[] prices){
        LinearLayout mother = findViewById(R.id.listMother);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size5 = Math.round( 5 * dm.density );

        for(int i=0;i<menus.length;i++){
            LinearLayout first = new LinearLayout(BaobabCpUsed.this);
            LinearLayout.LayoutParams firstParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            firstParam.setMargins(0, size5, 0, size5);
            first.setWeightSum(1);
            first.setOrientation(LinearLayout.HORIZONTAL);
            mother.addView(first);

            TextView menuName = new TextView(BaobabCpUsed.this);
            menuName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.5));
            menuName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            menuName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            menuName.setTypeface(Typeface.DEFAULT_BOLD);
            menuName.setText(menus[i]);
            first.addView(menuName);

            TextView price = new TextView(BaobabCpUsed.this);
            price.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.5));
            price.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            price.setTypeface(Typeface.DEFAULT_BOLD);
            price.setTextColor(Color.RED);
            price.setText(decimalFormat.format(Integer.parseInt(prices[i])) + "원");
            first.addView(price);
        }
    }

    public void findUserPhone(String email){
        Call<String> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getUserPhone(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d("통신 완료 : ", "완료");
                        String phoneNum = response.body();
                        calling(phoneNum);
                    }else {
                        Log.d("통신 실패 : ", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("통신 실패 : ", "서버에러");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("통신 실패 : ", "통신에러 : " + t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    public void calling(String phoneNum){
        String iid = "fspsjeieh488vnfoebso";
        final String rn = phoneNum;
        Log.d("폰번호는 :: ", phoneNum);
        BaobabMenu baobabMenu = new BaobabMenu();
        String auth = new String( org.apache.commons.codec.binary.Base64.encodeBase64( baobabMenu.md5( iid + rn ).getBytes() ) );
        Call<AutoMappingVO> biz = bizcallSingleTon.getInstance().getBizcallInterface().autoMapping( iid, rn, "consumer", auth );
        biz.enqueue( new Callback<AutoMappingVO>() {
            @Override
            public void onResponse(Call<AutoMappingVO> call, Response<AutoMappingVO> response) {
                AutoMappingVO vo = response.body();
                final String telNum = vo.getVn();
                Log.d("번호는 :: ", telNum);
                Intent callTo = new Intent( Intent.ACTION_VIEW, Uri.parse( "tel:" + telNum ) );
                startActivity( callTo );
                Log.d( "맵핑 : ", "성공" );
            }

            @Override
            public void onFailure(Call<AutoMappingVO> call, Throwable t) {
                Log.d( "통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage() );
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        } );
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            setContentView(R.layout.activity_baobab_cp_used);
            if (extras.getString("context") != null) {
                backContext = extras.getString("context");
            }
            orderNum = extras.getString("orderNum");
            getList(orderNum);
        } else {
            Log.d("엑스트라 : ", "널 !!!!!!!!!");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = null;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(backContext != null && backContext.equals("push")){
                    intent = new Intent(this, BaobabAnterMain.class);
                    startActivity(intent);
                    finish();
                }else {
                    onBackPressed();
                }
        }
        return false;
    }
}