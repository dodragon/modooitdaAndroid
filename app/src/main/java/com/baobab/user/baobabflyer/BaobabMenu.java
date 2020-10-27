package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baobab.user.baobabflyer.activityLoader.MenuLoader;
import com.baobab.user.baobabflyer.server.adapter.MenuThemes;
import com.baobab.user.baobabflyer.server.util.FreeTicketDialog;
import com.baobab.user.baobabflyer.server.util.MenuSelectDialog;
import com.baobab.user.baobabflyer.server.util.Point;
import com.baobab.user.baobabflyer.server.util.ViewAnimationUtil;
import com.baobab.user.baobabflyer.server.vo.HitsVO;
import com.baobab.user.baobabflyer.server.vo.MenuNeedVO;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.apmem.tools.layouts.FlowLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.DeviceId;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class BaobabMenu extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    RetroSingleTon retroSingleTon;

    LinearLayout mainMenu;
    LinearLayout ectMenu;
    LinearLayout sideMenu;
    LinearLayout drinkMenu;
    CPInfoVO vo;
    int cpSeq;
    String cpNameForAll;
    String locName;
    String context;

    double longitude;
    double latitude;
    String userAddr;

    private FirebaseAuth auth;
    private GoogleApiClient gac;

    public static final String COUNTLY_URL = "http://analytics.smartcontentcenter.kr";
    public static final String COUNTLY_APP_KEY = "b67686583c86fe06523787f9c927a5a405592259";

    public static int pokeResult;

    ViewAnimationUtil animationUtil;

    LinearLayout[] tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());

        Countly.sharedInstance().init(this, COUNTLY_URL, COUNTLY_APP_KEY, null, DeviceId.Type.OPEN_UDID);
        Countly.sharedInstance().onStart(BaobabMenu.this);

        SharedPreferences spf = getSharedPreferences("gps", MODE_PRIVATE);
        longitude = Double.parseDouble(spf.getString("longitude", "0"));
        latitude = Double.parseDouble(spf.getString("latitude", "0"));
        userAddr = spf.getString("addr", "");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gac = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        auth = FirebaseAuth.getInstance();
    }


    public void baobabMenuStart() {
        MenuNeedVO needVO = new MenuNeedVO();
        needVO.setCpName(vo.getCP_name());
        needVO.setCpSeq(vo.getSeq_num());

        new MenuLoader(getApplicationContext(), this, this, needVO, vo.getCp_div(), vo);

        String email = getSharedPreferences("user", 0).getString("email", "");

        pokeConfirm(email);
        defaultSetting();
    }

    public void pokeConfirm(final String email) {
        if(email.equals("")){
            pokeSetting(0, email);
        }else {
            Call<Integer> poke = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).pokeConfirm(email, vo.getCP_name(), "our", vo.getSeq_num());
            poke.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            pokeResult = response.body();
                            ToggleButton pokeBtn = findViewById(R.id.pokeBtn);
                            if (pokeResult > 0) {
                                pokeBtn.setChecked(true);
                                pokeBtn.setBackground(getDrawable(R.drawable.ic_favorite_white_active));
                            } else {
                                pokeBtn.setChecked(false);
                                pokeBtn.setBackground(getDrawable(R.drawable.ic_favorite_white));
                            }
                            pokeSetting(pokeResult, email);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });
        }
    }

    public void defaultSetting() {
        tabs = new LinearLayout[]{
                findViewById(R.id.menu_tab),
                findViewById(R.id.info_tab),
                findViewById(R.id.review_tab)
        };

        for(int i=0;i<tabs.length;i++){
            tabs[i].setOnClickListener(tabListener);
        }

        tabs[0].performClick();

        cpSeq = vo.getSeq_num();
        cpNameForAll = String.valueOf(vo.getCP_name());
        locName = vo.getCP_address() + " " + vo.getCP_addr_details();

        mainMenu = findViewById(R.id.mainMenu);
        sideMenu = findViewById(R.id.sideMenu);
        ectMenu = findViewById(R.id.ectMenu);
        drinkMenu = findViewById(R.id.drinkMenu);

        findViewById(R.id.locSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locSelect = new Intent(BaobabMenu.this, BaobabLocSelect.class);

                locSelect.putExtra("longitude", longitude);
                locSelect.putExtra("latitude", latitude);
                locSelect.putExtra("cpLon", vo.getLongitude());
                locSelect.putExtra("cpLat", vo.getLatitude());
                locSelect.putExtra("cpAddr", vo.getCP_address() + " " + vo.getCP_addr_details());
                locSelect.putExtra("cpName", cpNameForAll);
                locSelect.putExtra("userAddr", userAddr);
                startActivity(locSelect);
            }
        });

        TextView cpNameTv = findViewById(R.id.cpName);
        cpNameTv.setSelected(true);
        cpNameTv.setText(String.valueOf(vo.getCP_name()));

        String addr = vo.getCP_address();
        ((TextView)findViewById(R.id.addr)).setText(addr);
        TextView closeDayTv = findViewById(R.id.closeDay);
        closeDayTv.setText(makeCloseDay(vo.getClose_day()) + "\n" + vo.getClose_ect());
        TextView businessTimeTv = findViewById(R.id.businessTime);
        businessTimeTv.setText(vo.getBusiness_start() + " ~ " + vo.getBusiness_end());
        TextView parkingTv = findViewById(R.id.parking);
        parkingTv.setText(String.valueOf(vo.getParking()));
        ((TextView) findViewById(R.id.intro)).setText(vo.getCP_intro());
        findViewById(R.id.phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + vo.getCP_phon())));
            }
        });

        if (!vo.getCP_Theme1().equals("[]"))
            new MenuThemes(vo.getCP_Theme1(), (FlowLayout) findViewById(R.id.flowLayout), this);

        TextView revGrade = findViewById(R.id.rev_grade);
        revGrade.setText(String.valueOf(vo.getRev_grade()));

        findViewById(R.id.select_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = getSharedPreferences("user", 0);
                if(spf.getString("email", "").equals("")){
                    Toast.makeText(BaobabMenu.this, "로그인 이후 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                }else {
                    MenuSelectDialog dialog = MenuSelectDialog.newInstance(cpSeq, BaobabMenu.this, cpNameForAll, false, null, null, 0);
                    dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
                    dialog.show(getSupportFragmentManager(), "");
                }
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        LinearLayout infoBtn = findViewById(R.id.info_btn_layout);
        LinearLayout introBtn = findViewById(R.id.intro_btn_layout);

        infoBtn.setTag(true);
        introBtn.setTag(true);

        animationUtil = new ViewAnimationUtil();
        infoBtn.setOnClickListener(animListener);
        introBtn.setOnClickListener(animListener);

        setFreeTicketBtn(vo.getCpPercentage(), vo.getCpPassword());
    }

    private void setFreeTicketBtn(final double percentAge, final String pw){
        if(percentAge > 0){
            findViewById(R.id.freeTicketLayout).setVisibility(View.VISIBLE);
            Button btn = findViewById(R.id.freeTicketBtn);
            btn.setText(percentAge + "% 할인받아 결제하기");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getSharedPreferences("user", 0).getString("email", "").equals("")){
                        Toast.makeText(BaobabMenu.this, "로그인 이후 이용 바랍니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        FreeTicketDialog dialog = FreeTicketDialog.newInstance(percentAge, cpSeq, BaobabMenu.this, pw, cpNameForAll);
                        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
                        dialog.show(getSupportFragmentManager(), "");
                    }
                }
            });
        }else {
            findViewById(R.id.freeTicketLayout).setVisibility(View.GONE);
        }
    }

    View.OnClickListener tabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int selectIndex = 858;

            for(int i=0;i<tabs.length;i++){
                ((TextView)tabs[i].getChildAt(0)).setTextColor(Color.parseColor("#b7b7b7"));
                tabs[i].getChildAt(1).setVisibility(GONE);

                if(v.getId() == tabs[i].getId()){
                    selectIndex = i;
                }
            }

            ((TextView)tabs[selectIndex].getChildAt(0)).setTextColor(Color.parseColor("#5c7cfa"));
            tabs[selectIndex].getChildAt(1).setVisibility(View.VISIBLE);

            switch (v.getId()){
                case R.id.menu_tab:
                    findViewById(R.id.eventMother).setVisibility(View.VISIBLE);
                    findViewById(R.id.infoMother).setVisibility(View.GONE);
                    findViewById(R.id.reviews_layout).setVisibility(View.GONE);
                    break;
                case R.id.info_tab:
                    findViewById(R.id.eventMother).setVisibility(View.GONE);
                    findViewById(R.id.infoMother).setVisibility(View.VISIBLE);
                    findViewById(R.id.reviews_layout).setVisibility(View.GONE);
                    break;
                case R.id.review_tab:
                    findViewById(R.id.eventMother).setVisibility(View.GONE);
                    findViewById(R.id.infoMother).setVisibility(View.GONE);
                    findViewById(R.id.reviews_layout).setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    View.OnClickListener animListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.info_btn_layout){
                ImageView infoBtn = findViewById(R.id.cp_info_btn);
                if((boolean)v.getTag()){
                    animationUtil.collapse(findViewById(R.id.info));
                    v.setTag(false);
                    infoBtn.setImageResource(R.drawable.ic_chevron_dropdown);
                }else {
                    animationUtil.expand(findViewById(R.id.info));
                    v.setTag(true);
                    infoBtn.setImageResource(R.drawable.ic_chevron_dropdown2);
                }
            }else {
                ImageView introBtn = findViewById(R.id.cp_intro_btn);
                if((boolean)v.getTag()){
                    animationUtil.collapse(findViewById(R.id.intro));
                    v.setTag(false);
                    introBtn.setImageResource(R.drawable.ic_chevron_dropdown);
                }else {
                    animationUtil.expand(findViewById(R.id.intro));
                    v.setTag(true);
                    introBtn.setImageResource(R.drawable.ic_chevron_dropdown2);
                }
            }
        }
    };

    public void pokeSetting(final int pokeResult, final String email) {
        ToggleButton pokeBtn = findViewById(R.id.pokeBtn);
        pokeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(email.equals("")){
                    if(isChecked){
                        Toast.makeText(BaobabMenu.this, "로그인 후 이용하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                    }
                }else {
                    if (pokeResult == 0) {
                        if (isChecked == true) {
                            buttonView.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white_active));
                            Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getPoke(email, cpNameForAll, vo.getCP_address() + " " + vo.getCP_addr_details(), vo.getRev_grade(),
                                    0, "our", vo.getSeq_num());
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Log.d("통신 완료", "완료");
                                    Toast.makeText(getApplicationContext(), "찜하기 완료", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            buttonView.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white));
                            Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).delPoke(email, cpNameForAll, "our", vo.getSeq_num());
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Log.d("통신 완료", "완료");
                                    Toast.makeText(getApplicationContext(), "찜하기 삭제 완료", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    } else {
                        if (isChecked == true) {
                            buttonView.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white_active));
                        } else {
                            buttonView.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white));
                            Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).delPoke(email, cpNameForAll, "our", vo.getSeq_num());
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Log.d("통신 완료", "완료");
                                    Toast.makeText(getApplicationContext(), "찜하기 삭제 완료", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public String makeCloseDay(String cd) {
        String result = "정기휴무 없음";
        String[] cdArr = makeCloseArr(cd.replace("무", "").split("~"));
        String[] type = {"매주", "첫째주", "둘째주", "셋째주", "넷째주", "다섯째주"};

        if (cdArr.length == 0) {
            return result;
        } else {
            result = "";
            for (int i = 0; i < cdArr.length; i++) {
                if (cdArr[i] != null && cdArr[i].length() > 0 && !cdArr[i].equals("")) {
                    result += type[i] + " : " + cdArr[i];
                    if (i != cdArr.length - 1) {
                        result += "\n";
                    }
                }
            }
            return result;
        }
    }

    public String[] makeCloseArr(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (i > 0 && arr[i].contains(arr[0])) {
                arr[i] = arr[i].replace(arr[0], "");
                if (arr[i].indexOf(",") == 0) {
                    arr[i] = arr[i].substring(1);
                }
            }
        }
        return arr;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (context.equals("page")) {
                        onBackPressed();
                        finish();
                    } else if (context.equals("call")) {
                        Intent intent = new Intent(BaobabMenu.this, BaobabPoke.class);
                        startActivity(intent);
                        finish();
                    } else if (context.equals("callHis")) {
                        Intent intent = new Intent(BaobabMenu.this, BaobabCallHistory.class);
                        startActivity(intent);
                        finish();
                    } else if (context.equals("push") || context.equals("link")) {
                        Intent intent = new Intent(BaobabMenu.this, BaobabAnterMain.class);
                        startActivity(intent);
                        finishAffinity();
                    } else if (context.equals("recommend")) {
                        try {
                            Intent intent = new Intent(BaobabMenu.this, BaobabMenu.class);
                            intent.putExtra("context", "page");
                            intent.putExtra("info", getIntent().getSerializableExtra("info"));
                            intent.putExtra("longitude", getIntent().getDoubleExtra("longitude", 0));
                            intent.putExtra("latitude", getIntent().getDoubleExtra("latitude", 0));
                            startActivity(intent);
                        } catch (Exception e) {
                            onBackPressed();
                        }
                    } else {
                        onBackPressed();
                    }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_baobab_menu);
            context = extras.getString("context");
            String hitDiv = "normal";
            if (context.equals("push")) {
                String email = getSharedPreferences("user", 0).getString("email", "");
                Point point = new Point();
                point.pointUpdate(email, getApplicationContext());
                hitDiv = "push";
            }
            longitude = extras.getDouble("longitude");
            latitude = extras.getDouble("latitude");
            vo = (CPInfoVO) extras.getSerializable("info");
            baobabMenuStart();
            hitsUp(hitDiv);
        } else {
            Log.d("엑스트라 : ", "널 !!!!!!!!!");
        }
    }

    public void hitsUp(String hitDiv){
        HitsVO vo = new HitsVO();
        SharedPreferences spf = getSharedPreferences("user", 0);
        String userInfo = spf.getString("email", "");
        if(userInfo.equals("")){
            SharedPreferences spf2 = getSharedPreferences("visiter", 0);
            userInfo = spf2.getString("userCode", "");
        }

        vo.setCpName(cpNameForAll);
        vo.setCpSeq(cpSeq);
        vo.setHitDiv(hitDiv);
        vo.setUserInfo(userInfo);

        Gson gson = new Gson();
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).hitsUp(gson.toJson(vo));
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();

                        if(result > 0){
                            Log.d("조회수", "성공");
                        }else {
                            Log.d("조회수", "insert 실패");
                        }
                    }else {
                        Log.d("조회수", "response 내용없음");
                    }
                }else {
                    Log.d("조회수", "서버로그확인 필요");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("조회수", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}