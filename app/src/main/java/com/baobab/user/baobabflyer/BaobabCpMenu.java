package com.baobab.user.baobabflyer;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.baobab.user.baobabflyer.activityLoader.AllActivityLoader;
import com.baobab.user.baobabflyer.activityLoader.CpMenuLoader;
import com.baobab.user.baobabflyer.server.vo.CpMenuNeedVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

public class BaobabCpMenu extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    RetroSingleTon retroSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_cp_menu );

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestIdToken( getString( R.string.default_web_client_id ) )
                .requestEmail()
                .build();

        new GoogleApiClient.Builder( this )
                .enableAutoManage( this, this )
                .addApi( Auth.GOOGLE_SIGN_IN_API, gso )
                .build();

        FirebaseAuth.getInstance();

        CpMenuNeedVO needVO = new CpMenuNeedVO();
        needVO.setCpName(getIntent().getStringExtra("getCpNameFromJoin"));
        needVO.setCpSeq(getIntent().getIntExtra("cpSeq", 0));
        needVO.setEmail(getSharedPreferences("user", 0).getString("email", ""));

        AllActivityLoader allActivityLoader = new AllActivityLoader(this, getApplicationContext());
        allActivityLoader.drawerSetting();
        allActivityLoader.profileSetting();

        new CpMenuLoader(getApplicationContext(), this, this, needVO);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}