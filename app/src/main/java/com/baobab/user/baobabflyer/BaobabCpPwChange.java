package com.baobab.user.baobabflyer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.Sha256Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpPwChange extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    EditText pwEt;
    EditText pwCheckEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_pw_change);

        pwEt = findViewById(R.id.inputPw);
        pwCheckEt = findViewById(R.id.inputPwCheck);

        pwEt.addTextChangedListener(watcher);
        pwCheckEt.addTextChangedListener(watcher);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final String beforePw = getIntent().getStringExtra("pw");
        final String email = getIntent().getStringExtra("email");

        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkingPw(beforePw, email);
            }
        });
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Button btn = findViewById(R.id.change);
            if(pwEt.getText().toString().length() == 4 && pwEt.getText().toString().length() == 4){
                btn.setEnabled(true);
                btn.setBackgroundColor(Color.rgb(255, 106, 41));
            }else{
                btn.setEnabled(false);
                btn.setBackgroundColor(Color.rgb(216, 220, 229));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void checkingPw(String beforePw, String email){
        String pw = pwEt.getText().toString();
        String checkPw = pwCheckEt.getText().toString();

        if(pw.equals(checkPw)){
            Sha256Util sha = new Sha256Util();
            String shaPw = sha.sha256(pw);
            if(shaPw.equals(beforePw)){
                Toast.makeText(this, "이전 비밀번호와 일치합니다.", Toast.LENGTH_SHORT).show();
            }else {
                Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).changeCpPw(shaPw, email);
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                int result = response.body();
                                if(result > 0){
                                    Toast.makeText(BaobabCpPwChange.this, "완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    ((Activity)BaobabCpOption.context).finish();
                                    Intent intent = new Intent(BaobabCpPwChange.this, BaobabCpOption.class);
                                    startActivity(intent);
                                    BaobabCpPwChange.this.finish();
                                }else {
                                    Log.d("업체비번변경", "result < 0");
                                    Toast.makeText(BaobabCpPwChange.this, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Log.d("업체비번변경", "response 내용없음");
                                Toast.makeText(BaobabCpPwChange.this, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.d("업체비번변경", "서버로그 확인필요");
                            Toast.makeText(BaobabCpPwChange.this, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.d("업체비번변경", t.getLocalizedMessage());
                        Toast.makeText(BaobabCpPwChange.this, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else {
            Toast.makeText(this, "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
