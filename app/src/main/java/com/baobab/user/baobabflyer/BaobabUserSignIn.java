package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.Sha256Util;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabUserSignIn extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_user_sign_in );

        EditText userPwCheck = findViewById(R.id.userPwCheck);
        userPwCheck.addTextChangedListener(watcher);
        EditText userPw = findViewById(R.id.userPw);
        userPw.addTextChangedListener(watcher);

        findViewById(R.id.commitBtn).setOnClickListener(commit);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EditText userEmail = findViewById(R.id.userEmail);
            EditText userPw = findViewById(R.id.userPw);
            EditText userPwCheck = findViewById(R.id.userPwCheck);
            Button commitBtn = findViewById(R.id.commitBtn);

            if(!userEmail.getText().toString().isEmpty() && userPwCheck.getText().toString().length() == userPw.getText().toString().length()){
                commitBtn.setBackgroundColor(Color.rgb(92, 124, 250));
            }else {
                commitBtn.setBackgroundColor(Color.rgb(216, 220, 229));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    View.OnClickListener commit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final EditText userEmail = findViewById(R.id.userEmail);
            final EditText userPw = findViewById(R.id.userPw);
            EditText userPwCheck = findViewById(R.id.userPwCheck);

            Pattern p = Pattern.compile( "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE );
            Matcher m = p.matcher(userEmail.getText().toString());
            Pattern p2 = Pattern.compile( "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*?,./\\\\\\\\<>|_-[+]=\\\\`~\\\\(\\\\)\\\\[\\\\]\\\\{\\\\}])[A-Za-z[0-9]!@#$%^&*?,./\\\\\\\\<>|_-[+]=\\\\`~\\\\(\\\\)\\\\[\\\\]\\\\{\\\\}]{8,20}$", Pattern.CASE_INSENSITIVE );
            Matcher m2 = p2.matcher(userPw.getText().toString());

            if (userEmail.getText().toString().length() == 0) {
                Toast.makeText( BaobabUserSignIn.this, "E-mail을 입력하세요.", Toast.LENGTH_SHORT ).show();
                userEmail.requestFocus();
                return;
            } else if (!m.matches()) {
                Toast.makeText( BaobabUserSignIn.this, "Email형식으로 입력하세요", Toast.LENGTH_SHORT ).show();
                userEmail.requestFocus();
                return;
            } else if (userPw.getText().toString().length() == 0) {
                Toast.makeText( BaobabUserSignIn.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT ).show();
                userPw.requestFocus();
                return;
            } else if (userPw.getText().toString().length() < 8) {
                Toast.makeText( BaobabUserSignIn.this, "비밀번호는 8자 ~ 20자까지 입력할 수 있습니다.", Toast.LENGTH_SHORT ).show();
                userPw.requestFocus();
                return;
            } else if (!m2.matches()) {
                Toast.makeText( BaobabUserSignIn.this, "비밀번호는 영문,숫자,특수문자(하나 이상)로된 형식만 허용합니다.", Toast.LENGTH_SHORT ).show();
                userPw.requestFocus();
                return;
            } else if (userPwCheck.getText().toString().length() == 0) {
                Toast.makeText( BaobabUserSignIn.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT ).show();
                userPwCheck.requestFocus();
                return;
            }else if(!userPw.getText().toString().equals(userPwCheck.getText().toString())){
                Toast.makeText( BaobabUserSignIn.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT ).show();
                userPwCheck.requestFocus();
                return;
            }/*else if(((EditText)findViewById(R.id.nickName)).getText().toString().length() == 0){
                Toast.makeText( BaobabUserSignIn.this, "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT ).show();
            }*/else {
                Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).duplicateCheck(userEmail.getText().toString());
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                int result = response.body();

                                UserAllVO vo = new UserAllVO();
                                Sha256Util sha = new Sha256Util();
                                Intent intent = new Intent(BaobabUserSignIn.this, MeCertWebView.class);
                                vo.setEmail(userEmail.getText().toString());
                                vo.setPush_agree(getIntent().getStringExtra("check"));
                                vo.setUser_password(sha.sha256(userPw.getText().toString()));
                                //vo.setNickName(((EditText)findViewById(R.id.nickName)).getText().toString());
                                intent.putExtra("userJoinVo", vo);
                                intent.putExtra("kind", "join");

                                resultCheck(result, intent, vo.getEmail());
                            }else {
                                Log.d("일반회원가입 중복", "response 내용없음");
                                Toast.makeText(BaobabUserSignIn.this, "죄송합니다. 재시도 바랍니다.", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }else {
                            Log.d("일반회원가입 중복", "서버로그 확인필요");
                            Toast.makeText(BaobabUserSignIn.this, "죄송합니다. 재시도 바랍니다.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.d("일반회원가입 중복", t.getLocalizedMessage());
                        Toast.makeText(BaobabUserSignIn.this, "죄송합니다. 재시도 바랍니다.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }
        }
    };

    public String pushCheck(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            return "o";
        } else {
            return "x";
        }
    }

    public void resultCheck(int result, Intent intent, final String email){
        if(result != 0){
            Toast.makeText(BaobabUserSignIn.this, "이미 가입된 이메일 입니다.", Toast.LENGTH_SHORT).show();
        }/*else if(result == 10){
            AlertDialog alert = new AlertDialog.Builder(this)
                    .setTitle("회원가입")
                    .setMessage("탈퇴했던 계정입니다.\n계정을 복구하시겠습니까?")
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).changeUserCode(email, "u-01-01");
                            call.enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if(response.isSuccessful()){
                                        if(response.body() != null){
                                            int changeResult = response.body();
                                            if(changeResult > 0){
                                                Toast.makeText(BaobabUserSignIn.this, "다시 로그인 후 계정을 이용해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }else {
                                                Log.d("탈퇴회원 복구", "결과값이 0");
                                                Toast.makeText(BaobabUserSignIn.this, "죄송합니다. 재시도 바랍니다.", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }
                                        }else {
                                            Log.d("탈퇴회원 복구", "response 내용없음");
                                            Toast.makeText(BaobabUserSignIn.this, "죄송합니다. 재시도 바랍니다.", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    }else {
                                        Log.d("탈퇴회원 복구", "서버 로그 확인 필요");
                                        Toast.makeText(BaobabUserSignIn.this, "죄송합니다. 재시도 바랍니다.", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {
                                    Log.d("탈퇴회원 복구", t.getLocalizedMessage());
                                    Toast.makeText(BaobabUserSignIn.this, "죄송합니다. 재시도 바랍니다.", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });
                        }
                    })
                    .create();
            alert.show();
        }*/else {
            startActivity(intent);
            finish();
        }
    }
}