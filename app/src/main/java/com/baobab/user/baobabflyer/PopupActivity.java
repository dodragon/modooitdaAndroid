package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;

public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView( R.layout.activity_popup2 );
    }

    public void kakao(View v) {
        Intent intent1 = getIntent();
        String nonCpName = intent1.getStringExtra( "cpName" );
        Double nonCpLon = intent1.getDoubleExtra( "kakaoLongitude",0 );
        Double nonCpLat = intent1.getDoubleExtra( "kakaoLatitude",0 );
        Location destination = Location.newBuilder(nonCpName, nonCpLon, nonCpLat).build();
        NaviOptions options = NaviOptions.newBuilder().setCoordType( CoordType.WGS84 ).setVehicleType( VehicleType.FIRST ).setRpOption( RpOption.SHORTEST ).build();

        KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder( destination ).setNaviOptions( options );
        builder.build();

        KakaoNaviService.getInstance().navigate( PopupActivity.this, builder.build() );

        finish();
    }

    public void tmap(View v) {
        Intent intent1 = getIntent();
        String nonCpName = intent1.getStringExtra( "cpName" );
        Double nonCpLon = intent1.getDoubleExtra( "kakaoLongitude",0 );
        Double nonCpLat = intent1.getDoubleExtra( "kakaoLatitude",0 );
        String url = "https://api2.sktelecom.com/tmap/app/routes?appKey=6c08e28d-6ddd-483b-b1e1-b724439f5b49&name=" +
                nonCpName + "&lon=" + nonCpLon  + "&lat=" + nonCpLat ;
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
        startActivity( intent );
        finish();
    }

    public void cancle(View v){
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
