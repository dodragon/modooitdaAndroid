package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class BaobabGps extends AppCompatActivity
        implements OnMapReadyCallback, LocationListener {


    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;

    LatLng currentPosition = null;
    LatLng previousPosition = null;
    Marker addedMarker = null;

    double lon;
    double lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
        setContentView( R.layout.activity_baobab_gps );

        Log.d( TAG, "onCreate" );

        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addApi( LocationServices.API )
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );


        final Button button = findViewById( R.id.button );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent backMain = new Intent( BaobabGps.this, BaobabAnterMain.class );
                    backMain.putExtra( "needChange", 1);

                    SharedPreferences sf = getSharedPreferences( "gps", MODE_PRIVATE );
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putString( "longitude", String.valueOf(lon));
                    editor.putString( "latitude", String.valueOf(lat));
                    editor.apply();

                    startActivity( backMain );
                    finish();
                } catch (IndexOutOfBoundsException e) {
                    Intent backMain = new Intent( BaobabGps.this, BaobabAnterMain.class );
                    backMain.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    backMain.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity( backMain );
                    finish();
                }
            }
        } );

        Button currentLoc = findViewById( R.id.currentLoc );
        currentLoc.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = getSharedPreferences( "gps", 0 );
                double longitude = Double.parseDouble( spf.getString( "longitude", "126.9783882" ) );
                double latitude = Double.parseDouble( spf.getString( "latitude", "37.5666103" ) );

                GeocodeUtil.GeoLocation geolocation = new GeocodeUtil.GeoLocation( latitude, longitude );
                GeocodeUtil geocodeUtil = new GeocodeUtil( BaobabGps.this );
                String addrStr;
                try {
                    ArrayList<String> arr;
                    arr = geocodeUtil.getAddressListUsingGeolocation( geolocation );
                    addrStr = arr.get( 0 ).substring( 29, arr.get( 0 ).indexOf( "\"]" ) );
                    LatLng latLng = new LatLng( latitude, longitude );
                    MarkerOptions markerOptions = new MarkerOptions();
                    lon = longitude;
                    lat = latitude;
                    markerOptions.position( latLng );
                    markerOptions.draggable( true );
                    markerOptions.title( String.valueOf( addrStr ) );
                    markerOptions.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_RED ) );
                    mGoogleMap.clear();
                    addedMarker = mGoogleMap.addMarker( markerOptions );
                    addedMarker.showInfoWindow();

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( latLng, 15 );
                    mGoogleMap.moveCamera( cameraUpdate );
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText( getApplicationContext(), "어플을 다시 실행해주세요.", Toast.LENGTH_LONG ).show();
                }
            }
        } );

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d( TAG, "onMapReady :" );

        mGoogleMap = googleMap;


        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation(37.5666103, 126.9783882);

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled( true );
        mGoogleMap.animateCamera( CameraUpdateFactory.zoomTo( 15 ) );
        mGoogleMap.setOnMyLocationButtonClickListener( new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d( TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화" );
                mMoveMapByAPI = true;
                return true;
            }
        } );

        mGoogleMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d( TAG, "onMapClick :" );
            }
        } );

        mGoogleMap.setOnCameraMoveStartedListener( new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates) {

                    Log.d( TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화" );
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        } );


        mGoogleMap.setOnCameraMoveListener( new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {


            }
        } );

        mGoogleMap.setOnMapLongClickListener( new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {

                AlertDialog.Builder builder = new AlertDialog.Builder( BaobabGps.this );
                LayoutInflater inflater = getLayoutInflater();

                final AlertDialog dialog = builder.create();

                //맵을 클릭시 현재 위치에 마커 추가
                lat = latLng.latitude;
                lon = latLng.longitude;
                LatLng latLng1 = new LatLng( lat, lon );
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position( latLng );
                markerOptions.draggable( true );
                markerOptions.title( String.valueOf( getCurrentAddress( latLng1 ) ) );
                markerOptions.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_RED ) );
                mGoogleMap.clear();
                Toast.makeText( getApplicationContext(), String.valueOf( getCurrentAddress( latLng1 ) ), Toast.LENGTH_LONG ).show();
                addedMarker = mGoogleMap.addMarker( markerOptions );
                addedMarker.showInfoWindow();
            }
        } );
    }

    @Override
    public void onLocationChanged(Location location) {

        previousPosition = currentPosition;

        currentPosition
                = new LatLng( location.getLatitude(), location.getLongitude() );


        Log.d( TAG, "onLocationChanged : " );

        String markerTitle = getCurrentAddress( currentPosition );
        String markerSnippet = "위도:" + String.valueOf( location.getLatitude() )
                + " 경도:" + String.valueOf( location.getLongitude() );

        if (previousPosition == null) previousPosition = currentPosition;

        //현재 위치에 마커 생성하고 이동
        setCurrentLocation( location, markerTitle, markerSnippet );

        mCurrentLocatiion = location;

    }


    @Override
    protected void onStart() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {

            Log.d( TAG, "onStart: mGoogleApiClient connect" );
            mGoogleApiClient.connect();
        }

        super.onStart();
    }


    public String getCurrentAddress(LatLng latlng) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder( this, Locale.getDefault() );

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1 );
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText( this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG ).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText( this, "잘못된 GPS 좌표", Toast.LENGTH_LONG ).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText( this, "주소 미발견", Toast.LENGTH_LONG ).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get( 0 );
            return address.getAddressLine( 0 ).toString();
        }

    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng( location.getLatitude(), location.getLongitude() );

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( currentLatLng );
        markerOptions.title( String.valueOf( getCurrentAddress( currentLatLng ) ) );
        markerOptions.snippet( markerSnippet );
        markerOptions.draggable( true );

        //구글맵의 디폴트 현재 위치는 파란색 동그라미로 표시
        //마커를 원하는 이미지로 변경하여 현재 위치 표시하도록 수정 fix - 2017. 11.27

        currentMarker = mGoogleMap.addMarker( markerOptions );


        if (mMoveMapByAPI) {
            Log.d( TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude() );
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng( currentLatLng );
            mGoogleMap.moveCamera( cameraUpdate );
        }
    }


    public void setDefaultLocation(double v, double v1) {
        mMoveMapByUser = false;
        Intent getinfo = getIntent();
        final double longitude = getinfo.getDoubleExtra( "longitude", v1);
        final double latitude = getinfo.getDoubleExtra( "latitude", v);

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng( latitude, longitude );

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( DEFAULT_LOCATION );
        markerOptions.draggable( true );
        markerOptions.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_RED ) );
        currentMarker = mGoogleMap.addMarker( markerOptions );

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( DEFAULT_LOCATION, 15 );
        mGoogleMap.moveCamera( cameraUpdate );
    }

}