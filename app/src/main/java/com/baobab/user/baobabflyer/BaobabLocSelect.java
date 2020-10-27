package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BaobabLocSelect extends FragmentActivity implements OnMapReadyCallback{

    GoogleMap mMap;
    LocationManager locationManager;
    double v;
    double v1;
    double v2;
    double v3;
    String addr;
    String addr2;
    Button nevi;
    Double cpLon;
    Double cpLat;
    String cpAddr;
    String userAddr;
    String cpName;

    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_loc_select );

        geocoder = new Geocoder(this);

        Intent getAddr = getIntent();
        addr = getAddr.getStringExtra( "locName" );
        cpLon = getAddr.getDoubleExtra( "cpLon", 0 );
        cpLat = getAddr.getDoubleExtra( "cpLat", 0 );
        cpAddr = getAddr.getStringExtra( "cpAddr" );
        userAddr = getAddr.getStringExtra( "userAddr" );
        cpName = getAddr.getStringExtra( "cpName" );

        /*Intent getAddr = getIntent();
        addr2 = getAddr.getStringExtra( "userLoc" );
        Log.d( "찍히니?", addr2 );
        String cpLoc = getAddr.getStringExtra( "cpLoc" );*/

        locationManager = (LocationManager)getSystemService( LOCATION_SERVICE );

        if(!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
            intent.addCategory( Intent.CATEGORY_DEFAULT );
            startActivity( intent );
            finish();
        }

        findViewById( R.id.nevi ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.nevi:
                        Intent intent = new Intent( BaobabLocSelect.this, PopupActivity.class );
                        intent.putExtra( "kakaoLongitude", cpLon );
                        intent.putExtra( "kakaoLatitude", cpLat );
                        intent.putExtra( "cpName", cpName );
                        startActivityForResult( intent, 1 );
                        break;
                }
            }
        });

        new Thread(  ){
            public void run() {
                Intent getAddr = getIntent();
                addr2 = getAddr.getStringExtra( "userLoc" );
                Bundle bun = new Bundle();

                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);


            }
        }.start();

        new Handler(  ).postDelayed( new Runnable() {
            @Override
            public void run() {
                SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById( R.id.map );
                mapFragment.getMapAsync( BaobabLocSelect.this );
            }
        },0);


    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            v1 = bun.getDouble("x1");
            v = bun.getDouble("y1");
            v2 = bun.getDouble("x2");
            v3 = bun.getDouble("y2");
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap){
        Intent locInfo = getIntent();
        double longitude = locInfo.getDoubleExtra("longitude", 0);
        double latitude = locInfo.getDoubleExtra("latitude", 0);

        mMap = googleMap;

        LatLng cpLocation = new LatLng(cpLat, cpLon);
        LatLng userLocation = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( cpLocation ).title(cpName );
        markerOptions.snippet(cpAddr);

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position( userLocation ).title("내 위치");
        markerOptions2.snippet(userAddr);

        mMap.addMarker( markerOptions ).showInfoWindow();
        mMap.addMarker( markerOptions2 );

        mMap.moveCamera(CameraUpdateFactory.newLatLng(cpLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cpLocation,17));

        this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //나의 위치 설정
    }
}