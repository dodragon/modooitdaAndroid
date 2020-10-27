package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.server.util.SslWebViewClient;

public class BaobabAddressSearch extends AppCompatActivity {

    private WebView webView;
    private WebView child;
    private TextView result;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_address_search);


        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
    }

    public void init_webView() {
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebChromeClient(new BaobabAddressSearch.BaobabWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new BaobabAddressSearch.AndroidBridge(getIntent().getStringExtra("classKind")), "BaobabApp");
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.onWindowFocusChanged(true);
        webView.setWebViewClient(new SslWebViewClient());
        webView.loadUrl("https://www.baobab858.com/jsp/addrSearch.api");
    }

    private class AndroidBridge {

        String classKind;

        public AndroidBridge(String classKind) {
            this.classKind = classKind;
        }

        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Intent asPage;
                    if(classKind.equals("update")){
                        asPage = new Intent( BaobabAddressSearch.this, BaobabCpJoin.class );
                    }else {
                        asPage = new Intent( BaobabAddressSearch.this, BaobabCpInfoUpdate.class );
                    }
                    asPage.putExtra( "zipcode", arg1 );
                    asPage.putExtra( "address1", makeDetailAddr(arg2) );
                    asPage.putExtra( "address2", arg3 );
                    setResult(1, asPage);
                    finish();

                }
            });
        }
    }

    private class BaobabWebChromeClient extends WebChromeClient{
        @Override
        public boolean onCreateWindow(WebView webView, boolean isDialog, boolean isUserGesture, Message resultMsg){
            child = new WebView(BaobabAddressSearch.this);
            child.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            BaobabAddressSearch.this.webView.addView(child);
            child.getSettings().setJavaScriptEnabled(true);

            ((WebView.WebViewTransport)resultMsg.obj).setWebView(child);
            resultMsg.sendToTarget();

            child.bringToFront();
            return true;
        }
    }

    public String makeDetailAddr(String addr){
        if(addr.startsWith("경기 ")){
            addr.replace("경기 ", "경기도 ");
        }else if(addr.startsWith("강원 ")){
            addr.replace("강원 ", "강원도");
        }else if(addr.startsWith("충북 ")){
            addr.replace("충북 ", "충청북도 ");
        }else if(addr.startsWith("충남 ")){
            addr.replace("충남 ", "충청남도 ");
        }else if(addr.startsWith("경북 ")){
            addr.replace("경북 ", "경상북도 ");
        }else if(addr.startsWith("충북 ")){
            addr.replace("경남 ", "경상남도 ");
        }else if(addr.startsWith("전남 ")){
            addr.replace("전남 ", "전라남도 ");
        }else if(addr.startsWith("전북 ")){
            addr.replace("전북 ", "전라남도 ");
        }else if(addr.startsWith("제주 ") || addr.startsWith("제주도 ")){
            addr.replace(addr.split(" ")[0], "제주특별자치도 ");
        }else if(addr.startsWith("세종 ") || addr.startsWith("세종시 ")){
            addr.replace(addr.split(" ")[0], "세종특별자치시 ");
        }else if(addr.startsWith("서울 ") || addr.startsWith("서울시 ")){
            addr.replace(addr.split(" ")[0], "서울특별시 ");
        }else if(addr.startsWith("인천 ") || addr.startsWith("인천시 ")){
            addr.replace(addr.split(" ")[0], "인천광역시 ");
        }else if(addr.startsWith("광주 ") || addr.startsWith("광주시 ")){
            addr.replace(addr.split(" ")[0], "광주광역시 ");
        }else if(addr.startsWith("대전 ") || addr.startsWith("대전시 ")){
            addr.replace(addr.split(" ")[0], "대전광역시 ");
        }else if(addr.startsWith("대구 ") || addr.startsWith("대구시 ")){
            addr.replace(addr.split(" ")[0], "대구광역시 ");
        }else if(addr.startsWith("부산 ") || addr.startsWith("부산시 ")){
            addr.replace(addr.split(" ")[0], "부산광역시 ");
        }

        return addr;
    }
}