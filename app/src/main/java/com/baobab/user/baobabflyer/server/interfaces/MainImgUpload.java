package com.baobab.user.baobabflyer.server.interfaces;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MainImgUpload {

    @Multipart
    @POST("mainImgUpload.api")
    Call<ResponseBody> setMainImg(@Part MultipartBody.Part mainImg, @Part("cpName") RequestBody cpName, @Part("cpSeq") RequestBody cpSeq);

    @Multipart
    @POST("mainImgUpdate.api")
    Call<Integer> mainImgUpdate(@Part MultipartBody.Part mainImg, @Part("url") RequestBody url);
}
