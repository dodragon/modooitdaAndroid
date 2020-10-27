package com.baobab.user.baobabflyer.server.interfaces;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CPJoinInterface {

    @Multipart
    @POST("cpUserJoin.api")
    Call<ResponseBody> cpJoin(@Part("email") RequestBody userMail, @Part("password") RequestBody userPw, @Part("phonNum") RequestBody userPhone, @Part("cpName") RequestBody cpNameStr, @Part("cpLicenseNum") RequestBody licenseNum,
                              @Part("cpPhon") RequestBody cpPhoneStr, @Part("cpZipcode") RequestBody cpZipcodeStr, @Part("cpAddress") RequestBody cpAddressStr,
                              @Part("cpAddrDetails") RequestBody cpAddrDetailsStr, @Part("cpKind") RequestBody kindStr, @Part("cpType") RequestBody typeStr, @Part("cpTheme1") RequestBody theme1, @Part("cpTheme2") RequestBody theme2, @Part("parking") RequestBody parkStr,
                              @Part("closeDay") RequestBody closeDay, @Part("businessStart") RequestBody bStart, @Part("businessEnd") RequestBody bEnd, @Part MultipartBody.Part license, @Part("cpIntro") RequestBody cpIntro,
                              @Part MultipartBody.Part bankbook, @Part("bank")RequestBody bank, @Part("accountHolder")RequestBody accountHolder, @Part("accountNumber")RequestBody accountNumber,
                              @Part("pushCheck")RequestBody pushCheck, @Part("cpDiv")RequestBody cpDiv, @Part("close_ect")RequestBody closeEct);

    @Multipart
    @POST("beingMall.api")
    Call<ResponseBody> beingMall(@Part("email") RequestBody userMail, @Part("cpName") RequestBody cpNameStr, @Part("cpLicenseNum") RequestBody licenseNum,
                                 @Part("cpPhon") RequestBody cpPhoneStr, @Part("cpZipcode") RequestBody cpZipcodeStr, @Part("cpAddress") RequestBody cpAddressStr,
                                 @Part("cpAddrDetails") RequestBody cpAddrDetailsStr, @Part("cpKind") RequestBody kindStr, @Part("cpType") RequestBody typeStr, @Part("cpTheme1") RequestBody theme1, @Part("cpTheme2") RequestBody theme2, @Part("parking") RequestBody parkStr,
                                 @Part("closeDay") RequestBody closeDay, @Part("businessStart") RequestBody bStart, @Part("businessEnd") RequestBody bEnd, @Part MultipartBody.Part license, @Part("cpIntro") RequestBody cpIntro,
                                 @Part("bank")RequestBody bank, @Part("accountHolder")RequestBody accountHolder, @Part("accountNumber")RequestBody accountNumber, @Part("pushCheck")RequestBody pushCheck, @Part("cpDiv")RequestBody cpDiv, @Part("close_ect")RequestBody closeEct);
}