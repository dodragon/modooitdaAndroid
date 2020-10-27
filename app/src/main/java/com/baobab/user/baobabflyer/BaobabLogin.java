package com.baobab.user.baobabflyer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.Sha256Util;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabLogin extends AppCompatActivity {

    RetroSingleTon retroSingleTon;
    String loginDiv ="baobab";

    private FirebaseAuth auth = null;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_login);

        SharedPreferences spf = getSharedPreferences("cert", 0);
        SharedPreferences.Editor editor = spf.edit();
        editor.clear();
        editor.commit();

        final EditText idEt = findViewById(R.id.inputEmail);
        final EditText pwEt = findViewById(R.id.inputPw);

        findViewById(R.id.btn_baobabJoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(BaobabLogin.this, BaobabAcceptTerms2.class);
                startActivity(signIn);
            }
        });

        findViewById(R.id.findEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabLogin.this, BaobabOptionCert.class);
                intent.putExtra("kind", "findEmail");
                startActivity(intent);
            }
        });

        findViewById(R.id.findPassWord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabLogin.this, BaobabOptionCert.class);
                intent.putExtra("kind", "findPassword");
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_baobabLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoadDialog loading = new LoadDialog(BaobabLogin.this);
                loading.showDialog();
                Sha256Util sha = new Sha256Util();

                final String ID = idEt.getText().toString();
                final String PW = sha.sha256(pwEt.getText().toString());

                Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).almightyLogin(ID, pwEt.getText().toString());
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                int result = response.body();

                                if(result == 1) {
                                    loading.dialogCancel();
                                    Intent intent = new Intent(BaobabLogin.this, AlmightyCpSearch.class);
                                    startActivity(intent);
                                } else {
                                    if (ID.getBytes().length <= 0) {
                                        Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_LONG).show();
                                        loading.dialogCancel();
                                    } else if (PW.getBytes().length <= 0) {
                                        Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_LONG).show();
                                        loading.dialogCancel();
                                    } else if ((ID.getBytes().length <= 0) && (PW.getBytes().length <= 0)) {
                                        Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_LONG).show();
                                        loading.dialogCancel();
                                    } else if (!((ID.getBytes().length <= 0) && (PW.getBytes().length <= 0))) {
                                        loginConfirm(ID, PW, loading);
                                    }
                                }
                            } else {
                                Log.d("관리자 로그인", "response 내용없음");
                                Toast.makeText(getApplicationContext(), "죄송합니다. 같은상황이 지속되면 고객센터로 문의해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("관리자 로그인", "서버에러");
                            Toast.makeText(getApplicationContext(), "죄송합니다. 같은상황이 지속되면 고객센터로 문의해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.d("관리자 로그인", t.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(), "죄송합니다. 같은상황이 지속되면 고객센터로 문의해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        findViewById(R.id.goHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabLogin.this, HomeWebView.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void loginConfirm(final String id, final String pw, final LoadDialog loading){
        Call<UserAllVO> login = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).loginConfirm(id, pw);
        login.enqueue(new Callback<UserAllVO>() {
            @Override
            public void onResponse(Call<UserAllVO> call, Response<UserAllVO> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        final UserAllVO vo = response.body();
                        Log.d("통신 완료", "완료");

                        Call<ResponseBody> logHistory = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).loginHistory(vo.getEmail(), pw, vo.getDiv_code(), "in");
                        logHistory.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d("통신완료", "로그인 히스토리");

                                SharedPreferences spf = getSharedPreferences("user", 0);
                                final SharedPreferences.Editor editor = spf.edit();
                                editor.putString("email", id);
                                editor.putString("pw", pw);
                                editor.putString("divCode", vo.getDiv_code());
                                editor.putString("phone", vo.getPhon_num());
                                editor.putString("pushAgree", vo.getPush_agree());
                                editor.putString("loginDiv", loginDiv);
                                editor.putInt("point", vo.getPoint());
                                editor.putString("nickName", vo.getNickName());
                                editor.putString("profile", vo.getProfileImg());
                                //로그인 관련 세션 추가시 여기서 추가
                                editor.commit();

                                if (vo.getDiv_code().equals("c-01-01") || vo.getDiv_code().equals("c-02-02")) {
                                    loading.dialogCancel();
                                    Intent intent = new Intent(BaobabLogin.this, BaobabAnterMain.class);
                                    intent.putExtra("userLogin", "userLogin");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "로그인이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    finish();
                                } else if (vo.getDiv_code().equals("u-01-01") || vo.getDiv_code().equals("c-02-03")) {
                                    //i권한 = 이니시스 테스트계정
                                    loading.dialogCancel();
                                    Intent intent = new Intent(BaobabLogin.this, BaobabAnterMain.class);
                                    intent.putExtra("userLogin", "userLogin");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "로그인이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    finish();
                                } else if (vo.getDiv_code().equals("u-01-02")) {
                                    if(vo.getUser_password().equals("")){
                                        loading.dialogCancel();
                                        AlertDialog alert = new AlertDialog.Builder(BaobabLogin.this)
                                                .setTitle("탈퇴회원")
                                                .setMessage("고객님은 탈퇴회원 이십니다. 계정을 복구 하시겠습니까?\n복구 이후 바로 로그인 됩니다.")
                                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        editor.clear();
                                                        editor.commit();
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Call<Integer> accountCall = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).accountBack(id);
                                                        accountCall.enqueue(new Callback<Integer>() {
                                                            @Override
                                                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                                                if(response.isSuccessful()){
                                                                    if(response.body() != null){
                                                                        int result = response.body();

                                                                        if(result > 0){
                                                                            editor.putString("divCode", "u-01-01");
                                                                            editor.commit();
                                                                            Intent intent = new Intent(BaobabLogin.this, BaobabAnterMain.class);
                                                                            intent.putExtra("userLogin", "userLogin");
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            startActivity(intent);
                                                                            Toast.makeText(getApplicationContext(), "로그인이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                                                            finish();
                                                                        }else {
                                                                            Log.d("계정 복구", "결과값 이상");
                                                                            Toast.makeText(BaobabLogin.this, "복구가 되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }else {
                                                                        Log.d("계정 복구", "response 내용없음");
                                                                        Toast.makeText(BaobabLogin.this, "복구가 되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }else {
                                                                    Log.d("계정 복구", "서버로그 확인 필요");
                                                                    Toast.makeText(BaobabLogin.this, "복구가 되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Integer> call, Throwable t) {
                                                                Log.d("계정 복구", t.getLocalizedMessage());
                                                                Toast.makeText(BaobabLogin.this, "복구가 되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                })
                                                .setIcon(R.drawable.logo_and)
                                                .create();
                                        alert.show();
                                    }else {
                                        editor.clear();
                                        editor.commit();
                                        Toast.makeText(getApplicationContext(), "이용할 수 없는 계정입니다. 회원가입을 다시 해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                                        loading.dialogCancel();
                                    }
                                } else if (vo.getDiv_code().startsWith("a")) {
                                    Intent intent = new Intent(BaobabLogin.this, BaobabAnterMain.class);
                                    intent.putExtra("userLogin", "userLogin");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "(관리자)로그인이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    loading.dialogCancel();
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                intent.putExtra("status", "오류");
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        if(loginDiv.equals("baobab")){
                            Log.d("통신 실패", "실패1 : response내용없음");
                            Toast.makeText(getApplicationContext(), "이메일과 비밀번호를 다시 확인해 주세요.", Toast.LENGTH_LONG).show();
                            loading.dialogCancel();
                        }else if(loginDiv.equals("kakao") || loginDiv.equals("google")){
                            UserAllVO vo = new UserAllVO();
                            vo.setEmail(id);
                            vo.setUser_password("");
                            vo.setPush_agree("o");
                            Intent intent = new Intent(BaobabLogin.this, MeCertWebView.class);
                            intent.putExtra("kind", "join");
                            intent.putExtra("userJoinVo", vo);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Log.d("통신 실패", "실패2 : 서버 에러");
                    Toast.makeText(getApplicationContext(), "서버에러입니다. 같은상황이 지속되면 고객센터로 문의해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                    loading.dialogCancel();
                }
            }

            @Override
            public void onFailure(Call<UserAllVO> call, Throwable t) {
                Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "죄송합니다. 같은상황이 지속되면 고객센터로 문의해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                loading.dialogCancel();
            }
        });
    }

    @Override
    public void onBackPressed(){
        boolean isLink = getIntent().getBooleanExtra("isLink", false);
        if(isLink){
            Intent intent = new Intent(this, BaobabAnterMain.class);
            startActivity(intent);
            finish();
        }else {
            finish();
        }
    }

    private void googleSignIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            LoadDialog loading = new LoadDialog(BaobabLogin.this);
                            loading.showDialog();
                            loginConfirm(user.getEmail(), "", loading);
                        }else {
                            Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}