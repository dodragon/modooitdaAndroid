package com.baobab.user.baobabflyer.server.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabAnterMain;
import com.baobab.user.baobabflyer.BaobabLogin;
import com.baobab.user.baobabflyer.BaobabUserTicketList;
import com.baobab.user.baobabflyer.PayAndScanResult;
import com.baobab.user.baobabflyer.ScanResult;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Scanner extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("QR코드에 카메라를 대세요.");
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null){
                String resultStr = result.getContents();
                if(resultStr.startsWith("P")){
                    productQrScan(resultStr, "cp", getSharedPreferences("user", 0).getString("email", ""), 0);
                }else if(resultStr.startsWith("http://") || resultStr.startsWith("https://")){
                    try{
                        if(resultStr.contains("https://baobab858.com/jsp/cpQr.api?cpSeq=")){
                            int cpSeq = Integer.parseInt(resultStr.split("=")[1]);
                            String serial = getIntent().getStringExtra("serial");
                            if(serial == null){
                                SharedPreferences spf = getSharedPreferences("user", 0);
                                if(spf.getString("email", "").equals("")){
                                    Toast.makeText(this, "로그인 이후 사용해 주세요.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, BaobabLogin.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(this, "티켓을 먼저 선택해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, BaobabUserTicketList.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                productQrScan(serial, "user", "", cpSeq);
                            }
                        }else if(resultStr.equals("https://baobab858.com/jsp/app.api")){
                            Intent intent = new Intent(this, BaobabAnterMain.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            Intent intent = new Intent(this, ScanResult.class);
                            intent.putExtra("url", resultStr);
                            startActivity(intent);
                            finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, "사용할 수 없는 QR 코드 입니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(this, "사용할 수 없는 QR 코드 입니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }else {
                Toast.makeText(this, "사용할 수 없는 QR 코드 입니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void productQrScan(final String serial, final String scanner, final String cpEmail, int cpSeq){
        final Intent intent = new Intent(Scanner.this, PayAndScanResult.class);
        final SendFCMtoBaobabServer fcm = new SendFCMtoBaobabServer(Scanner.this);
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).usedTicket(serial, scanner, cpEmail, cpSeq);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();

                        if(result == 0){
                            Toast.makeText(getApplicationContext(), "티켓사용이 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                            fcm.sendFCM("티켓 사용완료", "티켓사용이 완료되었습니다.", "티켓성공(사용자)", serial + "," + getLocalPhoneNumber());
                            fcm.sendFCM("티켓 사용완료", "티켓사용이 완료되었습니다.", "티켓성공(사장님)", serial + "," + cpEmail);
                            intent.putExtra("title", "스캔이 완료되었습니다.");
                            intent.putExtra("status", "티켓사용이 완료되었습니다.");
                            intent.putExtra("scanner", scanner);
                            intent.putExtra("div", "scan(성공)");
                            startActivity(intent);
                            finish();
                        }else if(result == 10){
                            Toast.makeText(getApplicationContext(), "이미 사용된 티켓입니다.", Toast.LENGTH_SHORT).show();
                            fcm.sendFCM("티켓 사용실패", "이미 사용되었거나 취소된 티켓을 스캔하셨습니다.", "티켓실패", serial + "," + getLocalPhoneNumber());
                            intent.putExtra("title", "스캔에 실패하였습니다.");
                            intent.putExtra("status", "이미 사용되었거나 취소된 티켓입니다.");
                            intent.putExtra("scanner", scanner);
                            intent.putExtra("div", "scan(실패)");
                            startActivity(intent);
                            finish();
                        }else if(result == 9){
                            Toast.makeText(getApplicationContext(), "일치하는 티켓 혹은 업체가 아닙니다.", Toast.LENGTH_SHORT).show();
                            fcm.sendFCM("티켓 사용실패", "해당 업체 티켓이 아닙니다. 티켓을 확인하세요.", "티켓실패", serial + "," + getLocalPhoneNumber());
                            intent.putExtra("title", "스캔에 실패하였습니다.");
                            intent.putExtra("status", "일치하는 티켓 혹은 업체가 아닙니다.");
                            intent.putExtra("scanner", scanner);
                            intent.putExtra("div", "scan(실패)");
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "스캔을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            fcm.sendFCM("티켓 사용 중 오류", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.", "티켓실패", serial + "," + getLocalPhoneNumber());
                            intent.putExtra("title", "스캔에 실패했습니다.");
                            intent.putExtra("status", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.");
                            intent.putExtra("scanner", scanner);
                            intent.putExtra("div", "scan(오류)");
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        Log.d("티켓사용", "response 내용없음");
                        Toast.makeText(getApplicationContext(), "스캔을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        fcm.sendFCM("티켓 사용 중 오류", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.", "티켓실패", serial + "," + getLocalPhoneNumber());
                        intent.putExtra("title", "스캔에 실패했습니다.");
                        intent.putExtra("status", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.");
                        intent.putExtra("scanner", scanner);
                        intent.putExtra("div", "scan(오류)");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("티켓사용", "서버로그 확인 필요");
                    Toast.makeText(getApplicationContext(), "스캔을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    fcm.sendFCM("티켓 사용 중 오류", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.", "티켓실패", serial + "," + getLocalPhoneNumber());
                    intent.putExtra("title", "스캔에 실패했습니다.");
                    intent.putExtra("status", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.");
                    intent.putExtra("scanner", scanner);
                    intent.putExtra("div", "scan(오류)");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("티켓사용", t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "스캔을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                fcm.sendFCM("티켓 사용 중 오류", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.", "티켓", serial + "," + getLocalPhoneNumber());
                intent.putExtra("title", "스캔에 실패했습니다.");
                intent.putExtra("status", "티켓 사용중 문제가 발생했습니다. 다시 시도해주세요.");
                intent.putExtra("div", "scan(오류)");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @SuppressLint("MissingPermission")
    public String getLocalPhoneNumber() {
        TelephonyManager telephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = "";
        try {
            if (telephony.getLine1Number() != null) {
                phoneNumber = telephony.getLine1Number();
            } else {
                if (telephony.getSimSerialNumber() != null) {
                    phoneNumber = telephony.getSimSerialNumber();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }
}