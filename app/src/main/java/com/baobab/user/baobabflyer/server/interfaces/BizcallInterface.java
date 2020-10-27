package com.baobab.user.baobabflyer.server.interfaces;

import com.baobab.user.baobabflyer.server.vo.AutoMappingVO;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BizcallInterface {

    @FormUrlEncoded
    @POST("link/auto_mapp.do")
    Call<AutoMappingVO> autoMapping(@Field("iid")String iid, @Field("rn")String rn, @Field("memo")String memo, @Field("auth")String auth);

}