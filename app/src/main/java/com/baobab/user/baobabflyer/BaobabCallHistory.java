package com.baobab.user.baobabflyer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.activityLoader.AllActivityLoader;
import com.baobab.user.baobabflyer.server.singleTons.BizcallSingleTon;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.baobab.user.baobabflyer.server.vo.CPmainImgVO;
import com.baobab.user.baobabflyer.server.vo.CallHistoryVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.UserLocationVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Typeface.BOLD;

public class BaobabCallHistory extends AppCompatActivity {
    RetroSingleTon retroSingleTon;
    BizcallSingleTon bizcallSingleTon;

    String telNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_call_history );


        final Context thisAc = BaobabCallHistory.this;

        AllActivityLoader allActivityLoader = new AllActivityLoader(thisAc, getApplicationContext());

        String userPhone = getLocalPhoneNumber();
        Call<List<CallHistoryVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).showHistory(userPhone);
        call.enqueue(new Callback<List<CallHistoryVO>>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<List<CallHistoryVO>> call, final Response<List<CallHistoryVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d("통신 완료", "완료");
                        final List<CallHistoryVO> list = response.body();

                        final LinearLayout mainLayout = findViewById(R.id.historyLayout);

                        DisplayMetrics dm = getResources().getDisplayMetrics();
                        int size1 = Math.round(12 * dm.density);
                        int size2 = Math.round(2 * dm.density);
                        int size3 = Math.round(15 * dm.density);
                        int size4 = Math.round(6 * dm.density);
                        int size6 = Math.round(24 * dm.density);
                        int size40 = Math.round( 40 * dm.density );

                        for(int i=0;i<list.size();i++){
                            final CallHistoryVO vo = list.get(i);
                            final String cpNameForGo = list.get(i).getCp_name();
                            final String addrForGo = list.get(i).getCp_address();

                            LinearLayout firstLayout = new LinearLayout(getApplicationContext());
                            LinearLayout.LayoutParams firstParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            firstLayout.setLayoutParams(firstParam);
                            firstLayout.setOrientation(LinearLayout.VERTICAL);
                            mainLayout.addView(firstLayout);
                            firstLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(vo.getCp_div().equals("our")){
                                        Call<CPInfoVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getForPoke(cpNameForGo, addrForGo);
                                        call.enqueue(new Callback<CPInfoVO>() {
                                            @Override
                                            public void onResponse(Call<CPInfoVO> call, Response<CPInfoVO> response) {
                                                if(response.isSuccessful()){
                                                    if(response.body() != null){
                                                        Log.d("통신완료 : ", "CallHist to detail");
                                                        final CPInfoVO vo = response.body();

                                                        Call<List<CPmainImgVO>> imgCall = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getMainImg(vo.getSeq_num());
                                                        imgCall.enqueue(new Callback<List<CPmainImgVO>>() {
                                                            @Override
                                                            public void onResponse(Call<List<CPmainImgVO>> call, Response<List<CPmainImgVO>> response) {
                                                                if(response.isSuccessful()){
                                                                    if(response.body() != null){
                                                                        Log.d("등록업체 이미지 : ", "통신완료");
                                                                        List<CPmainImgVO> imgVoList = response.body();

                                                                        ArrayList<String> imgUrlList = new ArrayList<>();
                                                                        for(int i=0;i<imgVoList.size();i++){
                                                                            imgUrlList.add(imgVoList.get(i).getImg_url());
                                                                        }

                                                                        UserLocationVO ulvo = getUserLoc(getLocalPhoneNumber());
                                                                        Intent goToDetail = new Intent(BaobabCallHistory.this, BaobabMenu.class);
                                                                        goToDetail.putExtra("context", "callHis");
                                                                        goToDetail.putExtra("userLoc", ulvo.getAddr());
                                                                        goToDetail.putExtra("info", vo);
                                                                        goToDetail.putExtra("longitude", ulvo.getLongitude());
                                                                        goToDetail.putExtra("latitude", ulvo.getLatitude());
                                                                        goToDetail.putExtra("imgUrl", imgUrlList);
                                                                        startActivity(goToDetail);
                                                                        finish();
                                                                    }else {
                                                                        Log.d("등록업체 이미지 : ", "response 내용없음");
                                                                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                                                        intent.putExtra("status", "오류");
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                }else {
                                                                    Log.d("등록업체 이미지 : ", "서버에러 (서버로그 확인 필)");
                                                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                                                    intent.putExtra("status", "오류");
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<List<CPmainImgVO>> call, Throwable t) {
                                                                Log.d("등록업체 이미지 : ", "통신실패 : " + t.getLocalizedMessage());
                                                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                                                intent.putExtra("status", "오류");
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                                    }else {
                                                        Log.d("통신실패 : ", "response 내용없음");
                                                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                                        intent.putExtra("status", "오류");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }else {
                                                    Log.d("통신실패 : ", "서버에러");
                                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                                    intent.putExtra("status", "오류");
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<CPInfoVO> call, Throwable t) {
                                                Log.d("통신실패 : ", "안드 : " + t.getLocalizedMessage());
                                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                                intent.putExtra("status", "오류");
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });

                            LinearLayout secondLayout = new LinearLayout(getApplicationContext());
                            LinearLayout.LayoutParams secondParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            secondParam.leftMargin = size1;
                            secondParam.rightMargin = size1;
                            secondLayout.setWeightSum( 1 );
                            secondLayout.setLayoutParams(secondParam);
                            secondLayout.setOrientation(LinearLayout.HORIZONTAL);
                            firstLayout.addView(secondLayout);

                            LinearLayout thirdLayout = new LinearLayout(getApplicationContext());
                            LinearLayout.LayoutParams thirdParam = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                            thirdParam.weight = (float) 0.7;
                            thirdLayout.setLayoutParams(thirdParam);
                            thirdLayout.setOrientation(LinearLayout.VERTICAL);
                            secondLayout.addView(thirdLayout);

                            TextView cpNameTv = new TextView(getApplicationContext());
                            LinearLayout.LayoutParams cpNameParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            cpNameParam.bottomMargin = size2;
                            cpNameTv.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 15 );
                            cpNameTv.setLayoutParams(cpNameParam);
                            cpNameTv.setText(list.get(i).getCp_name());
                            cpNameTv.setTextColor(Color.BLACK);
                            cpNameTv.setTypeface(null, BOLD);
                            thirdLayout.addView(cpNameTv);

                            TextView addressTv = new TextView(getApplicationContext());
                            LinearLayout.LayoutParams addressParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            addressParam.bottomMargin = size4;
                            addressTv.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 13 );
                            addressTv.setLayoutParams(addressParam);
                            addressTv.setText(list.get(i).getCp_address());
                            addressTv.setTextColor(Color.rgb(151, 151, 151));
                            thirdLayout.addView(addressTv);

                            TextView dateTv = new TextView(getApplicationContext());
                            LinearLayout.LayoutParams dateParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dateTv.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 13 );
                            dateTv.setLayoutParams(dateParam);
                            SimpleDateFormat day = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss a");
                            dateTv.setText(day.format(list.get(i).getCall_date()));
                            dateTv.setTextColor(Color.rgb(151, 151, 151));
                            thirdLayout.addView(dateTv);

                            LinearLayout thirdOneLayout = new LinearLayout(getApplicationContext());
                            LinearLayout.LayoutParams thirdOneParam = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                            /*thirdOneParam.leftMargin  = size7/7*4;*/
                            thirdOneParam.weight = (float) 0.3;
                            thirdOneLayout.setLayoutParams(thirdOneParam);
                            thirdOneLayout.setOrientation(LinearLayout.HORIZONTAL);
                            secondLayout.addView(thirdOneLayout);

                            Button delBtn = new Button(getApplicationContext());
                            LinearLayout.LayoutParams btnParam = new LinearLayout.LayoutParams(size6*2, size6*2);
                            btnParam.topMargin = size3;
                            btnParam.leftMargin = size40;
                            delBtn.setBackgroundResource( R.drawable.back6 );
                            delBtn.setTextColor( Color.rgb( 255, 255, 255 ) );
                            delBtn.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 12 );
                            delBtn.setText("삭제");
                            delBtn.setLayoutParams(btnParam);
                            delBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mainLayout.removeView((View) v.getParent().getParent().getParent());

                                    Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).deleteCallHis(vo.getSeq_num());
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            Log.d("전화내역 : ", "삭제완료");
                                            Toast.makeText(BaobabCallHistory.this, "전화내역이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Log.d("전화내역 : ", "삭제실패" + t.getLocalizedMessage());
                                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                            intent.putExtra("status", "오류");
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });
                            thirdOneLayout.addView(delBtn);

                            /*ImageButton callBtn = new ImageButton(getApplicationContext());
                            LinearLayout.LayoutParams callParam = new LinearLayout.LayoutParams(size6, size6);
                            callParam.leftMargin = size5;
                            callParam.topMargin = size3;
                            callBtn.setLayoutParams(callParam);
                            callBtn.setBackground(getResources().getDrawable(R.drawable.list_call));
                            final String callNum = list.get(i).getCp_phone();
                            final String cpName = list.get(i).getCp_name();
                            final String addr = list.get(i).getCp_address();
                            callBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String iid = "fspsjeieh488vnfoebso";
                                    String rn = callNum.replaceAll("[^0-9]","");
                                    BaobabMenu baobabMenu = new BaobabMenu();
                                    String auth = new String(org.apache.commons.codec.binary.Base64.encodeBase64(baobabMenu.md5(iid + rn).getBytes()));
                                    Call<AutoMappingVO> biz = bizcallSingleTon.getInstance().getBizcallInterface().autoMapping(iid, rn, "test", auth);
                                    biz.enqueue(new Callback<AutoMappingVO>() {
                                        @Override
                                        public void onResponse(Call<AutoMappingVO> call, Response<AutoMappingVO> response) {
                                            AutoMappingVO vo = response.body();
                                            if(vo.getRt() != 0){
                                                Log.d("맵핑 : ", "실패사유 : " + vo.getRs());
                                                Toast.makeText(getApplicationContext(), "등록된 번호가 없습니다.", Toast.LENGTH_LONG).show();
                                            }else {
                                                telNum = vo.getVn();
                                                Intent callTo = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + telNum));
                                                startActivity(callTo);
                                                Log.d("맵핑 : ", "성공");

                                                String userPhone = getLocalPhoneNumber();

                                                Call<ResponseBody> callHistory = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getCallHistory(userPhone, callNum, cpName, addr, div);
                                                callHistory.enqueue(new Callback<ResponseBody>(){
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        Log.d("통신 완료", "Call History 저장완료");
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<AutoMappingVO> call, Throwable t) {
                                            Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                                        }
                                    });
                                }
                            });
                            thirdOneLayout.addView(callBtn);*/

                            View hisLine = new View(getApplicationContext());
                            LinearLayout.LayoutParams lineParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size2);
                            lineParam.topMargin = size3/3;
                            lineParam.bottomMargin = size3/3;
                            hisLine.setLayoutParams(lineParam);
                            hisLine.setBackgroundColor(Color.rgb( 213, 213, 213 ));
                            firstLayout.addView(hisLine);
                        }
                    }else {
                        Log.d("통신 실패", "실패1 : response내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("통신 실패", "실패2 : 서버 에러");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<CallHistoryVO>> call, Throwable t) {
                Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    public UserLocationVO getUserLoc(String user){
        final UserLocationVO[] vo = {new UserLocationVO()};
        Call<UserLocationVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).selectUserLoc(user);
        call.enqueue(new Callback<UserLocationVO>() {
            @Override
            public void onResponse(Call<UserLocationVO> call, Response<UserLocationVO> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d("통신완료", "사용자위치정보 받음");
                        vo[0] = response.body();

                    }else {
                        Log.d("통신싪패", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("통신실패", "서버에러");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserLocationVO> call, Throwable t) {
                Log.d("통신실패", "안드로이드 : " + t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
        return vo[0];
    }

    @SuppressLint("MissingPermission")
    public String getLocalPhoneNumber() {
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber ="";
        try {
            if (telephony.getLine1Number() != null) {
                phoneNumber = telephony.getLine1Number();
            }
            else {
                if (telephony.getSimSerialNumber() != null) {
                    phoneNumber = telephony.getSimSerialNumber();
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(BaobabCallHistory.this, BaobabMain.class);
                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(intent);
        }
        return false;
    }*/


}