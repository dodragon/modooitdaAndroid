package com.baobab.user.baobabflyer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.activityLoader.AllActivityLoader;
import com.baobab.user.baobabflyer.activityLoader.AnterMainLoader;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.AES256Util;
import com.baobab.user.baobabflyer.server.util.GpsTracker;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.util.MakeCertNumber;
import com.baobab.user.baobabflyer.server.vo.AnterMainNeedVO;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.DeviceId;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabAnterMain extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    RetroSingleTon retroSingleTon;
    private BackPressCloseHandler backPressCloseHandler;

    int refreshChange = 1;
    double longitude;
    double latitude;

    private FirebaseAuth auth;
    private GoogleApiClient gac;

    LoadDialog loading;

    DrawerLayout drawerLayout;

    public static final String COUNTLY_URL = "http://analytics.smartcontentcenter.kr";
    public static final String COUNTLY_APP_KEY = "b67686583c86fe06523787f9c927a5a405592259";

    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE};

    AllActivityLoader allActivityLoader;

    public static Context context;
    SwipeRefreshLayout swipeRefreshLayout;

    AnterMainLoader anterMainLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_anter_main);

        context = this;
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.main_color,
                R.color.main_color,
                R.color.main_color,
                R.color.main_color
        );
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestIdToken( getString( R.string.default_web_client_id ) )
                .requestEmail()
                .build();

        gac = new GoogleApiClient.Builder( this )
                .enableAutoManage( this, this )
                .addApi( Auth.GOOGLE_SIGN_IN_API, gso )
                .build();

        auth = FirebaseAuth.getInstance();

        backPressCloseHandler = new BackPressCloseHandler( this );

        allActivityLoader = new AllActivityLoader(this, getApplicationContext());
        anterMainStart();

        findViewById(R.id.safe_cp).setOnClickListener(categoryClick);
        findViewById(R.id.safe_cafe).setOnClickListener(categoryClick);
        findViewById(R.id.all_btn).setOnClickListener(categoryClick);
    }

    public void anterMainStart(){
        Countly.sharedInstance().init(this, COUNTLY_URL, COUNTLY_APP_KEY, null, DeviceId.Type.OPEN_UDID);
        Countly.sharedInstance().onStart(BaobabAnterMain.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkVerify();
        }else {
            try {
                gpsSetting(this);
            } catch (NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | IllegalBlockSizeException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        searchOption();
    }

    @Nullable
    public static String getHashKey(Context context) {
        final String TAG = "KeyHash";
        String keyHash = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = new String(Base64.encode(md.digest(), 0));
                Log.d(TAG, keyHash);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }

        if (keyHash != null) {
            return keyHash;
        } else {
            return null;
        }
    }

    View.OnClickListener categoryClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            anterMainLoader.goPage(v);
        }
    };

    public AnterMainNeedVO visitant() {
        //기본정보 세팅
        AnterMainNeedVO needVO = new AnterMainNeedVO();
        needVO.setThisVersion(thisVersion());

        //방문자 정보 세팅
        SharedPreferences spf = getSharedPreferences("user", 0);
        String email = spf.getString("email", "");

        if(email.equals("")){
            spf = getSharedPreferences("visiter", 0);
            String userCode = spf.getString("userCode", "");
            if(userCode.equals("")){
                String newUserCode = "VU" + new MakeCertNumber().numberGen(10, 1) + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                needVO.setUser(newUserCode);

                SharedPreferences.Editor editor = spf.edit();
                editor.putString("userCode", newUserCode);
                editor.apply();
            }else {
                needVO.setUser(userCode);
            }
        }else {
            needVO.setUser(email);
        }

        return needVO;
    }

    private int thisVersion() {
        int device_version = 0;
        try {
            device_version = getPackageManager().getPackageInfo( getPackageName(), 0 ).versionCode;
            return device_version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return device_version;
    }

    private void searchOption(){
        EditText searchBar = findViewById(R.id.myEditText);

        searchBar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    anterMainLoader.goPage(v);
                }
                return false;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );
        context = this;
        anterMainStart();
    }

    @Override
    public void onBackPressed() {
        drawerLayout = findViewById(R.id.drawerLayout);
        View drawer = findViewById(R.id.drawer);
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
        } else {
            backPressCloseHandler.onBackPressed();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( this, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CAMERA,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_PHONE_STATE},
                    1 );
        } else {
            try {
                gpsSetting(this);
            } catch (NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | IllegalBlockSizeException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }

    public void gpsSetting(Context context) throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        AnterMainNeedVO needVO = visitant();

        if(!checkLocationServiceStatus(context)){
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission(context);
        }

        Intent getLocationIntent = getIntent();
        int needChange;
        needChange = getLocationIntent.getIntExtra("needChange", 0);

        SharedPreferences spf = getSharedPreferences( "gps", MODE_PRIVATE );
        SharedPreferences.Editor editor = spf.edit();


        if(needChange == 0 | refreshChange == 0){
            gpsTracker = new GpsTracker(BaobabAnterMain.this);
            if(gpsTracker.getLocation() != null){
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();

                editor.putString( "longitude", String.valueOf(longitude) );
                editor.putString( "latitude", String.valueOf(latitude) );
            }else {
                onResume();
            }
        }else {
            latitude = Double.parseDouble(spf.getString("latitude", "0"));
            longitude = Double.parseDouble(spf.getString("longitude", "0"));
        }

        com.baobab.user.baobabflyer.server.util.Geocoder geocoder = new com.baobab.user.baobabflyer.server.util.Geocoder(this);
        String address = geocoder.getCurrentAddress(latitude, longitude).replaceAll("대한민국 ", "");

        LinearLayout btn = findViewById( R.id.btn_gps );
        TextView addressText = findViewById(R.id.addressText);
        addressText.setText(address);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabAnterMain.this, BaobabAddressSearch2.class);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                startActivity(intent);
            }
        });


        editor.putString( "addr", address);
        editor.apply();

        needVO.setAddr(address);
        needVO.setLatitude(latitude);
        needVO.setLongitude(longitude);

        allActivityLoader.drawerSetting();
        allActivityLoader.profileSetting();
        anterMainLoader = new AnterMainLoader(getApplicationContext(), this, this, needVO);
        anterMainLoader.activityLoad();

        ((NestedScrollView)findViewById(R.id.scroll)).pageScroll(View.FOCUS_UP);
        ((NestedScrollView)findViewById(R.id.scroll)).fullScroll(ScrollView.FOCUS_UP);

        String user = getSharedPreferences("user", 0).getString("email", "");
        if(user.equals("")){
            user = needVO.getUser();
        }

        SharedPreferences fSpf = getSharedPreferences( "token", 0 );
        AES256Util aes = new AES256Util();
        Call<ResponseBody> call = retroSingleTon.getInstance().getRetroInterface( getApplicationContext() ).userLocUpdate( aes.encrypt( address ), aes.encrypt( String.valueOf( latitude ) ),
                aes.encrypt( String.valueOf( longitude ) ), user, fSpf.getString( "pushToken", "" ) );
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d( "통신완료", "사용자 위치정보 저장" );
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d( "통신실패", "안드로이드 : " + t.getLocalizedMessage() );
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        if(permsRequestCode == PERMISSIONS_REQUEST_CODE && grantResult.length == REQUIRED_PERMISSIONS.length){
            boolean check_result = true;

            for(int result : grantResult){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }

            if(check_result){
                try {
                    gpsSetting(this);
                } catch (NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | IllegalBlockSizeException | InvalidKeyException e) {
                    e.printStackTrace();
                }
            }else {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])){
                    Toast.makeText(BaobabAnterMain.this, "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(BaobabAnterMain.this, "권한이 거부되었습니다. 휴대폰 설정에서 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    void checkRunTimePermission(Context context){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){

        }else {
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, REQUIRED_PERMISSIONS[0])){
                Toast.makeText(context, "위치접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions((Activity) context, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }else {
                ActivityCompat.requestPermissions((Activity) context, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BaobabAnterMain.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해 위치서비스가 필요합니다.\n위치설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GPS_ENABLE_REQUEST_CODE:
                if(checkLocationServiceStatus(this)){
                    Log.d("onActivityResult", "GPS 활성화");
                    checkRunTimePermission(this);
                    return;
                }
                break;
        }
    }

    public boolean checkLocationServiceStatus(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            context = BaobabAnterMain.this;
            refreshChange = 0;
            anterMainStart();
            swipeRefreshLayout.setRefreshing(false);
        }
    };
}