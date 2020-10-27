package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.util.MakeCertNumber;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;

import org.apache.http.util.EncodingUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeCertWebView extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    WebView webView;

    String kind;
    UserAllVO vo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_cert_web_view);

        kind = getIntent().getStringExtra("kind");

        String postData = "";
        if(kind.equals("join")){
            vo = (UserAllVO) getIntent().getSerializableExtra("userJoinVo");
            postData = "ranNum=" + new MakeCertNumber().numberGen(15, 1) + "&email=" + vo.getEmail() + "&pw=" + vo.getUser_password() + "&push=" + vo.getPush_agree() + "&kind=" + kind + "&nickName=" + vo.getNickName();
        }else {
            String email = getIntent().getStringExtra("email");
            postData = "ranNum=" + new MakeCertNumber().numberGen(15, 1) + "&email=" + email + "&kind=" + kind;
        }

        webView = findViewById(R.id.certWeb);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MeCertWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webView.postUrl("https://baobab858.com/jsp/meCert.api", EncodingUtils.getBytes(postData, "BASE64"));
    }

    private class MeCertWebViewClient extends WebViewClient{
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if(url.startsWith("intent://") || url.startsWith("tauthlink://") || url.startsWith("ktauthexternalcall://") || url.startsWith("upluscorporation://")) { // intent url 처리
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    startActivity(intent);
                }else{
                    view.loadUrl(url);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onPageFinished(final WebView view, String url){
            CookieSyncManager.getInstance().sync();
            Log.d("온페이지 피니쉬드 : ", url);

            if(url.contains("meCertResult.api")){
                String result = view.getTitle();
                if(result.startsWith("Y")){
                    if(kind.equals("join")){
                        final LoadDialog loading = new LoadDialog(MeCertWebView.this);
                        loading.showDialog();

                        Toast.makeText( MeCertWebView.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT ).show();
                        SharedPreferences spf = getSharedPreferences("user", 0);
                        SharedPreferences.Editor editor = spf.edit();
                        editor.putString("email", vo.getEmail());
                        editor.putString("pw", vo.getUser_password());
                        editor.putString("divCode", "u-01-01");
                        editor.putString("phone", result.split("/")[1]);
                        editor.putString("pushAgree", vo.getPush_agree());
                        editor.putString("loginDiv", "google");
                        editor.putInt("point", 0);
                        editor.putString("nickName", vo.getEmail());
                        editor.putString("profile", "이미지없음");
                        editor.commit();

                        Toast.makeText( MeCertWebView.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT ).show();

                        Call<ResponseBody> logHistory = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).loginHistory(vo.getEmail(), vo.getUser_password(), vo.getDiv_code(), "in");
                        logHistory.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d("로그인 세션", "완료");

                                Intent intent = new Intent(MeCertWebView.this, BaobabAnterMain.class);
                                intent.putExtra("userLogin", "userLogin");
                                loading.dialogCancel();
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "로그인이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                                Toast.makeText(getApplicationContext(), "로그인이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplication(), BaobabAnterMain.class);
                                intent.putExtra("status", "오류");
                                loading.dialogCancel();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else {
                        Toast.makeText(MeCertWebView.this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(MeCertWebView.this, "본인 인증에 실패하였습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
