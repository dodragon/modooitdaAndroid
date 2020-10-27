package com.baobab.user.baobabflyer.server.singleTons;

import com.baobab.user.baobabflyer.server.interfaces.BizcallInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BizcallSingleTon {

    private final String BASE_URL = "https://api.050bizcall.co.kr/";

    private static BizcallSingleTon Instance = new BizcallSingleTon();

    public static BizcallSingleTon getInstance() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel( HttpLoggingInterceptor.Level.BODY );

        return Instance;
    }

    private BizcallSingleTon() {
    }

    Gson gson = new GsonBuilder().setLenient().create();
    private Retrofit retrofit = new Retrofit.Builder().baseUrl( BASE_URL ).addConverterFactory( GsonConverterFactory.create( gson ) ).build();

    BizcallInterface bizcallInterface;

    public BizcallInterface getBizcallInterface() {
        if (bizcallInterface == null) {
            bizcallInterface = retrofit.create( BizcallInterface.class );
        }
        return bizcallInterface;
    }
}