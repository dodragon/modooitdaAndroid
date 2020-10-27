package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabFindPassword extends AppCompatActivity {

    RetroSingleTon retroSingleTon;
    String phoneNumber = "";
    String email = "";

    EditText setPw;
    EditText setPwCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_find_password);

        phoneNumber = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");

        setPw = findViewById(R.id.setPw);
        setPwCheck = findViewById(R.id.setPwCheck);

        Call<UserAllVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).findPassword(email, phoneNumber);
        call.enqueue(new Callback<UserAllVO>() {
            @Override
            public void onResponse(Call<UserAllVO> call, Response<UserAllVO> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        UserAllVO vo = response.body();
                        if(vo.getEmail() == null || vo.getEmail().equals("") || vo.getEmail().length() == 0){
                            Toast.makeText(getApplicationContext(), "존재하지 않는 이메일 입니다.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else {
                            findViewById(R.id.checkFinish).setOnClickListener(passwordUpdate);
                            findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            });
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "존재하지 않는 이메일 입니다.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }else {
                    Log.d("비밀번호찾기", "서버로그 확인 필요");
                    Toast.makeText(getApplicationContext(), "지금은 비밀번호를 찾을 수 없습니다. 죄송합니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<UserAllVO> call, Throwable t) {
                Log.d("비밀번호찾기", t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "지금은 비밀번호를 찾을 수 없습니다. 죄송합니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    View.OnClickListener passwordUpdate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Pattern p2 = Pattern.compile( "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*?,./\\\\\\\\<>|_-[+]=\\\\`~\\\\(\\\\)\\\\[\\\\]\\\\{\\\\}])[A-Za-z[0-9]!@#$%^&*?,./\\\\\\\\<>|_-[+]=\\\\`~\\\\(\\\\)\\\\[\\\\]\\\\{\\\\}]{8,20}$", Pattern.CASE_INSENSITIVE );
            Matcher m2 = p2.matcher( (setPw).getText().toString() );

            if(setPw.getText().toString().length() == 0) {
                Toast.makeText( BaobabFindPassword.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT ).show();
                setPw.requestFocus();
                return;
            }else if(setPw.getText().toString().length() < 8) {
                Toast.makeText( BaobabFindPassword.this, "비밀번호는 8자 ~ 20자까지 입력할 수 있습니다.", Toast.LENGTH_SHORT ).show();
                setPw.requestFocus();
                return;
            }else if(!m2.matches()) {
                Toast.makeText( BaobabFindPassword.this, "비밀번호는 영문,숫자,특수문자(하나 이상)로된 형식만 허용합니다.", Toast.LENGTH_SHORT ).show();
                setPw.requestFocus();
                return;
            }else if(setPwCheck.getText().toString().length() == 0) {
                Toast.makeText( BaobabFindPassword.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT ).show();
                setPwCheck.requestFocus();
                return;
            }else if(!setPw.getText().toString().equals(setPwCheck.getText().toString())){
                Toast.makeText( BaobabFindPassword.this, "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT ).show();
                setPwCheck.requestFocus();
                return;
            }else{
                Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).updatePassword(email, phoneNumber, setPw.getText().toString());
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                Log.d("통신완료", "완료");
                                int result = response.body();
                                if(result > 0){
                                    Toast.makeText( BaobabFindPassword.this, "비밀번호 변경이 완료되었습니다. 로그인 해주시기 바랍니다.", Toast.LENGTH_LONG ).show();
                                    finish();
                                }else {
                                    Toast.makeText( BaobabFindPassword.this, "비밀번호 변경에 실패 했습니다. 다시 시도해 주시기 바랍니다.", Toast.LENGTH_LONG ).show();
                                    finish();
                                }
                            }else{
                                Log.d("서버에러", "response 내용없음");
                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                Toast.makeText( BaobabFindPassword.this, "죄송합니다. 다시 시도해 주시기 바랍니다.", Toast.LENGTH_LONG ).show();
                                intent.putExtra("status", "오류");
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            Log.d("서버에러", "서버로그 확인 필요");
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            Toast.makeText( BaobabFindPassword.this, "죄송합니다. 다시 시도해 주시기 바랍니다.", Toast.LENGTH_LONG ).show();
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.d("통신에러", t.getLocalizedMessage());
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        Toast.makeText( BaobabFindPassword.this, "죄송합니다. 다시 시도해 주시기 바랍니다.", Toast.LENGTH_LONG ).show();
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }
    };
}
