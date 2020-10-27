package com.baobab.user.baobabflyer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.baobab.user.baobabflyer.server.vo.PageBotListVO;
import com.baobab.user.baobabflyer.server.vo.SafeCpVO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NonCp extends AppCompatActivity implements OnMapReadyCallback {

    RetroSingleTon retroSingleTon;

    private GoogleMap gMap;
    private double longitude;
    private double latitude;
    private String cpName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_cp);

        defaultSetting((PageBotListVO) getIntent().getSerializableExtra("vo"));

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void defaultSetting(PageBotListVO vo){
        cpName = vo.getCpName();

        ((TextView)findViewById(R.id.cpName)).setText(cpName);

        if(vo.getSafeDiv().equals("안심")){
            Call<SafeCpVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).bottomSafeDetail(vo.getCpSeq());
            call.enqueue(new Callback<SafeCpVO>() {
                @Override
                public void onResponse(Call<SafeCpVO> call, Response<SafeCpVO> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            SafeCpVO safeVo = response.body();

                            longitude = safeVo.getLongitude();
                            latitude = safeVo.getLatitude();

                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            mapFragment.getMapAsync(NonCp.this);

                            ((TextView)findViewById(R.id.addr)).setText(safeVo.getAddr() + " " + safeVo.getAddrDetail());
                            ((TextView)findViewById(R.id.tel)).setText(safeVo.getTel());
                        }else {
                            Log.d("nonCp", "response 내용없음");
                        }
                    }else {
                        Log.d("nonCp", "server error");
                    }
                }

                @Override
                public void onFailure(Call<SafeCpVO> call, Throwable t) {
                    Log.d("nonCp", t.getLocalizedMessage());
                }
            });
        }else {
            Call<CPInfoVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).bottomCpDetail(vo.getCpSeq());
            call.enqueue(new Callback<CPInfoVO>() {
                @Override
                public void onResponse(Call<CPInfoVO> call, Response<CPInfoVO> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            CPInfoVO infoVO = response.body();

                            longitude = infoVO.getLongitude();
                            latitude = infoVO.getLatitude();

                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            mapFragment.getMapAsync(NonCp.this);

                            ((TextView)findViewById(R.id.addr)).setText(infoVO.getCP_address() + " " + infoVO.getCP_addr_details());
                            ((TextView)findViewById(R.id.tel)).setText(infoVO.getCP_phon());
                        }else {
                            Log.d("nonCp", "response 내용없음");
                        }
                    }else {
                        Log.d("nonCp", "server error");
                    }
                }

                @Override
                public void onFailure(Call<CPInfoVO> call, Throwable t) {
                    Log.d("nonCp", t.getLocalizedMessage());
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        LatLng cpLatLng = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(cpLatLng);
        markerOptions.title(cpName);

        gMap.addMarker(markerOptions);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cpLatLng, 16));
    }
}
