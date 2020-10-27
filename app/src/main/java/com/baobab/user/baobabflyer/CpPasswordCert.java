package com.baobab.user.baobabflyer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.AccountPagerAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.MakeCertNumber;
import com.baobab.user.baobabflyer.server.util.MenuSelectDialog;
import com.baobab.user.baobabflyer.server.util.Sha256Util;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.baobab.user.baobabflyer.server.vo.PaypleAccountVO;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CpPasswordCert extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    RetroSingleTon retroSingleTon;

    String payKind = "";

    private int acListSize = 0;
    List<PaypleAccountVO> accList;
    String payerId;
    String isSimple;

    int cpSeq;
    String cpName;
    int price;
    int disPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cp_password_cert);

        cpSeq = getIntent().getIntExtra("cpSeq", 0);
        cpName = getIntent().getStringExtra("cpName");
        price = getIntent().getIntExtra("price", 0);
        disPrice = getIntent().getIntExtra("disPrice", 0);

        TextView priceText = findViewById(R.id.priceText);
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        priceText.setText("여기를 클릭 하시면\n위 수단으로 " + decimalFormat.format(price) + "원 에서 할인받아\n" + decimalFormat.format(disPrice) + "원을 결제합니다. →");

        final String cpPw = getIntent().getStringExtra("pw");
        final EditText pw = findViewById(R.id.cpPw);
        pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(pw.getText().toString().length() == 4){
                    String inputPw = new Sha256Util().sha256(pw.getText().toString());
                    if(inputPw.equals(cpPw)){
                        findViewById(R.id.pwLayout).setVisibility(View.GONE);
                        findViewById(R.id.payLayout).setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(CpPasswordCert.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((RadioGroup)findViewById(R.id.payKindGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.payple){
                    payKind = "payple";
                    findViewById(R.id.pagerLayout).setVisibility(View.VISIBLE);
                }else {
                    payKind = "inicis";
                    findViewById(R.id.pagerLayout).setVisibility(View.GONE);
                }
            }
        });

        ((RadioButton)findViewById(R.id.payple)).setChecked(true);

        Call<List<PaypleAccountVO>> acCall = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).accountCheck(getSharedPreferences("user", 0).getString("email", ""));
        acCall.enqueue(new Callback<List<PaypleAccountVO>>() {
            @Override
            public void onResponse(Call<List<PaypleAccountVO>> call, Response<List<PaypleAccountVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        accList = response.body();
                        acListSize = accList.size();

                        if(acListSize == 0){
                            payerId = "";
                            isSimple = "N";
                        }else {
                            payerId = accList.get(0).getPayerId();
                            isSimple = "Y";
                        }

                        makeViewPager(accList);
                    }else {
                        Log.d("등록계좌 조회", "response 내용 없음");
                        makeViewPager(new ArrayList<PaypleAccountVO>());
                    }
                }else {
                    Log.d("등록계좌 조회", "서버 로그 확인 필요");
                    makeViewPager(new ArrayList<PaypleAccountVO>());
                }
            }

            @Override
            public void onFailure(Call<List<PaypleAccountVO>> call, Throwable t) {
                Log.d("등록계좌 조회", t.getLocalizedMessage());
                makeViewPager(new ArrayList<PaypleAccountVO>());
            }
        });

        findViewById(R.id.priceText).setOnClickListener(sell);
    }

    private void makeViewPager(List<PaypleAccountVO> list){
        ViewPager viewPager = findViewById(R.id.accountPager);
        viewPager.setClipToPadding(false);

        float density = getResources().getDisplayMetrics().density;
        int margin = (int)(60 * density);
        viewPager.setPadding(margin, 0, margin, 0);
        viewPager.setPageMargin(margin / 2);

        AccountPagerAdapter adapter = new AccountPagerAdapter(CpPasswordCert.this, list, null);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(CpPasswordCert.this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position < acListSize){
            payerId = accList.get(position).getPayerId();
            isSimple = "Y";
        }else {
            payerId = "";
            isSimple = "N";
        }

        Log.d("심플", isSimple);
        Log.d("아이디", payerId);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    View.OnClickListener sell = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PaymentVO vo = new PaymentVO();
            SharedPreferences spf = getSharedPreferences("user", 0);
            vo.setUserPhone(spf.getString("phone", ""));
            vo.setCpName(cpName);
            vo.setCpSeq(cpSeq);
            vo.setEmail(spf.getString("email", ""));
            vo.setOrderNum(makeOrderNum(vo.getUserPhone()));
            vo.setGoods(cpName + " 자유 이용권");
            vo.setTotalPrice(price);
            vo.setTotalDisPrice(disPrice);

            Intent intent;

            if(payKind.equals("payple")){
                intent = new Intent(CpPasswordCert.this, PaypleWebView.class);
                intent.putExtra("payerId", payerId);
                intent.putExtra("isSimple", isSimple);
                intent.putExtra("payWork", "PAY");
            }else {
                intent = new Intent(CpPasswordCert.this, Payment.class);
            }
            intent.putExtra("vo", vo);
            startActivity(intent);
        }
    };

    private String makeOrderNum(String userPhone){
        MakeCertNumber certNumber = new MakeCertNumber();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return "F" + cpSeq + certNumber.numberGen(10, 1) + format.format(new Date()) + userPhone.substring(5, 8);
    }
}
