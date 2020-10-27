package com.baobab.user.baobabflyer.activityLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabPage;
import com.baobab.user.baobabflyer.MainPopup;
import com.baobab.user.baobabflyer.MeCertWebView;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.adapter.MainAdAdapter;
import com.baobab.user.baobabflyer.server.adapter.MainBottomListAdapter;
import com.baobab.user.baobabflyer.server.adapter.MainTopListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.CircleAnimIndicator;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.vo.AdImgVO;
import com.baobab.user.baobabflyer.server.vo.AnterMainNeedVO;
import com.baobab.user.baobabflyer.server.vo.AnterMainVO;
import com.baobab.user.baobabflyer.server.vo.MainListTotalVO;
import com.baobab.user.baobabflyer.server.vo.MainTitleVO;
import com.baobab.user.baobabflyer.server.vo.PageNeedVO;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AnterMainLoader {

    RetroSingleTon retroSingleTon;

    Context appContext;
    Context activityContext;
    Activity activity;

    AnterMainNeedVO mainVO;

    AutoScrollViewPager autoScrollViewPager;
    CircleAnimIndicator circleAnimIndicator;

    List<MainTitleVO> titleList;

    public AnterMainLoader(Context appContext, Context activityContext, Activity activity, AnterMainNeedVO mainVO) {
        this.appContext = appContext;
        this.activityContext = activityContext;
        this.activity = activity;
        this.mainVO = mainVO;

        titleList = new ArrayList<>();
    }

    public void activityLoad() {
        Call<AnterMainVO> call = retroSingleTon.getInstance().getRetroInterface(appContext).anterMain(mainVO.getLongitude(), mainVO.getLatitude(), mainVO.getThisVersion(), mainVO.getUser(), "android");
        call.enqueue(new Callback<AnterMainVO>() {
            @Override
            public void onResponse(Call<AnterMainVO> call, Response<AnterMainVO> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        AnterMainVO vo = response.body();

                        titleList = vo.getMainTitle();

                        Log.d("버전체크", String.valueOf(vo.getVersion()));
                        if (vo.getVersion() == 0) {
                            AlertDialog.Builder alt_bld = new AlertDialog.Builder(activityContext)
                                    .setMessage("업데이트 후 사용해주세요.")
                                    .setCancelable(false)
                                    .setPositiveButton("업데이트 바로가기",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {
                                                    Intent marketLaunch = new Intent(
                                                            Intent.ACTION_VIEW);
                                                    marketLaunch.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.baobab.user.baobabflyer"));
                                                    activity.startActivity(marketLaunch);
                                                    activity.finish();
                                                }
                                            });
                            AlertDialog alert = alt_bld.create();
                            alert.setTitle("안 내");
                            alert.show();
                        } else {
                            cpListSetting(vo.getMainListVO(), vo.getMainTitle());
                            adImgSetting(vo.getAdImgVOList());
                            userDataChecking(activityContext.getSharedPreferences("user", 0).getString("email", ""));
                            try {
                                popupSetting();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.d("AnterMainLoader", "response 내용없음");
                    }
                } else {
                    Log.d("AnterMainLoader", "서버로그확인 필요");
                }
            }

            @Override
            public void onFailure(Call<AnterMainVO> call, Throwable t) {
                Log.d("AnterMainLoader", Objects.requireNonNull(t.getLocalizedMessage()));
            }
        });
    }

    private void userDataChecking(final String email){
        if(!email.equals("")){
            Call<UserAllVO> call = retroSingleTon.getInstance().getRetroInterface(appContext).checkUserData(email);
            call.enqueue(new Callback<UserAllVO>() {
                @Override
                public void onResponse(Call<UserAllVO> call, Response<UserAllVO> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            UserAllVO vo = response.body();
                            if(vo.getBirth().equals("00000000")){
                                AlertDialog.Builder alt_bld = new AlertDialog.Builder(activityContext)
                                        .setMessage("본인인증이 필요합니다.")
                                        .setCancelable(false)
                                        .setPositiveButton("지금하기", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent intent = new Intent(activityContext, MeCertWebView.class);
                                                        intent.putExtra("kind", "check");
                                                        intent.putExtra("email", email);
                                                        activityContext.startActivity(intent);
                                                    }
                                                })
                                        .setNegativeButton("나중에하기", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = alt_bld.create();
                                alert.setTitle("안 내");
                                alert.show();
                            }
                        }else {
                            Log.d("유저 데이터 체킹", "response 내용없음");
                        }
                    }else {
                        Log.d("유저 데이터 체킹", "서버로그 확인 필요");
                    }
                }

                @Override
                public void onFailure(Call<UserAllVO> call, Throwable t) {
                    Log.d("유저 데이터 체킹", Objects.requireNonNull(t.getLocalizedMessage()));
                }
            });
        }
    }

    public void cpListSetting(List<MainListTotalVO> list, List<MainTitleVO> titleList) {
        LoadDialog loading = new LoadDialog(activityContext);
        loading.showDialog();

        for(int i=0;i<titleList.size();i++){
            if(titleList.get(i).getTitleDiv().equals("t")){
                if(titleList.get(i).getTitleStatus().equals("on")){
                    activity.findViewById(R.id.top_list_layout).setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = activity.findViewById(R.id.top_list);
                    recyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);

                    MainTopListAdapter mainTopListAdapter = new MainTopListAdapter(listDiv(list, "t"), activityContext);
                    recyclerView.removeAllViewsInLayout();
                    recyclerView.setAdapter(mainTopListAdapter);

                    ((TextView)activity.findViewById(R.id.top_title)).setText(titleList.get(i).getMainText());
                    if(titleList.get(i).getSubText() == null){
                        activity.findViewById(R.id.top_sub_title).setVisibility(View.GONE);
                    }else {
                        ((TextView)activity.findViewById(R.id.top_sub_title)).setText(titleList.get(i).getSubText());
                    }

                }else {
                    activity.findViewById(R.id.top_list_layout).setVisibility(View.GONE);
                }
            }else if(titleList.get(i).getTitleDiv().equals("b")){
                if(titleList.get(i).getTitleStatus().equals("on")){
                    activity.findViewById(R.id.top_list_layout).setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = activity.findViewById(R.id.bot_list);
                    recyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext);
                    recyclerView.setLayoutManager(layoutManager);

                    MainBottomListAdapter mainBottomListAdapter = new MainBottomListAdapter(listDiv(list, "b"), activityContext);
                    recyclerView.removeAllViewsInLayout();
                    recyclerView.setAdapter(mainBottomListAdapter);

                    ((TextView)activity.findViewById(R.id.bot_title)).setText(titleList.get(i).getMainText());
                    ((TextView)activity.findViewById(R.id.bot_sub_title)).setText(titleList.get(i).getSubText());

                    if(titleList.get(i).getSubText() == null){
                        activity.findViewById(R.id.bot_sub_title).setVisibility(View.GONE);
                    }else {
                        ((TextView)activity.findViewById(R.id.bot_sub_title)).setText(titleList.get(i).getSubText());
                    }
                }else {
                    activity.findViewById(R.id.bot_list_layout).setVisibility(View.GONE);
                }
            }
        }

        loading.dialogCancel();
    }

    public List<MainListTotalVO> listDiv(List<MainListTotalVO> list, String div){
        List<MainListTotalVO> newList = new ArrayList<>();

        for(int i=0;i<list.size();i++){
            if(list.get(i).getMainListVO().getListDiv().equals(div)){
                newList.add(list.get(i));
            }
        }

        return newList;
    }

    public void adImgSetting(List<AdImgVO> adList) {
        autoScrollViewPager = activity.findViewById(R.id.ad_viewPager);
        if(adList.isEmpty()){
            autoScrollViewPager.setVisibility(View.GONE);
        }else{
            MainAdAdapter autoScrollAdapter = new MainAdAdapter(adList, appContext);
            autoScrollViewPager.setAdapter(autoScrollAdapter);
            autoScrollViewPager.setInterval(3500);


            circleAnimIndicator  = activity.findViewById(R.id.circleIndicator);
            circleAnimIndicator.removeAllViews();
            autoScrollViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    circleAnimIndicator.selectDot(i);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            circleAnimIndicator.setItemMargin(4);
            circleAnimIndicator.setAnimDuration(300);
            circleAnimIndicator.createDotPanel(adList.size(), R.drawable.default_dot, R.drawable.select_dot);

            autoScrollViewPager.startAutoScroll();
        }
    }

    public void goPage(View v){
        PageNeedVO vo = new PageNeedVO();

        String user;

        SharedPreferences userSpf = activityContext.getSharedPreferences("user", 0);
        if(userSpf.getString("email", "").equals("")){
            userSpf = activityContext.getSharedPreferences("visiter", 0);
            user = userSpf.getString("userCode", "");
        }else {
            user = userSpf.getString("email", "");
        }

        String category;
        if(v.getId() == R.id.safe_cp){
            category = "음식점";
            vo.setSearchWord("");
        }else if(v.getId() == R.id.safe_cafe){
            category = "카페";
            vo.setSearchWord("");
        }else {
            category = "";
            try {
                vo.setSearchWord(((EditText)v).getText().toString());
            }catch (Exception e){
                vo.setSearchWord("");
                e.printStackTrace();
            }
        }

        vo.setSortDiv("거리순");
        vo.setCategoryDiv(category);
        vo.setLocation("");
        vo.setRoot("android");
        vo.setService("위치기반 입점업체 검색");
        vo.setThirdPerson("제 3자 제공없음");
        vo.setTabDiv("전체");
        vo.setUser(user);
        vo.setLatitude(Double.parseDouble(activityContext.getSharedPreferences("gps", MODE_PRIVATE).getString("latitude", "0.0")));
        vo.setLongitude(Double.parseDouble(activityContext.getSharedPreferences("gps", MODE_PRIVATE).getString("longitude", "0.0")));
        vo.setTopPageInt(0);
        vo.setBotPageInt(0);
        vo.setTitleList(titleList);

        Intent intent = new Intent(activityContext, BaobabPage.class);
        intent.putExtra("pageVO", vo);
        intent.putExtra("userLocation", activityContext.getSharedPreferences("gps", MODE_PRIVATE).getString("addr", ""));
        activityContext.startActivity(intent);
    }

    private void popupSetting() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SharedPreferences spf = activityContext.getSharedPreferences("popup", MODE_PRIVATE);
        boolean isNotView = spf.getBoolean("see", false);
        if(isNotView){
            Log.d("팝업", "isNotView true");
            String startDate = spf.getString("start", "");
            if(startDate.equals("")){
                Log.d("팝업", "스타트 없음");
                SharedPreferences.Editor editor = spf.edit();
                editor.putString("start", format.format(new Date()));
                editor.putBoolean("see", true);
                editor.apply();

                goPopup();
            }else {
                Log.d("팝업", "스타트 있음");
                Date startDt = format.parse(startDate);
                Date today = new Date();

                long calDate = today.getTime() - startDt.getTime();
                long calDateDays = calDate / (24*60*60*1000);
                calDateDays = Math.abs(calDateDays);

                if(calDateDays > 7){
                    SharedPreferences.Editor editor = spf.edit();
                    editor.putString("start", format.format(today));
                    editor.putBoolean("see", true);
                    editor.apply();

                    goPopup();
                }
            }
        }else {
            Log.d("팝업", "isNotView false");
            SharedPreferences.Editor editor = spf.edit();
            editor.putString("start", format.format(new Date()));
            editor.putBoolean("see", true);
            goPopup();
        }
    }

    private void goPopup(){
        Intent intent = new Intent(activityContext, MainPopup.class);
        activity.startActivity(intent);
    }
}
