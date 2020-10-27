package com.baobab.user.baobabflyer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

public class AdWebViewActivity extends AppCompatActivity {

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_web_view);

        String url = getIntent().getStringExtra("url");

        mWebView = findViewById(R.id.scanWebView);
        mWebView.setWebViewClient(new AdWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);

        Log.d("유알엘", url);

        assert url != null;
        if(url.contains("baobabimage")){
            String data="<html><head><title>바오밥</title><meta name=\"viewport\"\"content=\"width="+"100%"+", initial-scale=0.65 \" /></head>";
            data=data+"<body><center><img width=\""+"100%"+"\" src=\""+url+"\" /></center></body></html>";

            mWebView.loadData(data, "text/html", null);
        }else {
            mWebView.loadUrl(url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//뒤로가기 버튼 이벤트
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {//웹뷰에서 뒤로가기 버튼을 누르면 뒤로가짐
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class AdWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("웹뷰클라이어늩", url);

            if(url.startsWith("http")){
                view.loadUrl(url);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }
    }
}
