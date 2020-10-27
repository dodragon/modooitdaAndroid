package com.baobab.user.baobabflyer.activityLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baobab.user.baobabflyer.BaobabAcceptTerms3;
import com.baobab.user.baobabflyer.BaobabPoke;
import com.baobab.user.baobabflyer.BaobabLogin;
import com.baobab.user.baobabflyer.BaobabCpMenu;
import com.baobab.user.baobabflyer.BaobabCpPayHistory;
import com.baobab.user.baobabflyer.BaobabInspection;
import com.baobab.user.baobabflyer.BaobabNotice;
import com.baobab.user.baobabflyer.BaobabOptionCert;
import com.baobab.user.baobabflyer.BaobabProfileModi;
import com.baobab.user.baobabflyer.BaobabUserTicketList;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.ImageUtil;
import com.baobab.user.baobabflyer.server.util.Scanner;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;

import java.util.UUID;

import ly.count.android.sdk.Countly;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllActivityLoader {

    RetroSingleTon retroSingleTon;

    Context context;
    Context appContext;

    DrawerLayout drawerLayout;
    View drawer;

    public AllActivityLoader(Context context, Context appContext) {
        this.context = context;
        this.appContext = appContext;
    }

    public void drawerSetting() {
        drawerLayout = ((Activity)context).findViewById(R.id.drawerLayout);
        drawer = ((Activity)context).findViewById(R.id.drawer);

        ((Activity)context).findViewById( R.id.openDrawer ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d( this.getClass().getName(), "openDrawer1" );
                drawerLayout.openDrawer( drawer );
            }
        } );


        drawerLayout.setDrawerListener( myDrawerListener );

        drawerLayout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        drawerLayout.findViewById(R.id.option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = context.getSharedPreferences( "user", 0 );
                String email = spf.getString( "email", "" );
                if (email.length() != 0) {
                    Intent intent = new Intent( context, BaobabOptionCert.class );
                    intent.putExtra("kind", "option");
                    context.startActivity( intent );
                    drawerLayout.closeDrawer( Gravity.LEFT );
                } else {
                    Toast.makeText( context, "로그인 후에 이용해주세요.", Toast.LENGTH_SHORT ).show();
                }
            }
        });
        drawerLayout.findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = context.getSharedPreferences( "user", 0 );
                String email = spf.getString( "email", "" );
                if (email.length() != 0) {
                    Intent intent = new Intent(context, BaobabProfileModi.class);
                    context.startActivity(intent);
                    drawerLayout.closeDrawer( Gravity.LEFT );
                } else {
                    Toast.makeText( context, "로그인 후에 이용해주세요.", Toast.LENGTH_SHORT ).show();
                }
            }
        });
        drawerLayout.findViewById(R.id.toggle_ic).bringToFront();
        ToggleButton toggle = ((ToggleButton)drawerLayout.findViewById(R.id.drawer_toggle));
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setText("사장님 전환");
                    drawerLayout.findViewById(R.id.common).setVisibility(View.VISIBLE);
                    drawerLayout.findViewById(R.id.owner).setVisibility(View.GONE);
                }else {
                    buttonView.setText("일반회원 전환");
                    drawerLayout.findViewById(R.id.common).setVisibility(View.GONE);
                    drawerLayout.findViewById(R.id.owner).setVisibility(View.VISIBLE);
                }
            }
        });
        toggle.setChecked(true);
        drawerLayout.findViewById(R.id.scanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Scanner.class);
                context.startActivity(intent);
            }
        });

        drawerLayout.findViewById(R.id.cs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String rn = "0313230858";
                Intent callTo = new Intent( Intent.ACTION_VIEW, Uri.parse( "tel:" + rn ) );
                context.startActivity( callTo );
                drawerLayout.closeDrawer( Gravity.LEFT );
            }
        });
        drawerLayout.findViewById(R.id.notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String div = context.getSharedPreferences( "user", 0 ).getString( "divCode", "" );
                if (div.equals( "" )) {
                    Toast.makeText( context, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT ).show();
                } else {
                    Intent intent = new Intent( context, BaobabNotice.class );
                    context.startActivity( intent );
                    drawerLayout.closeDrawer( Gravity.LEFT );
                }
            }
        });
        drawerLayout.findViewById(R.id.cs_cp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String rn = "0313402858";
                Intent callTo = new Intent( Intent.ACTION_VIEW, Uri.parse( "tel:" + rn ) );
                context.startActivity( callTo );
                drawerLayout.closeDrawer( Gravity.LEFT );
            }
        });
        drawerLayout.findViewById(R.id.notice_cp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String div = context.getSharedPreferences( "user", 0 ).getString( "divCode", "" );
                if (div.equals( "" )) {
                    Toast.makeText( context, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT ).show();
                } else {
                    Intent intent = new Intent( context, BaobabNotice.class );
                    context.startActivity( intent );
                    drawerLayout.closeDrawer( Gravity.LEFT );
                }
            }
        });
        drawerLayout.findViewById(R.id.being_mall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userSpf = context.getSharedPreferences( "user", 0 );
                String divCode = userSpf.getString( "divCode", "" );
                if (divCode.startsWith( "c" )) {
                    Toast.makeText( context, "이미 사장님(직원) 이십니다.", Toast.LENGTH_SHORT ).show();
                } else if (divCode.equals( "u-01-01" )) {
                    Intent userSignup = new Intent( context, BaobabAcceptTerms3.class );
                    context.startActivity( userSignup );
                    drawerLayout.closeDrawer( Gravity.LEFT );
                } else if (divCode.startsWith( "a" )) {
                    Toast.makeText( context, "관리자는 안됩니다. 관리하세요.", Toast.LENGTH_SHORT ).show();
                } else {
                    Toast.makeText( context, "로그인 후에 이용 해주시기 바랍니다.", Toast.LENGTH_SHORT ).show();
                }
            }
        });

        drawerLayout.findViewById(R.id.cp_coupon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = context.getSharedPreferences("user", 0).getString("email", "");
                if(email.equals("")){
                    Toast.makeText(context, "로그인 후 이용 바랍니다.", Toast.LENGTH_SHORT).show();
                }else {
                    if(context.getSharedPreferences("user", 0).getString("divCode", "").startsWith("c")){
                        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).getStaffSeq(email);
                        call.enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                if(response.isSuccessful()){
                                    if(response.body() != null) {
                                        int cpSeq = response.body();
                                        Intent intent = new Intent(context, BaobabCpPayHistory.class);
                                        intent.putExtra("cpSeq", cpSeq);
                                        context.startActivity(intent);
                                        drawerLayout.closeDrawer( Gravity.LEFT );
                                    }else {
                                        Log.d("drawer ticket History", "respons 내용없음");
                                        Toast.makeText(context, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Log.d("drawer ticket History", "서버 로그 확인 필요");
                                    Toast.makeText(context, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable t) {
                                Log.d("drawer ticket History", t.getLocalizedMessage());
                                Toast.makeText(context, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else {
                        Toast.makeText(context, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        drawerLayout.findViewById(R.id.cp_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userSpf = context.getSharedPreferences( "user", 0 );
                if(userSpf.getString("email", "").equals("")){
                    Toast.makeText(context, "로그인 이후 이용해 주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    String divCode = userSpf.getString( "divCode", "" );
                    if (divCode.startsWith("c")) {
                        String email = userSpf.getString( "email", "" );
                        Call<String> call = retroSingleTon.getInstance().getRetroInterface(appContext).getCpUserInfo( email );
                        call.enqueue( new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        Log.d( "통신 완료", "완료" );
                                        String info = response.body();

                                        String cpName = info.split(",")[0];
                                        int cpSeq = Integer.parseInt(info.split(",")[1]);

                                        Intent cpMenu = new Intent( context, BaobabCpMenu.class );
                                        cpMenu.putExtra( "activity", "anterMainActivity" );
                                        cpMenu.putExtra( "getCpNameFromJoin", cpName );
                                        cpMenu.putExtra("cpSeq", cpSeq);
                                        context.startActivity( cpMenu );
                                        drawerLayout.closeDrawer( Gravity.LEFT );
                                        Countly.sharedInstance().endEvent("CpPage(cpMenu)");
                                    } else {
                                        Log.d( "통신 실패", "실패1 : response 내용없음" );
                                        Intent intent = new Intent(context, BaobabInspection.class);
                                        intent.putExtra("status", "오류");
                                        context.startActivity(intent);
                                        ((Activity)context).finish();
                                    }
                                } else {
                                    Log.d( "통신 실패", "실패2 : 서버 에러" );
                                    Intent intent = new Intent(context, BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    context.startActivity(intent);
                                    ((Activity)context).finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d( "통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage() );
                                Intent intent = new Intent(context, BaobabInspection.class);
                                intent.putExtra("status", "오류");
                                context.startActivity(intent);
                                ((Activity)context).finish();
                            }
                        } );
                    } else {
                        Toast.makeText( context, "이용권한이 없습니다.", Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        });
        drawerLayout.findViewById(R.id.user_poke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userSpf = context.getSharedPreferences( "user", 0 );
                if(userSpf.getString("email", "").equals("")){
                    Toast.makeText(context, "로그인 이후 이용해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent favorites = new Intent(context, BaobabPoke.class);
                    context.startActivity(favorites);
                }
            }
        });

        drawerLayout.findViewById(R.id.user_ticket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userSpf = context.getSharedPreferences( "user", 0 );
                if(userSpf.getString("email", "").equals("")){
                    Toast.makeText(context, "로그인 이후 이용해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent favorites = new Intent(context, BaobabUserTicketList.class);
                    context.startActivity(favorites);
                }
            }
        });

        drawerLayout.findViewById(R.id.cp_poke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userSpf = context.getSharedPreferences( "user", 0 );
                if(userSpf.getString("email", "").equals("")){
                    Toast.makeText(context, "로그인 이후 이용해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent favorites = new Intent(context, BaobabPoke.class);
                    context.startActivity(favorites);
                }
            }
        });
    }

    public void profileSetting(){
        SharedPreferences spf = context.getSharedPreferences("user", 0);
        String email = spf.getString("email", "");

        TextView logoutTv = ((Activity)context).findViewById(R.id.logout);
        LinearLayout loginLayout = ((Activity)context).findViewById(R.id.login);
        final ImageView profileImg = ((Activity)context).findViewById(R.id.profileImg);
        Button goToLogin = ((Activity)context).findViewById(R.id.logout_btn);
        RelativeLayout toggleLayout = ((Activity)context).findViewById(R.id.login_btn);

        Log.d("프로필세팅", email);

        if(email.equals("")){
            logoutTv.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
            Glide.with(appContext).load(R.drawable.ic_profile_default).signature(new StringSignature(UUID.randomUUID().toString())).centerCrop().into(profileImg);
            goToLogin.setVisibility(View.VISIBLE);
            toggleLayout.setVisibility(View.GONE);
        }else {
            logoutTv.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            goToLogin.setVisibility(View.GONE);
            toggleLayout.setVisibility(View.VISIBLE);

            TextView divCodeTv = ((Activity)context).findViewById(R.id.div_code);
            String divCode = spf.getString("divCode", "");
            if(divCode.equals("u-01-01")){
                divCodeTv.setText("일반회원");
            }else if(divCode.equals("c-01-01")){
                divCodeTv.setText("사장님");
            }else if(divCode.startsWith("c-02")){
                divCodeTv.setText("직원");
            }else if(divCode.startsWith("a")){
                divCodeTv.setText("관리자");
            }else {
                divCodeTv.setText("");
            }

            ((TextView)((Activity)context).findViewById(R.id.nickName)).setText(nickNameResult(spf.getString("nickName", "")));

            String profileImgUrl = spf.getString("profile", "");

            Glide.clear(profileImg);
            if(profileImgUrl.equals("") || profileImgUrl.equals("이미지없음")){
                Glide.with(appContext).load(R.drawable.ic_profile_default).signature(new StringSignature(UUID.randomUUID().toString())).centerCrop().into(profileImg);
            }else {
                final ImageUtil imageUtil = new ImageUtil();
                Glide.with(appContext).load(profileImgUrl).asBitmap().override(100, 100).signature(new StringSignature(UUID.randomUUID().toString())).centerCrop()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                Bitmap bitmap = imageUtil.getCircularBitmap(resource);
                                profileImg.setImageBitmap(bitmap);
                            }
                        });
            }
        }

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaobabLogin.class);
                context.startActivity(intent);
            }
        });
    }

    public String nickNameResult(String nickName){
        if(nickName.length() > 10){
            return nickName.substring(0, 8) + "..";
        }else {
            return nickName;
        }
    }

    DrawerLayout.DrawerListener myDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {

        }

        @Override
        public void onDrawerOpened(@NonNull View view) {

        }

        @Override
        public void onDrawerClosed(@NonNull View view) {

        }

        @Override
        public void onDrawerStateChanged(int i) {
            String state;
            switch (i) {
                case DrawerLayout.STATE_IDLE:
                    state = "STATE_IDLE";
                    break;
                case DrawerLayout.STATE_DRAGGING:
                    state = "STATE_DRAGGING";
                    break;
                case DrawerLayout.STATE_SETTLING:
                    state = "STATE_SETTLING";
                    break;
                default:
                    state = "unknown!";
            }
        }
    };

    View.OnTouchListener bottomMenuListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int size24 = Math.round(24 * dm.density);

            ImageView imageView = null;
            TextView textView = null;
            int changeImgResource = 0;
            int changeColorResource = 0;

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                switch (v.getId()){
                    case R.id.btn_home:
                        imageView = ((Activity)context).findViewById(R.id.homeImg);
                        textView = ((Activity)context).findViewById(R.id.homeText);
                        changeImgResource = R.drawable.tabicon_ic_home_active;
                        changeColorResource = Color.rgb(252, 133, 73);
                        break;
                    case R.id.btn_review:
                        imageView = ((Activity)context).findViewById(R.id.reviewImg);
                        textView = ((Activity)context).findViewById(R.id.reviewText);
                        changeImgResource = R.drawable.tabicon_ic_reviews_active;
                        changeColorResource = Color.rgb(252, 133, 73);
                        break;
                    case R.id.btn_favorites:
                        imageView = ((Activity)context).findViewById(R.id.favImg);
                        textView = ((Activity)context).findViewById(R.id.favText);
                        changeImgResource = R.drawable.tabicon_ic_favorites_active;
                        changeColorResource = Color.rgb(252, 133, 73);
                        break;
                    case R.id.btn_callhis:
                        imageView = ((Activity)context).findViewById(R.id.callImg);
                        textView = ((Activity)context).findViewById(R.id.callText);
                        changeImgResource = R.drawable.tabicon_ic_called_active;
                        changeColorResource = Color.rgb(252, 133, 73);
                        break;
                }
                glideDrawableResource(context, changeImgResource, size24, size24).into(imageView);
                textView.setTextColor(changeColorResource);
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                switch (v.getId()){
                    case R.id.btn_home:
                        imageView = ((Activity)context).findViewById(R.id.homeImg);
                        textView = ((Activity)context).findViewById(R.id.homeText);
                        changeImgResource = R.drawable.tabicon_ic_home;
                        changeColorResource = Color.rgb(51, 51, 51);
                        break;
                    case R.id.btn_review:
                        imageView = ((Activity)context).findViewById(R.id.reviewImg);
                        textView = ((Activity)context).findViewById(R.id.reviewText);
                        changeImgResource = R.drawable.tabicon_ic_reviews;
                        changeColorResource = Color.rgb(51, 51, 51);
                        break;
                    case R.id.btn_favorites:
                        imageView = ((Activity)context).findViewById(R.id.favImg);
                        textView = ((Activity)context).findViewById(R.id.favText);
                        changeImgResource = R.drawable.tabicon_ic_favorites;
                        changeColorResource = Color.rgb(51, 51, 51);
                        break;
                    case R.id.btn_callhis:
                        imageView = ((Activity)context).findViewById(R.id.callImg);
                        textView = ((Activity)context).findViewById(R.id.callText);
                        changeImgResource = R.drawable.tabicon_ic_called;
                        changeColorResource = Color.rgb(51, 51, 51);
                        break;
                }
                glideDrawableResource(context, changeImgResource, size24, size24).into(imageView);
                textView.setTextColor(changeColorResource);
            }
            return false;
        }
    };

    public DrawableRequestBuilder<Integer> glideDrawableResource(Context context, int resource, int width, int height){
        return  Glide.with(context).load(resource).override(width, height).signature(new StringSignature(UUID.randomUUID().toString())).centerCrop();
    }
}
