package com.baobab.user.baobabflyer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;

import org.apache.http.util.EncodingUtils;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabPushPayment extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    AlertDialog alertIsp;
    WebView webView;
    String postData;
    String result;
    int ea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_push_payment);

        alertIsp = new AlertDialog.Builder(BaobabPushPayment.this)
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
                        Toast.makeText(BaobabPushPayment.this, "(-1)결제를 취소 하셨습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).create();

        ea = getIntent().getIntExtra("ea", 0);
        postData = "goods=" + "푸시이용권 " + ea + "개" + "&ea=" + ea + "&totalDisPrice=" + getIntent().getIntExtra("pay", 0) + "&email=" +
                getSharedPreferences("user", 0).getString("email", "") + "&orderNum=" + orderNum(getSharedPreferences("user", 0).getString("phone", ""),
                getIntent().getIntExtra("cpSeq", 0)) + "&userPhone=" + getSharedPreferences("user", 0).getString("phone", "") + "&payCode=push" + "&status=승인대기" +
                "&cpSeq=" + getIntent().getIntExtra("cpSeq", 0);

        webView = findViewById(R.id.payWeb);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new BaobabPushPayment.INIPWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new BaobabPushPayment.ResultHandler(BaobabPushPayment.this), "getResult");
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

    public class ResultHandler{
        private final Handler handler = new Handler();
        Context mContext;
        ResultHandler(Context c){
            mContext = c;
        }

        @JavascriptInterface
        public void getResult(final String status){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    result = status;
                    Log.d("결제 상태 넘어옴?  :   ", status);
                }
            });
        }
    }

    public String orderNum(String userPhone, int cpSeq) {
        char[] arr = userPhone.substring(3, userPhone.length()).toCharArray();
        String front = "";
        String back = "";
        for (int i = 0; i < arr.length; i++) {
            if (i % 2 == 0) {
                front += arr[i];
            } else {
                back += arr[i];
            }
        }
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = dateFormat.format(date);
        return "P" + front + dateStr + back + String.valueOf(cpSeq);
    }

    private class INIPWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            CookieSyncManager.getInstance().startSync();

            Log.d("유알엘 !!!!!!!!!!!!!!!!!!!", "url : " + url);
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

                            Log.d("<INIPAYMOBILE>", "packageName : " + packageName);

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

        @Override
        public void onPageFinished(final WebView view, String url){
            CookieSyncManager.getInstance().sync();
            Log.d("온페이지 피니쉬드 : ", url);

            if(url.contains("payResult.api")){
                final Button submit = findViewById(R.id.submit);
                submit.setVisibility(View.VISIBLE);

                //webView.loadUrl("javascript:getResult()");

                if(result.equals("00") || result.equals("0")){
                    submit.setText("완료");
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submit.setVisibility(View.GONE);

                            Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).pushEaPlma(getSharedPreferences("user", 0).
                                    getString("email", ""), ea);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Intent intent = new Intent(BaobabPushPayment.this, BaobabPush.class);
                                    intent.putExtra("cpName", getIntent().getStringExtra("cpName"));
                                    intent.putExtra("cpSeq", getIntent().getIntExtra("cpSeq", 0));
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.d("푸시증가 실패", "통신에러 : " + t.getLocalizedMessage());
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });
                }else {
                    submit.setText("다시하기");
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            webView.postUrl("https://www.baobab858.com/jsp/payment.api", EncodingUtils.getBytes(postData, "BASE64"));
                            submit.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    }
}
