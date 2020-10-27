package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.util.MakeCertNumber;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaobabPushPay extends AppCompatActivity {

    int cpSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_push_pay);

        cpSeq = getIntent().getIntExtra("cpSeq", 0);
        ((EditText)findViewById(R.id.pushEa)).addTextChangedListener(payCalcu);
        findViewById(R.id.goPay).setOnClickListener(goPay);
    }

    TextWatcher payCalcu = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try{
                DecimalFormat formatter = new DecimalFormat("###,###");
                ((TextView)findViewById(R.id.resultPay)).setText(formatter.format(Integer.parseInt(s.toString()) * 50));
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    View.OnClickListener goPay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText editText = findViewById(R.id.pushEa);
            if(editText.getText().toString().length() > 0){
                int pushEa = Integer.parseInt(editText.getText().toString());
                if(pushEa < 100 || (pushEa % 100) != 0){
                    Toast.makeText(getApplicationContext(), "100개 이상, 100단위로 구매가 가능합니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(BaobabPushPay.this, Payment.class);
                    intent.putExtra("vo", makeVo());
                    startActivity(intent);
                    finish();
                }
            }else {
                Toast.makeText(getApplicationContext(), "구매할 개수를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private PaymentVO makeVo(){
        PaymentVO vo = new PaymentVO();

        vo.setGoods("푸시이용권" + ((EditText)findViewById(R.id.pushEa)).getText().toString() + "개");
        vo.setOrderNum(makeOID());
        vo.setTotalDisPrice(Integer.parseInt(((TextView)findViewById(R.id.resultPay)).getText().toString().replaceAll(",", "")));
        vo.setCpName(getIntent().getStringExtra("cpName"));
        vo.setEmail(getSharedPreferences("user", 0).getString("email", ""));
        vo.setUserPhone(getSharedPreferences("user", 0).getString("phone", ""));
        vo.setCpSeq(cpSeq);

        return vo;
    }

    private String makeOID(){
        MakeCertNumber certNumber = new MakeCertNumber();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return "M" + cpSeq + certNumber.numberGen(10, 1) + format.format(new Date());
    }
}
