package com.baobab.user.baobabflyer.server.interfaces;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MenuJoinInterface {

    @Multipart
    @POST("cpMenuJoin.api")
    Call<ResponseBody> setCPMenu(@Part("menuName") RequestBody menuName, @Part("menuDiv") RequestBody menuDev, @Part("cpName") RequestBody cpName, @Part("option") RequestBody option, @Part("price") RequestBody price,
                                 @Part MultipartBody.Part menuImg, @Part("menuIntro") RequestBody menuIntro, @Part("cpSeq") RequestBody cpSeq);
}