package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpPaymentList extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_payment_list);

        SharedPreferences spf = getSharedPreferences("user", 0);
        String email = spf.getString("email", "");

        rtnUsed(email);

        RadioButton allSee = findViewById(R.id.allSee);
        allSee.setChecked(true);
    }

    public void rtnUsed(final String email){
        RadioGroup radioGroup = findViewById(R.id.usedRadio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                getList(email, radioButton.getText().toString());
            }
        });
    }

    public void getList(String email, String used){
        Call<List<PaymentVO>> call = retroSingleTon.getInstance().getRetroInterface(BaobabCpPaymentList.this).payCpList(email, used);
        call.enqueue(new Callback<List<PaymentVO>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<List<PaymentVO>> call, Response<List<PaymentVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d("통신 완료 : ", "결제내역 리스트 완료");
                        List<PaymentVO> list = response.body();
                        makeList(list);
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
            public void onFailure(Call<List<PaymentVO>> call, Throwable t) {
                Log.d("통신 실패 : ", "통신에러 : " + t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void makeList(final List<PaymentVO> list){
        LinearLayout mother = findViewById(R.id.mother);
        mother.removeAllViews();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size10 = Math.round( 10 * dm.density );
        int size5 = Math.round( 5 * dm.density );
        int size2 = Math.round( 2 * dm.density );
        int size30 = Math.round( 40 * dm.density );

        for(int i=0;i<list.size();i++){
            final int position = i;

            LinearLayout first = new LinearLayout(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams firstParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            first.setLayoutParams(firstParam);
            first.setGravity(Gravity.CENTER);
            first.setOrientation(LinearLayout.VERTICAL);
            first.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaobabCpPaymentList.this, BaobabCpUsed.class);
                    intent.putExtra("orderNum", list.get(position).getOrderNum());
                    startActivity(intent);
                }
            });
            mother.addView(first);

            LinearLayout second = new LinearLayout(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams secondParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            second.setLayoutParams(secondParam);
            second.setOrientation(LinearLayout.VERTICAL);
            second.setGravity(Gravity.CENTER);
            first.addView(second);

            TextView orderNumTv = new TextView(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams orderNumParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            orderNumTv.setLayoutParams(orderNumParam);
            orderNumTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            orderNumTv.setTypeface(Typeface.DEFAULT_BOLD);
            orderNumTv.setTextColor(Color.BLACK);
            orderNumTv.setText("예약번호 : " + list.get(i).getOrderNum());
            second.addView(orderNumTv);

            LinearLayout third = new LinearLayout(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams thirdParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            thirdParam.setMargins(size10, 0, size10, 0);
            third.setLayoutParams(thirdParam);
            third.setGravity(Gravity.CENTER);
            third.setOrientation(LinearLayout.HORIZONTAL);
            third.setWeightSum(1);
            first.addView(third);

            LinearLayout fourth = new LinearLayout(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams fourthParam = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            fourthParam.weight = (float)0.45;
            fourth.setGravity( Gravity.CENTER );
            fourth.setLayoutParams(fourthParam);
            fourth.setOrientation(LinearLayout.VERTICAL);
            third.addView(fourth);

            TextView usedTv = new TextView(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams usedParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            usedTv.setLayoutParams(usedParam);
            usedTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            if(list.get(i).getUsed().equals("미사용")){
                usedTv.setTextColor(Color.RED);
            }else {
                usedTv.setTextColor(Color.BLACK);
            }

            fourth.addView(usedTv);

            TextView emailTv = new TextView(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams emailParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            emailParam.setMargins(0, size5, 0, size5);
            emailTv.setLayoutParams(emailParam);
            emailTv.setTextColor(Color.BLACK);
            emailTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            emailTv.setTypeface(Typeface.DEFAULT_BOLD);
            emailTv.setText(list.get(i).getEmail());
            fourth.addView(emailTv);

            TextView goodsTv = new TextView(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams goodsParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            goodsTv.setLayoutParams(goodsParam);
            goodsTv.setTextColor(Color.BLACK);
            goodsTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            goodsTv.setText(list.get(i).getGoods());
            fourth.addView(goodsTv);

            LinearLayout fifth = new LinearLayout(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams fifthParam = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            fifthParam.weight = (float)0.45;
            fourth.setGravity( Gravity.CENTER );
            fifth.setLayoutParams(fifthParam);
            fifth.setOrientation(LinearLayout.VERTICAL);
            fifth.setGravity(Gravity.CENTER);
            third.addView(fifth);

            TextView payTv = new TextView(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams payParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            payTv.setLayoutParams(payParam);
            payTv.setTextColor(Color.BLACK);
            payTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            payTv.setText("결제금액");
            fifth.addView(payTv);

            TextView priceTv = new TextView(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams priceParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            priceTv.setLayoutParams(priceParam);
            priceTv.setTextColor(Color.RED);
            priceTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            priceTv.setText(decimalFormat.format(list.get(i).getTotalDisPrice()));
            fifth.addView(priceTv);

            ImageView arrow = new ImageView(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams arrowParam = new LinearLayout.LayoutParams(0, size30);
            arrowParam.weight = (float)0.1;
            arrow.setLayoutParams(arrowParam);
            arrow.setBackground(getDrawable(R.drawable.arrow_r));
            third.addView(arrow);

            TextView dateTv = new TextView(BaobabCpPaymentList.this);
            LinearLayout.LayoutParams dateParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dateParam.setMargins(0, size5, 0, size5);
            dateTv.setLayoutParams(dateParam);
            dateTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            dateTv.setText("결제일 : " + format.format(list.get(i).getPayDate()));
            first.addView(dateTv);

            if(i != list.size()-1){
                View line = new View(BaobabCpPaymentList.this);
                LinearLayout.LayoutParams lineParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size2);
                lineParam.setMargins(0, size5, 0, size5);
                line.setLayoutParams(lineParam);
                line.setBackgroundColor(Color.DKGRAY);
                first.addView(line);
            }
        }
    }
}