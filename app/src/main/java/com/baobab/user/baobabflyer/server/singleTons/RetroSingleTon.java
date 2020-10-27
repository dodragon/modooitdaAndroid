package com.baobab.user.baobabflyer.server.singleTons;

import android.content.Context;

import com.baobab.user.baobabflyer.server.interfaces.CPJoinInterface;
import com.baobab.user.baobabflyer.server.interfaces.MainImgUpload;
import com.baobab.user.baobabflyer.server.interfaces.MenuJoinInterface;
import com.baobab.user.baobabflyer.server.interfaces.RetroInterface;
import com.baobab.user.baobabflyer.server.util.SSLUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroSingleTon {

    //테스트서버
    //private final static String BASE_URL = "http://baobabtestserver.cafe24.com:80/jsp/";
    //로컬테스트용
    //private final static String BASE_URL = "http://192.168.35.199:80/baobab/jsp/";
    //실서버용
    private final static String BASE_URL = "https://www.baobab858.com:443/jsp/";

    private static RetroSingleTon Instance = new RetroSingleTon();

    public static RetroSingleTon getInstance() {
        return Instance;
    }

    private RetroSingleTon() {

    }

    static Gson gson = new GsonBuilder().setLenient().create();

    public static Retrofit retrofit(Context context) {
        return new Retrofit.Builder().baseUrl( BASE_URL ).client( new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).
                connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES).writeTimeout(1, TimeUnit.MINUTES).sslSocketFactory( SSLUtil.getPinnedCertSslSocketFactory( context ) )
                .build() ).addConverterFactory( GsonConverterFactory.create( gson ) ).build();
    }

    RetroInterface retroInterface;
    CPJoinInterface cpJoinInterface;
    MenuJoinInterface menuJoinInterface;
    MainImgUpload mainImgUpload;

    public RetroInterface getRetroInterface(Context context) {
        if (retroInterface == null) {
            retroInterface = retrofit( context ).create( RetroInterface.class );
        }
        return retroInterface;
    }

    public CPJoinInterface getCpJoinInterface(Context context) {
        if (cpJoinInterface == null) {
            cpJoinInterface = retrofit( context ).create( CPJoinInterface.class );
        }
        return cpJoinInterface;
    }

    public MenuJoinInterface menuJoinInterface(Context context) {
        if (menuJoinInterface == null) {
            menuJoinInterface = retrofit( context ).create( MenuJoinInterface.class );
        }
        return menuJoinInterface;
    }

    public MainImgUpload mainImgUpload(Context context) {
        if (mainImgUpload == null) {
            mainImgUpload = retrofit( context ).create( MainImgUpload.class );
        }
        return mainImgUpload;
    }
}