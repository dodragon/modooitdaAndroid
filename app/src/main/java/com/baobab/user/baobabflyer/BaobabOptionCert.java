package com.baobab.user.baobabflyer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.util.MakeCertNumber;
import com.baobab.user.baobabflyer.server.util.SendFCMtoBaobabServer;
import com.baobab.user.baobabflyer.server.vo.CertificationVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.PaycancelVO;

import ly.count.android.sdk.Countly;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabOptionCert extends AppCompatActivity {
    RetroSingleTon retroSingleTon;

    CertificationVO certVo;

    final String cpId = "WDHS1001";
    final String urlCode = "001001";

    String realPhoneNum;

    private static String kind;

    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_option_cert);

        kind = getIntent().getStringExtra("kind");

        if(kind.equals("findPassword")){
            findViewById(R.id.email).setVisibility(View.VISIBLE);
        }else if(kind.equals("payCancel")){
            findViewById(R.id.cancelTv).setVisibility(View.VISIBLE);
        }

        timer = new CountDownTimer(180000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                int s = (int)millisUntilFinished / 1000;
                int m = s / 60;
                int rs = s % 60;
                String rsStr = String.valueOf(rs);
                if(rs < 10){
                    rsStr = "0" + rsStr;
                }
                TextView timerText = findViewById(R.id.timer);
                timerText.setText(m + ":" + rsStr);
            }

            @Override
            public void onFinish() {
                TextView timerText = findViewById(R.id.timer);
                timerText.setText("0:00");

                findViewById(R.id.smsCert).setVisibility(View.GONE);
                if (!BaobabOptionCert.this.isFinishing()) {
                    AlertDialog alert = new AlertDialog.Builder(BaobabOptionCert.this)
                            .setIcon(R.drawable.logo_and)
                            .setTitle("인증번호")
                            .setMessage("제한시간을 초과하였습니다.\n확인을 누르시면 인증번호를 다시 보냅니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    findViewById(R.id.reSms).performClick();
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    alert.show();
                }
            }
        };

        findViewById(R.id.smsCert).setOnClickListener(sms);

        EditText phone = findViewById(R.id.inputAuth);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button sms = findViewById(R.id.smsCert);
                if(s.toString().length() == 11){
                    sms.setBackgroundColor(Color.parseColor("#5c7cfa"));
                    sms.setEnabled(true);
                }else {
                    sms.setBackgroundColor(Color.rgb(216, 220, 229));
                    sms.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final EditText inputAuth = findViewById(R.id.authNum);
        inputAuth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button certBtn = findViewById(R.id.smsCert);
                if(s.toString().length() != 6){
                    certBtn.setEnabled(false);
                    certBtn.setBackgroundColor(Color.rgb(216, 220, 229));
                }else {
                    certBtn.setEnabled(true);
                    certBtn.setBackgroundColor(Color.parseColor("#5c7cfa"));
                }

                LinearLayout lines = (LinearLayout) ((LinearLayout)findViewById(R.id.authLayout)).getChildAt(1);
                View[] arr = new View[6];

                for(int i=0;i<arr.length;i++){
                    arr[i] = lines.getChildAt(i);
                }

                findViewById(R.id.authFail).setVisibility(View.GONE);

                for(int i=0;i<arr.length;i++){
                    arr[i].setBackgroundColor(Color.rgb(0, 0, 0));
                }

                inputAuth.setTextColor(Color.rgb(0, 0, 0));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SharedPreferences spf = getSharedPreferences("user", 0);
        realPhoneNum = spf.getString("phone", "");

        if(spf.getString("email", "").equals("위대한올마이티")){
            findViewById(R.id.adminLogout).setVisibility(View.VISIBLE);
            findViewById(R.id.adminLogout).setOnClickListener(new View.OnClickListener() {
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
            });
        }

        findViewById(R.id.reSms).setOnClickListener(sms);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    View.OnClickListener sms = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            findViewById(R.id.smsCert).setVisibility(View.VISIBLE);
            TextView timerText = findViewById(R.id.timer);
            timerText.setVisibility(View.VISIBLE);

            if(v.getId() == R.id.reSms){
                timer.cancel();
                timer.start();
            }else {
                timer.start();
            }

            final String phoneNum = ((EditText)findViewById(R.id.inputAuth)).getText().toString();
            MakeCertNumber certNumber = new MakeCertNumber();
            final String autoNum = certNumber.numberGen(6, 1);

            if(kind.equals("findPassword")){
                EditText emailEt = findViewById(R.id.email);
                String emailStr = emailEt.getText().toString();
                emailEt.setVisibility(View.GONE);

                if(emailStr == null | emailStr.isEmpty() | emailStr.length() == 0){
                    Toast.makeText(BaobabOptionCert.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else if(!emailStr.contains("@")){
                    Toast.makeText(BaobabOptionCert.this, "이메일을 정확하게 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    sendAuthNum(v, cpId, urlCode, phoneNum, autoNum);
                }
            }else if(kind.equals("payCancel")){
                sendAuthNum(v, cpId, urlCode, phoneNum, autoNum);
            }else {
                if(!kind.equals("option") | realPhoneNum.equals(phoneNum)){
                    sendAuthNum(v, cpId, urlCode, phoneNum, autoNum);
                }else {
                    Toast.makeText( BaobabOptionCert.this, "휴대폰 번호가 다릅니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT ).show();
                }
            }
        }
    };

    public void sendAuthNum(final View v, String cpId, String urlCode, final String phoneNum, String autoNum){
        final LoadDialog loading = new LoadDialog(this);
        loading.showDialog();

        findViewById(R.id.smsCert).setVisibility(View.VISIBLE);
        Toast.makeText( getApplicationContext(), "인증번호가 올때까지 기다려주세요.", Toast.LENGTH_SHORT ).show();
        Call<CertificationVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).cert(cpId, urlCode, phoneNum, autoNum);
        call.enqueue(new Callback<CertificationVO>() {
            @Override
            public void onResponse(Call<CertificationVO> call, Response<CertificationVO> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d("통신 완료 : ", "완료");
                        certVo = response.body();

                        if(v.getId() == R.id.smsCert){
                            v.setBackgroundColor(Color.rgb(216, 220, 229));

                            findViewById(R.id.inputAuth).setVisibility(View.GONE);
                            findViewById(R.id.authLayout).setVisibility(View.VISIBLE);
                            findViewById(R.id.reSms).setVisibility(View.VISIBLE);

                            final EditText inputAuth = findViewById(R.id.authNum);

                            /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(inputAuth, 0);*/

                            /*inputAuth.setClickable(true);
                            inputAuth.setEnabled(true);
                            inputAuth.setFocusable(true);
                            inputAuth.setFocusableInTouchMode(true);*/

                            //inputAuth.performClick();

                            ((Button)v).setText("다음");
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final LoadDialog newLoading = new LoadDialog(BaobabOptionCert.this);
                                    newLoading.showDialog();

                                    LinearLayout lines = (LinearLayout) ((LinearLayout)findViewById(R.id.authLayout)).getChildAt(1);
                                    View[] arr = new View[6];

                                    for(int i=0;i<arr.length;i++){
                                        arr[i] = lines.getChildAt(i);
                                    }

                                    String userAuth = inputAuth.getText().toString();
                                    if(certVo == null){
                                        Toast.makeText( BaobabOptionCert.this, "인증번호가 올때까지 기다려 주세요.", Toast.LENGTH_SHORT ).show();
                                        newLoading.dialogCancel();
                                    }else if(!certVo.getResult().equals("Y")){
                                        Toast.makeText( BaobabOptionCert.this, "인증번호를 다시 받아주세요.", Toast.LENGTH_SHORT ).show();
                                        newLoading.dialogCancel();
                                    }else if(userAuth.length() == 0){
                                        Toast.makeText( BaobabOptionCert.this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT ).show();
                                        newLoading.dialogCancel();
                                    }else if(!userAuth.equals(certVo.getAuthNo())){
                                        Toast.makeText( BaobabOptionCert.this, "인증번호를 정확하게 입력해주세요.", Toast.LENGTH_SHORT ).show();
                                        findViewById(R.id.authFail).setVisibility(View.VISIBLE);

                                        for(int i=0;i<arr.length;i++){
                                            arr[i].setBackgroundColor(Color.rgb(255, 38, 80));
                                        }

                                        inputAuth.setTextColor(Color.rgb(255, 38, 80));

                                        newLoading.dialogCancel();
                                    }else {
                                        if(kind.equals("option")){
                                            newLoading.dialogCancel();
                                            Intent intent = new Intent(BaobabOptionCert.this, BaobabOption.class);
                                            startActivity(intent);
                                            finish();
                                        }else if(kind.equals("findEmail")){
                                            newLoading.dialogCancel();
                                            Intent intent = new Intent(BaobabOptionCert.this, BaobabFindEmail.class);
                                            intent.putExtra("phone", phoneNum);
                                            startActivity(intent);
                                            finish();
                                        }else if(kind.equals("findPassword")){
                                            newLoading.dialogCancel();
                                            Intent intent = new Intent(BaobabOptionCert.this, BaobabFindPassword.class);
                                            intent.putExtra("phone", phoneNum);
                                            intent.putExtra("email", ((EditText)findViewById(R.id.email)).getText().toString());
                                            startActivity(intent);
                                            finish();
                                        }else if(kind.equals("payCancel")){
                                            final Intent intent = new Intent(BaobabOptionCert.this, PayAndScanResult.class);
                                            final SendFCMtoBaobabServer sendFCMtoBaobabServer = new SendFCMtoBaobabServer(BaobabOptionCert.this);
                                            final String oid = getIntent().getStringExtra("oid");
                                            Call<PaycancelVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).payCancel(oid);
                                            call.enqueue(new Callback<PaycancelVO>() {
                                                @Override
                                                public void onResponse(Call<PaycancelVO> call, Response<PaycancelVO> response) {
                                                    if(response.isSuccessful()){
                                                        if(response.body() != null){
                                                            PaycancelVO vo = response.body();
                                                            if(vo.getResultCode().equals("00")){
                                                                intent.putExtra("title", "결제를 취소하였습니다.");
                                                                intent.putExtra("status", "결제를 정상적으로 취소하였습니다.");
                                                                intent.putExtra("div", "결제취소(성공)");
                                                                sendFCMtoBaobabServer.sendFCM("결제취소", "결제가 정상적으로 취소되었습니다.", "결제취소(사용자)", oid);
                                                                sendFCMtoBaobabServer.sendFCM("결제취소승인", "결제취소건이 있습니다.\n주문번호 : " + oid, "결제취소(사장님)", oid);
                                                                newLoading.dialogCancel();
                                                                Toast.makeText(BaobabOptionCert.this, "결제를 정상적으로 취소하였습니다.", Toast.LENGTH_SHORT).show();
                                                                startActivity(intent);
                                                                finish();
                                                            }else {
                                                                intent.putExtra("title", "결제 취소에 실패하였습니다.");
                                                                intent.putExtra("status", "[취소실패]" + vo.getResultMsg());
                                                                intent.putExtra("div", "결제취소(실패)");
                                                                sendFCMtoBaobabServer.sendFCM("결제취소", "[취소실패]" + vo.getResultMsg(), "결제취소(사용자, 실패)", oid);
                                                                newLoading.dialogCancel();
                                                                Toast.makeText(BaobabOptionCert.this, "[취소실패]" + vo.getResultMsg(), Toast.LENGTH_SHORT).show();
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }else {
                                                            Log.d("결제취소", "response 내용없음");
                                                            intent.putExtra("title", "결제 취소 오류");
                                                            intent.putExtra("status", "다시 시도해 주시기 바랍니다.");
                                                            intent.putExtra("div", "결제취소(오류)");
                                                            sendFCMtoBaobabServer.sendFCM("결제취소", "[취소실패] 다시 시도해 주시기 바랍니다.", "결제취소(사용자, 실패)", oid);
                                                            newLoading.dialogCancel();
                                                            Toast.makeText(BaobabOptionCert.this, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }else {
                                                        Log.d("결제취소", "서버 로그 확인 필요");
                                                        intent.putExtra("title", "결제 취소 오류");
                                                        intent.putExtra("status", "다시 시도해 주시기 바랍니다.");
                                                        intent.putExtra("div", "결제취소(오류)");
                                                        sendFCMtoBaobabServer.sendFCM("결제취소", "[취소실패] 다시 시도해 주시기 바랍니다.", "결제취소(사용자, 실패)", oid);
                                                        newLoading.dialogCancel();
                                                        Toast.makeText(BaobabOptionCert.this, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<PaycancelVO> call, Throwable t) {
                                                    Log.d("결제취소", t.getLocalizedMessage());
                                                    intent.putExtra("title", "결제 취소 오류");
                                                    intent.putExtra("status", "다시 시도해 주시기 바랍니다.");
                                                    intent.putExtra("div", "결제취소(오류)");
                                                    newLoading.dialogCancel();
                                                    sendFCMtoBaobabServer.sendFCM("결제취소", "[취소실패] 다시 시도해 주시기 바랍니다.", "결제취소(사용자, 실패)", oid);
                                                    Toast.makeText(BaobabOptionCert.this, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                            loading.dialogCancel();
                        }else {
                            loading.dialogCancel();
                        }
                    }else {
                        Log.d("서버 에러 : ", "response 내용없음");
                        loading.dialogCancel();
                    }
                }else {
                    Log.d("서버 에러 : ", "서버 로그를 확인바람");
                    loading.dialogCancel();
                }
            }

            @Override
            public void onFailure(Call<CertificationVO> call, Throwable t) {
                Log.d("통신 실패 : ", t.getLocalizedMessage());
                loading.dialogCancel();
                Toast.makeText( BaobabOptionCert.this, "오류입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT ).show();
            }
        });
    }
}
