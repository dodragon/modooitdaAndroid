package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.baobab.user.baobabflyer.server.util.SendFCMtoBaobabServer;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.google.gson.Gson;

import org.apache.http.util.EncodingUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaypleWebView extends AppCompatActivity {

    WebView webView;
    String payWork;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payple_web_view);

        SharedPreferences spf = getSharedPreferences("user", 0);
        email = spf.getString("email", "");
        String phoneNum = spf.getString("phone", "");

        String isSimple = getIntent().getStringExtra("isSimple");
        String payerId = getIntent().getStringExtra("payerId");
        payWork = getIntent().getStringExtra("payWork");

        if(payerId == null){
            payerId = "";
        }

        PaymentVO vo = (PaymentVO) getIntent().getSerializableExtra("vo");
        vo.setPg("payple");

        String postData = "buyer_hp=" + phoneNum + "&buyer_email=" + email + "&buy_goods=" + vo.getGoods() + "&buy_total=" + vo.getTotalDisPrice() + "&order_num=" + vo.getOrderNum() + "&is_taxsave=Y&is_simple=" + isSimple
                + "&payer_id=" + payerId + "&pay_work=" + payWork;

        webView = findViewById(R.id.payWeb);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new PaypleWebViewClient(vo));
        webView.setWebChromeClient(new WebChromeClient());
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webView.postUrl("https://baobab858.com/jsp/payple.api", EncodingUtils.getBytes(postData, "BASE64"));
    }

    private class PaypleWebViewClient extends WebViewClient{

        RetroSingleTon retroSingleTon;
        PaymentVO vo;

        public PaypleWebViewClient(PaymentVO vo) {
            this.vo = vo;
        }

        @Override
        public void onPageFinished(final WebView view, String url){
            CookieSyncManager.getInstance().sync();
            Log.d("온페이지 피니쉬드 : ", url);

            final Intent intent = new Intent(PaypleWebView.this, PayAndScanResult.class);

            if(url.contains("paySuccess.api")){
                Gson gson = new Gson();
                vo.setTid(view.getTitle());

                String paymentData = gson.toJson(vo);
                try{
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
                                            Toast.makeText(PaypleWebView.this, "결제가 완료되었습니다.", Toast.LENGTH_LONG).show();
                                            SendFCMtoBaobabServer sendFCM = new SendFCMtoBaobabServer(PaypleWebView.this);
                                            Gson gson = new Gson();
                                            DecimalFormat decimalFormat = new DecimalFormat("###,###");
                                            //sendFCM.sendFCM("결제승인", vo.getGoods() + " 상품이 결제 되었습니다.\n주문번호 : " + vo.getOrderNum(), "결제(사장님)", gson.toJson(vo));
                                            sendFCM.sendFCM("결제완료", vo.getGoods() + " 상품이 결제 되었습니다. (" + decimalFormat.format(vo.getTotalDisPrice()) + "원)", "결제(사용자)", gson.toJson(vo));
                                            //sendFCM.sendFCM("결제완료 : " + vo.getCpName(), vo.getEmail() + "고객님께서 " + vo.getGoods() + "상품을 결제 하셨습니다.\n" + "(" +
                                                    //decimalFormat.format(vo.getTotalDisPrice()) + "원)", "결제(관리자)", null);
                                            intent.putExtra("title", "결제가 완료되었습니다.");
                                            intent.putExtra("status", vo.getGoods() + " 상품이 결제 되었습니다. (" + decimalFormat.format(vo.getTotalDisPrice()) + "원)");
                                            intent.putExtra("div", "pay(성공)");
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Toast.makeText(PaypleWebView.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                            Log.d("이벤트 결제 insert", "result <= 0");
                                            intent.putExtra("title", "결제에 실패했습니다.");
                                            intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                            intent.putExtra("div", "pay(오류)");
                                            startActivity(intent);
                                            finish();
                                        }
                                    }else {
                                        Toast.makeText(PaypleWebView.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                        Log.d("이벤트 결제 insert", "response 내용없음");
                                        intent.putExtra("title", "결제에 실패했습니다.");
                                        intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                        intent.putExtra("div", "pay(오류)");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else {
                                    Toast.makeText(PaypleWebView.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(PaypleWebView.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                Log.d("이벤트 결제 insert", t.getLocalizedMessage());
                                intent.putExtra("title", "결제에 실패했습니다.");
                                intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                intent.putExtra("div", "pay(오류)");
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
                                            Toast.makeText(PaypleWebView.this, "결제가 완료되었습니다.", Toast.LENGTH_LONG).show();
                                            SendFCMtoBaobabServer sendFCM = new SendFCMtoBaobabServer(PaypleWebView.this);
                                            Gson gson = new Gson();
                                            DecimalFormat decimalFormat = new DecimalFormat("###,###");
                                            sendFCM.sendFCM("결제승인", vo.getGoods() + "이 결제 되었습니다.\n주문번호 : " + vo.getOrderNum(), "결제(사장님)", gson.toJson(vo));
                                            sendFCM.sendFCM("결제완료", vo.getGoods() + "이 결제 되었습니다. (" + decimalFormat.format(vo.getTotalDisPrice()) + "원)", "결제(사용자)", gson.toJson(vo));
                                            sendFCM.sendFCM("결제완료 : " + vo.getCpName(), vo.getEmail() + "고객님께서 " + vo.getGoods() + "상품을 결제 하셨습니다.\n" + "(" +
                                                    decimalFormat.format(vo.getTotalDisPrice()) + "원)", "결제(관리자)", null);
                                            intent.putExtra("title", "자유 이용권 결제가 완료되었습니다.");
                                            intent.putExtra("status", vo.getGoods() + "이 결제 되었습니다. (" + decimalFormat.format(vo.getTotalDisPrice()) + "원)");
                                            intent.putExtra("div", "pay(성공)");
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Toast.makeText(PaypleWebView.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                            Log.d("자유 이용권 결제 insert", "서버로그 확인 필요");
                                            intent.putExtra("title", "자유 이용권 결제에 실패했습니다.");
                                            intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                            intent.putExtra("div", "pay(오류)");
                                            startActivity(intent);
                                            finish();
                                        }
                                    }else {
                                        Toast.makeText(PaypleWebView.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                        Log.d("자유 이용권 결제 insert", "서버로그 확인 필요");
                                        intent.putExtra("title", "자유 이용권 결제에 실패했습니다.");
                                        intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                        intent.putExtra("div", "pay(오류)");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else {
                                    Toast.makeText(PaypleWebView.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                    Log.d("자유 이용권 결제 insert", "서버로그 확인 필요");
                                    intent.putExtra("title", "자유 이용권 결제에 실패했습니다.");
                                    intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                    intent.putExtra("div", "pay(오류)");
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable t) {
                                Toast.makeText(PaypleWebView.this, "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.", Toast.LENGTH_LONG).show();
                                Log.d("자유 이용권 결제 insert", t.getLocalizedMessage());
                                intent.putExtra("title", "자유 이용권 결제에 실패했습니다.");
                                intent.putExtra("status", "결제 중 문제가 발생했습니다. 고객센터로 문의 바랍니다.");
                                intent.putExtra("div", "pay(오류)");
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();

                    Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).accountInsert(email, vo.getTid());
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    int result = response.body();

                                    if(result > 0){
                                        Toast.makeText(PaypleWebView.this, "계좌 등록이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                        intent.putExtra("title", "계좌 등록이 완료되었습니다.");
                                        intent.putExtra("status", "[계좌등록성공]");
                                        intent.putExtra("div", "pay(성공)");
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(PaypleWebView.this, "계좌 등록에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                        intent.putExtra("title", "계좌 등록에 실패하였습니다. 고객센터로 문의 바랍니다.");
                                        intent.putExtra("status", "[계좌등록실패]");
                                        intent.putExtra("div", "pay(실패)");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else {
                                    Toast.makeText(PaypleWebView.this, "계좌 등록에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                    intent.putExtra("title", "계좌 등록에 실패하였습니다. 고객센터로 문의 바랍니다.");
                                    intent.putExtra("status", "[계좌등록실패]");
                                    intent.putExtra("div", "pay(실패)");
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                Toast.makeText(PaypleWebView.this, "계좌 등록에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                intent.putExtra("title", "계좌 등록에 실패하였습니다. 고객센터로 문의 바랍니다.");
                                intent.putExtra("status", "[계좌등록실패]");
                                intent.putExtra("div", "pay(실패)");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Toast.makeText(PaypleWebView.this, "계좌 등록에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            intent.putExtra("title", "계좌 등록에 실패하였습니다. 고객센터로 문의 바랍니다.");
                            intent.putExtra("status", "[계좌등록실패]");
                            intent.putExtra("div", "pay(실패)");
                            startActivity(intent);
                            finish();
                        }
                    });

                }
            }else if(url.contains("payFail.api")){
                if(payWork.equals("PAY")){
                    Toast.makeText(PaypleWebView.this, "[결제실패]" + view.getTitle(), Toast.LENGTH_LONG).show();
                    intent.putExtra("title", "결제에 실패했습니다.");
                    intent.putExtra("status", "[결제실패]" + view.getTitle());
                    intent.putExtra("div", "pay(실패)");
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(PaypleWebView.this, "[계좌등록실패]" + view.getTitle(), Toast.LENGTH_LONG).show();
                    intent.putExtra("title", "계좌등록에 실패했습니다.");
                    intent.putExtra("status", "[계좌등록실패]" + view.getTitle());
                    intent.putExtra("div", "pay(실패)");
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
}
