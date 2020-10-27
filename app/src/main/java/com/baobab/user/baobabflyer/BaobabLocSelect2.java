package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class BaobabLocSelect2 extends FragmentActivity implements OnMapReadyCallback{

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



    Geocoder geocoder = new Geocoder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_loc_select );

        Intent getAddr = getIntent();
        addr = getAddr.getStringExtra( "locName" );
        cpLon = getAddr.getDoubleExtra( "nonCpLon", 0 );
        cpLat = getAddr.getDoubleExtra( "nonCpLat", 0 );
        Log.d( "나오오오냥?", String.valueOf( cpLon )+" "+String.valueOf( cpLat ) );
        cpAddr = getAddr.getStringExtra( "nonCpAddr" );
        userAddr = getAddr.getStringExtra( "userAddr" );
        Log.d( "앙 기모띠", cpAddr+" "+cpLon+" "+cpLat );
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
        Button.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.nevi:
                        Intent intent = new Intent( BaobabLocSelect2.this, PopupActivity2.class );
                        intent.putExtra( "tmapLongitude", cpLon );
                        intent.putExtra( "tmapLatitude", cpLat );
                        intent.putExtra( "cpName", cpName );
                        Log.d( "업체명2", cpName );
                        startActivityForResult( intent, 1 );
                        break;
                }
            }
        };
        nevi = (Button)findViewById( R.id.nevi );
        nevi.setOnClickListener( btnListener );

        /*nevi = (Button)findViewById( R.id.nevi );
        nevi.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                String cpName = intent1.getStringExtra( "cpName" );

                *//*String url ="https://api2.sktelecom.com/tmap/app/routes?appKey=6c08e28d-6ddd-483b-b1e1-b724439f5b49&name=" +
                        String.valueOf( cpName ) + "&lon="+cpLon+"&lat="+cpLat;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);*//*
            }
        } );*/

        List<Address> list = null;

        new Thread(  ){
            public void run() {
                Intent getAddr = getIntent();
                addr2 = getAddr.getStringExtra( "userLoc" );
                Bundle bun = new Bundle();
                /*bun.putDouble( "x1", getPointFromNaver(addr).x );
                bun.putDouble( "y1", getPointFromNaver(addr).y );
                bun.putDouble( "x2", getPointFromNaver("경기도 성남시 분당구 수내동 115-15").x );
                bun.putDouble( "y2", getPointFromNaver("경기도 성남시 분당구 수내동 115-15").y );*/

                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();

        new Handler(  ).postDelayed( new Runnable() {
            @Override
            public void run() {
                SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById( R.id.map );
                mapFragment.getMapAsync( BaobabLocSelect2.this );
            }
        }, 0 );


    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            v1 = bun.getDouble("x1");
            v = bun.getDouble("y1");
            v2 = bun.getDouble("x2");
            v3 = bun.getDouble("y2");

            Log.d( "받음", String.valueOf( "x : " + v1 + "  /  y : " + v) );
            Log.d( "받음2", String.valueOf( "x2 : " + v2 + "  /  y2 : " + v3) );
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap){
        Intent locInfo = getIntent();
        double longitude = locInfo.getDoubleExtra("longitude", 0);
        double latitude = locInfo.getDoubleExtra("latitude", 0);
        Log.d( "나오니?3", String.valueOf( longitude ) );
        Log.d( "나오니?4", String.valueOf( latitude ) );
        Log.d( "v들 어케됬니 :: ", String.valueOf( v + "  /  " + v1 ) );
        Log.d( "v들 어케됬니2 :: ", String.valueOf( v2 + "  /  " + v3 ));

        mMap = googleMap;

        LatLng cpLocation = new LatLng(cpLat, cpLon);
        LatLng userLocation = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( cpLocation ).title( cpAddr );

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position( userLocation ).title( userAddr );

        mMap.addMarker( markerOptions );
        mMap.addMarker( markerOptions2 );

        mMap.moveCamera( CameraUpdateFactory.newLatLng( cpLocation ) );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cpLocation,17));

        this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //나의 위치 설정



    }

    class Point {
        // 위도
        public double x;
        // 경도
        public double y;
        public String addr;
        // 포인트를 받았는지 여부
        public boolean havePoint;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("x : ");
            builder.append(x);
            builder.append(" y : ");
            builder.append(y);
            builder.append(" addr : ");
            builder.append(addr);

            return builder.toString();
        }
    }

    /*private Point getPointFromNaver(String addr) {
        Log.d( "넘어왓니?", addr );
        Point point = new Point();
        point.addr = addr;

        String json = null;
        String clientId = "A1NxP6i5lsOgT4NfhkC0";// 애플리케이션 클라이언트 아이디값";
        String clientSecret = "dljkOm4ILT";// 애플리케이션 클라이언트 시크릿값";
        try {
            addr = URLEncoder.encode( addr, "UTF-8" );
            String apiURL = "https://openapi.naver.com/v1/map/geocode?query=" + addr; // json
            URL url = new URL( apiURL );
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod( "GET" );
            con.setRequestProperty( "X-Naver-Client-Id", clientId );
            con.setRequestProperty( "X-Naver-Client-Secret", clientSecret );
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
            } else { // 에러 발생
                br = new BufferedReader( new InputStreamReader( con.getErrorStream() ) );
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append( inputLine );
            }
            br.close();
            json = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json == null) {
            return point;
        }

        Log.d( "TEST2", "json => " + json );

        Gson gson = new Gson();
        NaverData data = new NaverData();
        try {
            data = gson.fromJson( json, NaverData.class );
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data.result != null) {
            point.x = data.result.items.get( 0 ).point.x;
            point.y = data.result.items.get( 0 ).point.y;
            point.havePoint = true;
        }

        return point;

    }*/

}