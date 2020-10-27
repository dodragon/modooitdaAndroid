package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.baobab.user.baobabflyer.server.util.SslWebViewClient;

import java.util.ArrayList;

public class BaobabAddressSearch2 extends AppCompatActivity {

    private WebView webView;
    private WebView child;
    private Handler handler;

    String addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_address_search2);

        init_webView();

        handler = new Handler();

        findViewById( R.id.btn_map ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btn_map = new Intent( BaobabAddressSearch2.this, BaobabGps.class );
                Intent getinfo = getIntent();
                final double longitude = getinfo.getDoubleExtra( "longitude", 0 );
                final double latitude = getinfo.getDoubleExtra( "latitude", 0 );
                btn_map.putExtra( "latitude", latitude );
                btn_map.putExtra( "longitude", longitude );
                startActivity( btn_map );
            }
        } );
    }

    public void init_webView() {
        webView = findViewById(R.id.webView);
        webView.setWebChromeClient(new BaobabWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "BaobabApp");
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.onWindowFocusChanged(true);
        webView.setWebViewClient(new SslWebViewClient());
        webView.loadUrl("https://www.baobab858.com/jsp/addrSearch.api");
    }

    private class BaobabWebChromeClient extends WebChromeClient{
        @Override
        public boolean onCreateWindow(WebView webView, boolean isDialog, boolean isUserGesture, Message resultMsg){
            child = new WebView(BaobabAddressSearch2.this);
            child.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            BaobabAddressSearch2.this.webView.addView(child);
            child.getSettings().setJavaScriptEnabled(true);

            ((WebView.WebViewTransport)resultMsg.obj).setWebView(child);
            resultMsg.sendToTarget();

            child.bringToFront();
            return true;
        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    addr = arg2 + " " + arg3;
                    GeocodeUtil geocodeUtil = new GeocodeUtil( getApplicationContext() );
                    ArrayList<GeocodeUtil.GeoLocation> list =  geocodeUtil.getGeoLocationListUsingAddress( addr );
                    Intent asPage = new Intent( BaobabAddressSearch2.this, BaobabAnterMain.class );

                    double longitude = list.get(0).longitude;
                    double latitude = list.get(0).latitude;

                    asPage.putExtra("lon", longitude);
                    asPage.putExtra("lat", latitude);
                    asPage.putExtra("needChange", 1);

                    SharedPreferences gpsSpf = getSharedPreferences("gps", MODE_PRIVATE);
                    SharedPreferences.Editor editor = gpsSpf.edit();
                    editor.putString("longitude", String.valueOf(longitude));
                    editor.putString("latitude", String.valueOf(latitude));
                    editor.apply();

                    startActivity(asPage);
                    finish();
                }
            });
        }
    }
}