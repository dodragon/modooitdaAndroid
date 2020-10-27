package com.baobab.user.baobabflyer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.Sha256Util;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.util.helper.log.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ly.count.android.sdk.Countly;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabOption extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    RetroSingleTon retroSingleTon;

    String email;

    AlertDialog alert;
    AlertDialog alert1;
    AlertDialog alert2;

    private GoogleApiClient gac;
    private FirebaseAuth auth;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_option);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestIdToken( getString( R.string.default_web_client_id ) )
                .requestEmail()
                .build();

        gac = new GoogleApiClient.Builder( this )
                .enableAutoManage( this, this )
                .addApi( Auth.GOOGLE_SIGN_IN_API, gso )
                .build();

        auth = FirebaseAuth.getInstance();

        SharedPreferences spf = getSharedPreferences("user", 0);
        final Sha256Util sha256Util = new Sha256Util();
        final EditText password = findViewById(R.id.password);
        final EditText wordCheck = findViewById(R.id.wordCheck);
        final CheckBox pushCheck = findViewById(R.id.pushAgree);
        LinearLayout checkLayout = findViewById(R.id.checkLayout);

        pushCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setBackground(getDrawable(R.drawable.ic_check_on));
                }else {
                    buttonView.setBackground(getDrawable(R.drawable.ic_check_off));
                }
            }
        });

        checkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushCheck.performClick();
            }
        });

        if(spf.getString("pushAgree", "").equals("o")){
            pushCheck.setChecked(true);
        }

        String divCode = spf.getString("divCode", "");
        if(divCode.startsWith("c") | divCode.startsWith("C")){
            findViewById(R.id.cpOption).setVisibility(View.VISIBLE);
        }

        email = spf.getString("email", "");
        ((TextView)findViewById(R.id.myEmail)).setText(email);

        alert = new AlertDialog.Builder(BaobabOption.this)
                .setIcon(R.drawable.logo_and)
                .setTitle("탈퇴..!?")
                .setMessage("저..정말...탈퇴할꺼에요오...??")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert1.show();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BaobabOption.this, "휴...다행이다 헿..", Toast.LENGTH_SHORT).show();
                    }
                }).create();

        alert1 = new AlertDialog.Builder(BaobabOption.this)
                .setIcon(R.drawable.logo_and)
                .setTitle("탈퇴..!?")
                .setMessage("진짜 할려구요오오오ㅠㅠㅠ...??")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert2.show();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BaobabOption.this, "휴...다행이다 헿..", Toast.LENGTH_SHORT).show();
                    }
                }).create();

        alert2 = new AlertDialog.Builder(BaobabOption.this)
                .setIcon(R.drawable.logo_and)
                .setTitle("탈퇴..!?")
                .setMessage("히이이이이이잉..이번이 마지막이에요ㅠㅠ \n정말 탈퇴할꾸야?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    final SharedPreferences spf = getSharedPreferences( "user", 0 );
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveUser(email);
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BaobabOption.this, "휴...다행이다 헿..", Toast.LENGTH_SHORT).show();
                    }
                }).create();

        View.OnClickListener saveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(legalCheck(password.getText().toString(), wordCheck.getText().toString())){
                    Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).userInfoCha(email, sha256Util.sha256(password.getText().toString()),
                            pushChecking(pushCheck));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Toast.makeText(BaobabOption.this, "설정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(BaobabOption.this, "오류 : 오류가 계속되면 고객센터로 문의바랍니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    });
                }else {
                    Toast.makeText(BaobabOption.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        findViewById(R.id.saveBtn).setOnClickListener(saveListener);
        findViewById(R.id.leave).setOnClickListener(leaveListener);

        findViewById(R.id.cpOption).setOnClickListener(cpOption);

        findViewById(R.id.logout).setOnClickListener(logout);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    View.OnClickListener logout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Countly.sharedInstance().startEvent("Logout");
            final SharedPreferences spf = getSharedPreferences( "user", 0 );
            Call<ResponseBody> logHistroy = retroSingleTon.getInstance().getRetroInterface( getApplicationContext() ).loginHistory( spf.getString( "email", "" ), spf.getString( "pw", "" ),
                    spf.getString( "divCode", "" ), "out" );
            logHistroy.enqueue( new Callback<ResponseBody>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d( "통신완료", "로그인 히스토리" );

                    SharedPreferences.Editor editor = spf.edit();
                    editor.clear();
                    editor.commit();

                    Log.d( "로그아웃", "클릭" );

                    Intent intent = new Intent( getApplicationContext(), BaobabAnterMain.class );
                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity( intent );
                    finish();

                    Countly.sharedInstance().endEvent("Logout");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d( "통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage() );
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            } );
        }
    };

    View.OnClickListener cpOption = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BaobabOption.this, BaobabCpOption.class);
            startActivity(intent);
        }
    };

    View.OnClickListener leaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences spf = getSharedPreferences("user", 0);
            if(spf.getString("divCode", "").startsWith("u")){
                alert.show();
            }else {
                Toast.makeText(BaobabOption.this, "일반 사용자 이외에 사용자는 고객센터로 문의 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public boolean legalCheck(String password, String wordCheck){
        //아래 적법성 검사 작성 바람
        Pattern p1 = Pattern.compile( "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*?,./\\\\\\\\<>|_-[+]=\\\\`~\\\\(\\\\)\\\\[\\\\]\\\\{\\\\}])[A-Za-z[0-9]!@#$%^&*?,./\\\\\\\\<>|_-[+]=\\\\`~\\\\(\\\\)\\\\[\\\\]\\\\{\\\\}]{8,20}$", Pattern.CASE_INSENSITIVE );
        Matcher m1 = p1.matcher( password );
        if(password.length() == 0){
            Toast.makeText( BaobabOption.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT ).show();
            return false;
        }else if (password.length() < 8){
            Toast.makeText( BaobabOption.this, "비밀번호는 8자 ~ 20자까지 입력할 수 있습니다.", Toast.LENGTH_SHORT ).show();
            return false;
        }else if (!m1.matches(  )){
            Toast.makeText( BaobabOption.this, "비밀번호는 영문, 숫자, 특수문자(하나 이상)로된 형식만 허용합니다.", Toast.LENGTH_SHORT ).show();
            return false;
        } else if (wordCheck.length() == 0){
            Toast.makeText( BaobabOption.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT ).show();
            return false;
        }else if (!password.equals( wordCheck )){
            Toast.makeText( BaobabOption.this, "비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    public String pushChecking(CheckBox checkBox){
        if(checkBox.isChecked()){
            return "o";
        }else {
            return "x";
        }
    }

    public void leaveUser(String email){
        Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).leaveUser(email);
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("탈퇴 완료 : ", "완료");
                Toast.makeText(BaobabOption.this, "힝........다음에 꼭 다시 보아요오ㅠㅠㅠㅠ,,", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BaobabOption.this, BaobabAnterMain.class);
                SharedPreferences spf = getSharedPreferences("user", 0);
                SharedPreferences.Editor editor = spf.edit();
                editor.clear();
                editor.commit();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("탈퇴 오류 : ", t.getLocalizedMessage());
                Toast.makeText(BaobabOption.this, "탈퇴 중 오류발생 : 그냥 안하면 안되용..?", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void googleSignOut(){
        FirebaseAuth.getInstance().signOut();
        SharedPreferences spf = getSharedPreferences( "user", 0 );
        SharedPreferences.Editor editor = spf.edit();
        editor.clear();
        editor.commit();

        Log.d( "로그아웃", "클릭" );

        Intent intent = new Intent( getApplicationContext(), BaobabAnterMain.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( intent );
        finish();

        Countly.sharedInstance().endEvent("Logout");
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
