package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.vo.CertificationVO;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;

import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCertification extends AppCompatActivity {

    RetroSingleTon retroSingleTon;
    EditText userPhone;
    Button certBtn;
    EditText authNum;
    Button commitBtn;
    String pushCheck;
    String userEmail;
    String kakaoEmail;
    String googleEmail;

    String loginDiv;

    CertificationVO certVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_certification );

        userPhone = findViewById( R.id.userPhone );
        authNum = findViewById( R.id.authNum );
        certBtn = findViewById( R.id.certification );
        commitBtn = findViewById( R.id.commitBtn );

        final Intent idIntent = getIntent();

        googleEmail = idIntent.getStringExtra( "googleEmail" );
        kakaoEmail = idIntent.getStringExtra( "kakaoEmail" );
        pushCheck = idIntent.getStringExtra( "check" );

        if (googleEmail != null) {
            userEmail = googleEmail;
            loginDiv = "google";
        } else if (kakaoEmail != null) {
            userEmail = kakaoEmail;
            loginDiv = "kakao";
        }

        certBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userPhone.getText().toString().length() == 11 && (userPhone.getText().toString().startsWith( "010" ) || userPhone.getText().toString().startsWith( "019" ) || userPhone.getText().toString().startsWith( "016" )
                        || userPhone.getText().toString().startsWith( "017" ) || userPhone.getText().toString().startsWith( "018" ) || userPhone.getText().toString().startsWith( "011" )) &&
                        isStringDouble( userPhone.getText().toString() )) {
                    String cpId = "WDHS1001";
                    String urlCode = "001001";
                    String phoneNum = userPhone.getText().toString();
                    Random ran = new Random();
                    String autoNum = String.valueOf( ran.nextInt( 999999 ) );

                    Call<CertificationVO> call = retroSingleTon.getInstance().getRetroInterface( getApplicationContext() ).cert( cpId, urlCode, phoneNum, autoNum );
                    call.enqueue( new Callback<CertificationVO>() {
                        @Override
                        public void onResponse(Call<CertificationVO> call, Response<CertificationVO> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    Log.d( "통신 완료 : ", "완료" );
                                    certVo = response.body();
                                    Toast.makeText( BaobabCertification.this, "인증번호가 올때까지 기다려주세요.", Toast.LENGTH_SHORT ).show();
                                } else {
                                    Log.d( "서버 에러 : ", "response 내용없음" );
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Log.d( "서버 에러 : ", "서버 로그를 확인바람" );
                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                intent.putExtra("status", "오류");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<CertificationVO> call, Throwable t) {
                            Log.d( "통신 실패 : ", t.getLocalizedMessage() );
                            Toast.makeText( BaobabCertification.this, "오류입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT ).show();
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    } );
                } else {
                    Toast.makeText( BaobabCertification.this, "휴대폰번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        commitBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled( false );
                final LoadDialog loading = new LoadDialog( BaobabCertification.this );
                loading.showDialog();

                if (userPhone.getText().toString().length() < 11 && (!userPhone.getText().toString().startsWith( "010" )
                        || !userPhone.getText().toString().startsWith( "019" ) || !userPhone.getText().toString().startsWith( "016" )
                        || !userPhone.getText().toString().startsWith( "017" ) || !userPhone.getText().toString().startsWith( "018" )
                        || !userPhone.getText().toString().startsWith( "011" )) && !isStringDouble( userPhone.getText().toString() )) {
                    Toast.makeText( BaobabCertification.this, "휴대폰번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT ).show();
                    userPhone.requestFocus();
                    v.setEnabled( true );
                    loading.dialogCancel();
                    return;
                } else if (authNum.getText().toString().length() == 0) {
                    Toast.makeText( BaobabCertification.this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT ).show();
                    authNum.requestFocus();
                    v.setEnabled( true );
                    loading.dialogCancel();
                } else if (!authNum.getText().toString().equals( certVo.getAuthNo() )) {
                    Toast.makeText( BaobabCertification.this, "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT ).show();
                    authNum.requestFocus();
                    v.setEnabled( true );
                    loading.dialogCancel();
                } else if (!certVo.getResult().equals( "Y" )) {
                    Toast.makeText( BaobabCertification.this, "인증에 실패하였습니다. 인증을 다시 시도해 주세요.", Toast.LENGTH_SHORT ).show();
                    authNum.requestFocus();
                    v.setEnabled( true );
                    loading.dialogCancel();
                } else {
                    Call<Integer> call = retroSingleTon.getInstance().getRetroInterface( getApplicationContext() ).userJoin( userEmail, "", String.valueOf( userPhone.getText() ), pushCheck  );
                    call.enqueue( new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    Call<UserAllVO> login = retroSingleTon.getInstance().getRetroInterface( getApplicationContext() ).loginConfirm( userEmail, "" );
                                    login.enqueue( new Callback<UserAllVO>() {
                                        @Override
                                        public void onResponse(Call<UserAllVO> call, Response<UserAllVO> response) {
                                            if (response.isSuccessful()) {
                                                if (response.body() != null) {
                                                    UserAllVO vo = response.body();
                                                    Log.d( "통신 완료", "완료" );

                                                    SharedPreferences spf = getSharedPreferences( "user", 0 );
                                                    SharedPreferences.Editor editor = spf.edit();
                                                    editor.putString( "email", vo.getEmail() );
                                                    editor.putString( "pw", vo.getUser_password() );
                                                    editor.putString( "divCode", vo.getDiv_code() );
                                                    editor.putString( "phone", vo.getPhon_num() );
                                                    editor.putString( "pushAgree", vo.getPush_agree() );
                                                    editor.putString( "loginDiv", loginDiv );
                                                    editor.putInt("point", 0);
                                                    editor.putString("nickName", vo.getEmail());
                                                    editor.putString("profile", "이미지없음");
                                                    editor.commit();

                                                    Call<ResponseBody> logHistory = retroSingleTon.getInstance().getRetroInterface( getApplicationContext() ).loginHistory( vo.getEmail(), vo.getUser_password(), vo.getDiv_code(), "in" );
                                                    logHistory.enqueue( new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                            Log.d( "통신완료", "로그인 히스토리" );
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
                                                    Log.d( "로그인 세션", "완료" );
                                                    Intent userLogin = new Intent( BaobabCertification.this, BaobabAnterMain.class );
                                                    userLogin.putExtra( "userLogin", "userLogin" );
                                                    startActivity( userLogin );
                                                    Toast.makeText( getApplicationContext(), "로그인이 완료되었습니다.", Toast.LENGTH_LONG ).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText( getApplicationContext(), "인증과 동의를 진행해주세요.", Toast.LENGTH_LONG ).show();
                                                }
                                            } else {
                                                Log.d( "통신 실패", "실패2 : 서버 에러" );
                                                Toast.makeText( getApplicationContext(), "서버에러입니다. 같은 상황이 지속되면 고객센터로 문의해주시기 바랍니다.", Toast.LENGTH_LONG ).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UserAllVO> call, Throwable t) {
                                            Log.d( "통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage() );
                                            Toast.makeText( getApplicationContext(), "죄송합니다. 같은 상황이 지속되면 고객센터로 문의해주시기 바랍니다.", Toast.LENGTH_LONG ).show();
                                        }
                                    } );
                                    loading.dialogCancel();
                                    Toast.makeText( getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT ).show();

                                } else {
                                    Log.d( "통신 실패", "실패1 : response 내용없음" );
                                    Toast.makeText( getApplicationContext(), "존재하지 않는 계정입니다.", Toast.LENGTH_SHORT ).show();
                                    loading.dialogCancel();
                                }
                            } else {
                                Log.d( "통신 실패", "실패 : 서버 에러" );
                                Toast.makeText( getApplicationContext(), "서버에러입니다. 같은 상황이 지속되면 고객센터로 문의해주시기 바랍니다.", Toast.LENGTH_SHORT ).show();
                                loading.dialogCancel();
                            }

                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            loading.dialogCancel();
                            Log.d( "통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage() );
                            Toast.makeText( getApplicationContext(), "죄송합니다. 같은 상황이 지속되면 고객센터로 문의해주시기 바랍니다.", Toast.LENGTH_SHORT ).show();
                            v.setEnabled( true );
                        }
                    } );
                }
            }
        } );

    }

    public boolean isStringDouble(String s) {
        try {
            Double.parseDouble( s );
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
