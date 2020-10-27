package com.baobab.user.baobabflyer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.util.SendFCMtoBaobabServer;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.google.gson.Gson;

import org.apache.http.util.EncodingUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Payment extends AppCompatActivity {
    RetroSingleTon retroSingleTon;

    AlertDialog alertIsp;
    WebView webView;
    String postData;
    String result;

    PaymentVO vo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        alertIsp = new AlertDialog.Builder(Payment.this)
                .setIcon(R.drawable.logo_and)
                .setTitle("알림")
                .setMessage("모바일 ISP 어플리케이션이 설치되지 않았습니다.\n설치를 눌러 진행 해주십시오.\n취소를 누르면 결제가 취소 됩니다.")
                .setPositiveButton("설치", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        webView.loadUrl("https://mobile.vpay.co.kr/jsp/MISP/andown.jsp");
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Payment.this, "(-1)결제를 취소 하셨습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).create();


        Intent getVO = getIntent();
        vo = (PaymentVO) getVO.getSerializableExtra("vo");
        vo.setPg("inicis");
        postData = "orderNum=" + vo.getOrderNum() + "&userPhone=" + vo.getUserPhone() + "&totalDisPrice=" + vo.getTotalDisPrice() + "&cpName=" + vo.getCpName() + "&email=" + vo.getEmail() + "&goods=" + vo.getGoods();

        webView = findViewById(R.id.payWeb);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new INIPWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webView.postUrl("https://www.baobab858.com/jsp/payment.api", EncodingUtils.getBytes(postData, "BASE64"));

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        CookieSyncManager.createInstance(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    public void onPause(){
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    private class INIPWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            CookieSyncManager.getInstance().startSync();

            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
                Intent intent;
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
                    Log.d("<이니시스> ", "URISyntaxError : " + url + " : " + e.getMessage());
                    return false;
                }
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    if (url.startsWith("ispmobile://")) {
                        alertIsp.show();
                        return false;
                    } else if (url.startsWith("intent://")) {
                        try {
                            Intent excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                            String packageName = excepIntent.getPackage();

                            excepIntent = new Intent(Intent.ACTION_VIEW);
                            excepIntent.setData(Uri.parse("market://search?q=" + packageName));
                            excepIntent.addCategory( "android.intent.category.BROWSABLE" );
                            excepIntent.setSelector( null );
                            excepIntent.setComponent( null );

                            view.getContext().startActivity(excepIntent);
                        } catch (URISyntaxException e1) {
                            Log.e("<INIPAYMOBILE>", "INTENT:// 인입될시 예외 처리  오류 : " + e1);
                        }
                    }
                }
            } else{
                view.loadUrl(url);
                return false;
            }
            return true;
        }

        @SneakyThrows
        @Override
        public void onPageFinished(final WebView view, String url){
            CookieSyncManager.getInstance().sync();
            Log.d("온페이지 피니쉬드 : ", url);

            final Intent intent = new Intent(Payment.this, PayAndScanResult.class);

            if(url.contains("paySuccess.api")){
                Gson gson = new Gson();
                vo.setTid(view.getTitle());

                String paymentData = gson.toJson(vo);
                if(vo.getOrderNum().startsWith("P")){
                    ArrayList<EventCpMenuVO> list = (ArrayList<EventCpMenuVO>) getIntent().getSerializableExtra("eventList");
                    String eventListData = gson.toJson(list);

                    Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).eventPayInsert(paymentData, eventListData);
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    int result = response.body();

                                    if(result > 0){
                                        Toast.makeText(Payment.this, "결제가 완료되었습니다.", Toast.LENGTH_LONG).show();
                                        SendFCMtoBaobabServer sendFCM = new SendFCMtoBaobabServer(Payment.this);
                                        Gson gson = new Gson();
                                        DecimalFormat decimalFormat = new DecimalFormat("###,###");
                                        sendFCM.sendFCM("결제승인", vo.getGoods() + " 상품이 결제 되었습니다.\n주문번호 : " + vo.getOrderNum(), "결제(사장님)", gson.toJson(vo));
                                        sendFCM.sendFCM("결제완료", vo.getGoods() + " 상품이 결제 되었습니다. (" + decimalFormat.format(vo.getTotalDisPrice()) + "원)", "결제(사용자)", gson.toJson(vo));
                                        sendFCM.sendFCM("결제완료 : " + vo.getCpName(), vo.getEmail() + "고객님께서 " + vo.getGoods() + "상품을 결제 하셨습니다.\n" + "(" +
                                                decimalFormat.format(vo.getTotalDisPrice()) + "원)", "결제(관리자)", null);
                                        intent.putExtra("title", "결제가 완료되었습니다.");
                                        intent.putExtra("status", vo.getGoods() + " 상품이 결제 되었습니다. (" + decimalFormat.format(vo.getTotalDisPrice()) + "원)");
                                        intent.putExtra("div", "pay(성공)");
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                        Log.d("이벤트 결제 insert", "result <= 0");
                                        intent.putExtra("title", "결제에 실패했습니다.");
                                        intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                        intent.putExtra("div", "pay(오류)");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else {
                                    Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                    Log.d("이벤트 결제 insert", "response 내용없음");
                                    intent.putExtra("title", "결제에 실패했습니다.");
                                    intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                    intent.putExtra("div", "pay(오류)");
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                Log.d("이벤트 결제 insert", "서버로그 확인 필요");
                                intent.putExtra("title", "결제에 실패했습니다.");
                                intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                intent.putExtra("div", "pay(오류)");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                            Log.d("이벤트 결제 insert", t.getLocalizedMessage());
                            intent.putExtra("title", "결제에 실패했습니다.");
                            intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                            intent.putExtra("div", "pay(오류)");
                            startActivity(intent);
                            finish();
                        }
                    });
                }else if(vo.getOrderNum().startsWith("M")){
                    Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).pushPayment(gson.toJson(vo));
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    int result = response.body();
                                    if(result > 0){
                                        Toast.makeText(Payment.this, "결제가 완료되었습니다.", Toast.LENGTH_LONG).show();
                                        SendFCMtoBaobabServer sendFCM = new SendFCMtoBaobabServer(Payment.this);
                                        Gson gson = new Gson();
                                        DecimalFormat decimalFormat = new DecimalFormat("###,###");
                                        sendFCM.sendFCM("결제승인", vo.getGoods() + " 상품이 결제 되었습니다.\n주문번호 : " + vo.getOrderNum(), "결제(사장님)", gson.toJson(vo));
                                        intent.putExtra("title", "결제가 완료되었습니다.");
                                        intent.putExtra("status", vo.getGoods() + "가 결제 완료 되었습니다. (" + decimalFormat.format(vo.getTotalDisPrice()) + "원)");
                                        intent.putExtra("div", "push(성공)");
                                        intent.putExtra("cpName", vo.getCpName());
                                        intent.putExtra("cpSeq", vo.getCpSeq());
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Log.d("푸시이용권 결제", "result 값 0이하");
                                        Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                        Log.d("이벤트 결제 insert", "서버로그 확인 필요");
                                        intent.putExtra("title", "결제에 실패했습니다.");
                                        intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                        intent.putExtra("div", "push(오류)");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else{
                                    Log.d("푸시이용권 결제", "response 내용 없음");
                                    Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                    Log.d("이벤트 결제 insert", "서버로그 확인 필요");
                                    intent.putExtra("title", "결제에 실패했습니다.");
                                    intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                    intent.putExtra("div", "push(오류)");
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                Log.d("푸시이용권 결제", "서버로그 확인필요");
                                Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                Log.d("이벤트 결제 insert", "서버로그 확인 필요");
                                intent.putExtra("title", "결제에 실패했습니다.");
                                intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                intent.putExtra("div", "push(오류)");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.d("푸시이용권 결제", t.getLocalizedMessage());
                            Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                            Log.d("이벤트 결제 insert", "서버로그 확인 필요");
                            intent.putExtra("title", "결제에 실패했습니다.");
                            intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                            intent.putExtra("div", "push(오류)");
                            startActivity(intent);
                            finish();
                        }
                    });
                }else if(vo.getOrderNum().startsWith("F")){
                    Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).freeTicket(vo.getEmail(), vo.getTotalPrice(), vo.getTotalDisPrice(), vo.getCpName(), vo.getCpSeq());
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    int result = response.body();

                                    if(result > 0){
                                        Toast.makeText(Payment.this, "결제가 완료되었습니다.", Toast.LENGTH_LONG).show();
                                        SendFCMtoBaobabServer sendFCM = new SendFCMtoBaobabServer(Payment.this);
                                        Gson gson = new Gson();
                                        DecimalFormat decimalFormat = new DecimalFormat("###,###");
                                        //sendFCM.sendFCM("결제승인", vo.getGoods() + "이 결제 되었습니다.\n주문번호 : " + vo.getOrderNum(), "결제(사장님)", gson.toJson(vo));
                                        sendFCM.sendFCM("결제완료", vo.getGoods() + "이 결제 되었습니다. (" + decimalFormat.format(vo.getTotalDisPrice()) + "원)", "결제(사용자)", gson.toJson(vo));
                                        //sendFCM.sendFCM("결제완료 : " + vo.getCpName(), vo.getEmail() + "고객님께서 " + vo.getGoods() + "상품을 결제 하셨습니다.\n" + "(" +
                                                //decimalFormat.format(vo.getTotalDisPrice()) + "원)", "결제(관리자)", null);
                                        intent.putExtra("title", "자유 이용권 결제가 완료되었습니다.");
                                        intent.putExtra("status", vo.getGoods() + "이 결제 되었습니다. (" + decimalFormat.format(vo.getTotalDisPrice()) + "원)");
                                        intent.putExtra("div", "pay(성공)");
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                        Log.d("자유 이용권 결제 insert", "서버로그 확인 필요");
                                        intent.putExtra("title", "자유 이용권 결제에 실패했습니다.");
                                        intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                        intent.putExtra("div", "pay(오류)");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else {
                                    Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                    Log.d("자유 이용권 결제 insert", "서버로그 확인 필요");
                                    intent.putExtra("title", "자유 이용권 결제에 실패했습니다.");
                                    intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                    intent.putExtra("div", "pay(오류)");
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                Log.d("자유 이용권 결제 insert", "서버로그 확인 필요");
                                intent.putExtra("title", "자유 이용권 결제에 실패했습니다.");
                                intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                intent.putExtra("div", "free(오류)");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Toast.makeText(Payment.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                            Log.d("자유 이용권 결제 insert", t.getLocalizedMessage());
                            intent.putExtra("title", "결제에 실패했습니다.");
                            intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                            intent.putExtra("div", "pay(오류)");
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }else if(url.contains("payFail.api")){
                Toast.makeText(Payment.this, "[결제실패]" + "한도초과 또는 사용 할 수 없는 카드 입니다.", Toast.LENGTH_LONG).show();
                intent.putExtra("title", "결제에 실패했습니다.");
                intent.putExtra("status", "[결제실패]" + "한도초과 또는 사용 할 수 없는 카드 입니다.");
                intent.putExtra("div", "pay(실패)");
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
            /*SslCertificate sslCertificateServer = error.getCertificate();
            Certificate pinnedCert = getCertificateForRawResource(R.raw.ssl, getApplicationContext());
            Certificate serverCert = convertSSLCertificateToCertificate(sslCertificateServer);

            if(pinnedCert.equals(serverCert)) {
                handler.proceed();
            } else {
                super.onReceivedSslError(view, handler, error);
            }*/
        }
    }

    public static Certificate getCertificateForRawResource(int resourceId, Context context) {
        CertificateFactory cf;
        Certificate ca = null;
        Resources resources = context.getResources();
        InputStream caInput = resources.openRawResource(resourceId);

        try {
            cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(caInput);
        } catch (CertificateException e) {
            Log.e("결제 ssl1", "exception", e);
        } finally {
            try {
                caInput.close();
            } catch (IOException e) {
                Log.e("결제 ssl2" , "exception", e);
            }
        }

        return ca;
    }

    public static Certificate convertSSLCertificateToCertificate(SslCertificate sslCertificate) {
        Certificate certificate = null;
        Bundle bundle = sslCertificate.saveState(sslCertificate);
        byte[] bytes = bundle.getByteArray("x509-certificate");

        if (bytes != null) {
            try {
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(bytes));
                certificate = cert;
            } catch (CertificateException e) {
                Log.e("결제 ssl3", "exception", e);
            }
        }
        return certificate;
    }
}