package com.baobab.user.baobabflyer.server.interfaces;

import com.baobab.user.baobabflyer.server.vo.AnterMainVO;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.baobab.user.baobabflyer.server.vo.CPUserDBVO;
import com.baobab.user.baobabflyer.server.vo.CPUserVO;
import com.baobab.user.baobabflyer.server.vo.CPmainImgVO;
import com.baobab.user.baobabflyer.server.vo.CpMenuVO;
import com.baobab.user.baobabflyer.server.vo.CpPaidHistoryVO;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;
import com.baobab.user.baobabflyer.server.vo.EventOptionStatVO;
import com.baobab.user.baobabflyer.server.vo.MenuActivityVO;
import com.baobab.user.baobabflyer.server.vo.PageVO;
import com.baobab.user.baobabflyer.server.vo.CpStaffVO;
import com.baobab.user.baobabflyer.server.vo.NoticeVO;
import com.baobab.user.baobabflyer.server.vo.PayMenusVO;
import com.baobab.user.baobabflyer.server.vo.PaycancelVO;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.baobab.user.baobabflyer.server.vo.PaypleAccountVO;
import com.baobab.user.baobabflyer.server.vo.PushPayVO;
import com.baobab.user.baobabflyer.server.vo.SafeCpVO;
import com.baobab.user.baobabflyer.server.vo.SalesHistoryVO;
import com.baobab.user.baobabflyer.server.vo.ShowPokeVO;
import com.baobab.user.baobabflyer.server.vo.StatisticsVO;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;
import com.baobab.user.baobabflyer.server.vo.UserLocationVO;
import com.baobab.user.baobabflyer.server.vo.CallHistoryVO;
import com.baobab.user.baobabflyer.server.vo.CertificationVO;
import com.baobab.user.baobabflyer.server.vo.MenuVO;
import com.baobab.user.baobabflyer.server.vo.UserTicketHistoryVO;
import com.baobab.user.baobabflyer.server.vo.UserTicketVO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetroInterface {

    @FormUrlEncoded
    @POST("loginService.api")
    Call<UserAllVO> loginConfirm(@Field("email")String email, @Field("password")String password);

    @GET("getMymenu.api")
    Call<List<MenuVO>> getMymenu(@Query("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("showCallHistory.api")
    Call<List<CallHistoryVO>> showHistory(@Field("userPhone")String userPhone);

    @FormUrlEncoded
    @POST("getPoke.api")
    Call<ResponseBody> getPoke(@Field("email")String email, @Field("cpName")String cpName, @Field("cpAddr")String cpAddr, @Field("score")double score, @Field("revNum")int revNum, @Field("divCode")String divCode,
                               @Field("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("delPoke.api")
    Call<ResponseBody> delPoke(@Field("email")String email, @Field("cpName")String cpName, @Field("divCode")String divCode, @Field("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("showPoke.api")
    Call<List<ShowPokeVO>> showPork(@Field("email")String email, @Field("longitude")double longitude, @Field("latitude")double latitude);

    @GET("getUpdateData.api")
    Call<CPInfoVO> getUpdateData(@Query("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("updateCPInfo.api")
    Call<ResponseBody> updateCPInfo(@Field("cpName") String cpName, @Field("cpPhone")String cpPhone, @Field("zipcode")String zipcode, @Field("addr")String addr, @Field("addrDetail")String addrDetail, @Field("busyStart")
            String busyStart, @Field("busyEnd")String busyEnd, @Field("parking")String parking, @Field("intro")String intro, @Field("closeDay")String closeDay, @Field("theme1")String theme1,
                                    @Field("theme2")String theme2, @Field("kind")String kind, @Field("type")String type, @Field("num")int num, @Field("email")String email, @Field("close_ect")String closeEct);

    @GET("pokeConfirm.api")
    Call<Integer> pokeConfirm(@Query("email")String email, @Query("cpName")String cpName, @Query("divCode")String divCode, @Query("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("version.api")
    Call<Integer> version(@Field( "version" )int version);

    @FormUrlEncoded
    @POST("userLocUpdate.api")
    Call<ResponseBody> userLocUpdate(@Field("addr")String addr, @Field("latitude")String latitude, @Field("longitude")String longitude, @Field("user")String user, @Field("pushToken")String pushToken);

    @FormUrlEncoded
    @POST("selectUserLoc.api")
    Call<UserLocationVO> selectUserLoc(@Field("user")String user);

    @GET("getForPoke.api")
    Call<CPInfoVO> getForPoke(@Query("cpName")String cpName, @Query("addr")String addr);

    @FormUrlEncoded
    @POST("userJoin.api")
    Call<Integer> userJoin(@Field( "userEmail" )String userEmail, @Field( "userPw" )String userPw, @Field( "userPhone" )String userPhone, @Field("pushCheck")String push);

    @GET("transUserJoin.api")
    Call<Integer> duplicateCheck(@Query("email")String userEmail);

    @FormUrlEncoded
    @POST("getCpUserInfo.api")
    Call<String> getCpUserInfo(@Field("email")String email);

    @FormUrlEncoded
    @POST("loginHistory.api")
    Call<ResponseBody> loginHistory(@Field("email")String email, @Field("pw")String pw, @Field("divCode")String divCode, @Field("state")String state);

    @FormUrlEncoded
    @POST("payCpList.api")
    Call<List<PaymentVO>> payCpList(@Field("email")String email, @Field("used")String used);

    @FormUrlEncoded
    @POST("payDetail.api")
    Call<PaymentVO> payDetail(@Field("orderNum")String orderNum);

    @FormUrlEncoded
    @POST("getUserPhone.api")
    Call<String> getUserPhone(@Field("email")String email);

    @GET("receipt.api")
    Call<ResponseBody> receipt(@Query("orderNum")String orderNum);

    @GET("deleteCallHis.api")
    Call<ResponseBody> deleteCallHis(@Query("seqNum")int seqNum);

    @GET("cpNameDup.api")
    Call<String> cpNameDup(@Query("cpName")String cpName);

    @GET("menuDel.api")
    Call<ResponseBody> menuDel(@Query("cpName")String cpName, @Query("menuName")String menuName, @Query("option")String option);

    @Multipart
    @POST("menuModi.api")
    Call<Integer> menuModi(@Part("menuName")RequestBody menuName, @Part("option")RequestBody option, @Part("category")RequestBody category, @Part("price")RequestBody price, @Part("intro")RequestBody intro
            , @Part("imageUrl")RequestBody imageUrl, @Part("menuSeq")RequestBody menuSeq, @Part MultipartBody.Part image, @Part("cpName")RequestBody cpName);

    @FormUrlEncoded
    @POST("cert.api")
    Call<CertificationVO> cert(@Field("cpId")String cpId, @Field("urlCode")String urlCode, @Field("phoneNo")String phoneNo, @Field("authNo")String authNo);

    @FormUrlEncoded
    @POST("userInfoCha.api")
    Call<ResponseBody> userInfoCha(@Field("email")String email, @Field("password")String password, @Field("pushCheck")String pushCheck);

    @FormUrlEncoded
    @POST("leaveUser.api")
    Call<ResponseBody> leaveUser(@Field("email")String email);

    @FormUrlEncoded
    @POST("pushNormal.api")
    Call<List<UserLocationVO>> pushNormal(@Field("cpSeq")int cpSeq, @Field("radius")int radius);

    @FormUrlEncoded
    @POST("pushFan.api")
    Call<List<UserLocationVO>> pushFan(@Field("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("sendPush.api")
    Call<Integer> sendPush(@Field("cpName")String cpName, @Field("radius")int radius, @Field("fanEa")int fanEa, @Field("normalEa")int normalEa, @Field("title")String title, @Field("message")String message,
                           @Field("cpSeq")int cpSeq, @Field("email")String email);

    @FormUrlEncoded
    @POST("findUserLocation.api")
    Call<UserLocationVO> findUserLocation(@Field("pushToken")String pushToken);

    @FormUrlEncoded
    @POST("findCP.api")
    Call<CPInfoVO> findCP(@Field("cpName")String cpName, @Field("title")String title, @Field("message")String message);

    @FormUrlEncoded
    @POST("getNoti.api")
    Call<List<NoticeVO>> getNoti(@Field("readDiv")String readDiv);

    @GET("noticePush.api")
    Call<NoticeVO> noticePush(@Query("title")String title);

    @FormUrlEncoded
    @POST("loclog.api")
    Call<Integer>  loclog(@Field("user")String user, @Field("root")String root, @Field("service")String service, @Field("thirdPerson")String thirdPerson);

    @FormUrlEncoded
    @POST("payPush.api")
    Call<Integer> payPush(@Field("title")String title, @Field("message")String message, @Field("userEmail")String email, @Field("cpSeq")int cpSeq, @Field("div")String div);

    @GET("getMainImg.api")
    Call<List<CPmainImgVO>> getMainImg(@Query("seq")int seq);

    @GET("delMainImg.api")
    Call<Integer> delMainImg(@Query("url")String url);

    @FormUrlEncoded
    @POST("getStatus.api")
    Call<String> getStatus(@Field("status")String status);

    @FormUrlEncoded
    @POST("findEmail.api")
    Call<List<UserAllVO>> findEmail(@Field("phoneNum")String phoneNum);

    @FormUrlEncoded
    @POST("findPassword.api")
    Call<UserAllVO> findPassword(@Field("email")String email, @Field("phone")String phone);

    @FormUrlEncoded
    @POST("updatePassword.api")
    Call<Integer> updatePassword(@Field("email")String email, @Field("phone")String phone, @Field("password")String password);

    @FormUrlEncoded
    @POST("cpUserAllInfo.api")
    Call<CPUserVO> cpUserAllInfo(@Field("email")String email);

    @FormUrlEncoded
    @POST("pointUp.api")
    Call<Integer> pointUp(@Field("email")String email);

    @FormUrlEncoded
    @POST("pushPaySelect.api")
    Call<List<PushPayVO>> pushPaySelect (@Field("email")String email);

    @FormUrlEncoded
    @POST("pushEaPlma.api")
    Call<ResponseBody> pushEaPlma(@Field("email")String email, @Field("pushEa")int ea);

    @FormUrlEncoded
    @POST("cpUserInfo.api")
    Call<CPUserDBVO> cpUserInfo(@Field("email")String email);

    @Multipart
    @POST("changeCpUser.api")
    Call<Integer> changeCpUser(@Part("email")RequestBody email, @Part("licenseNum")RequestBody licenseNum, @Part("accHolder")RequestBody accHolder, @Part("bank")RequestBody bank, @Part("accNum")RequestBody accNum,
                               @Part MultipartBody.Part license);

    @FormUrlEncoded
    @POST("staffSearch.api")
    Call<List<UserAllVO>> staffSearch(@Field("searchWord")String searchWord, @Field("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("cpStaffInfoInsert.api")
    Call<Integer> staffInfoInsert(@Field("email")String email, @Field("staffPhone")String staffPhone, @Field("staffName")String staffName, @Field("staffGender")String staffGender, @Field("divCode")String divCode,
                                    @Field("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("getStaffs.api")
    Call<List<CpStaffVO>> getStaffs(@Field("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("updateStaff.api")
    Call<Integer> updateStaff(@Field("seqNum")int seqNum, @Field("staffName")String staffName, @Field("staffGender")String staffGender, @Field("divCode")String divCode, @Field("email")String email);

    @FormUrlEncoded
    @POST("staffCancel.api")
    Call<Integer> staffDelete(@Field("email")String email, @Field("cpSeq")int cpSeq);

    @Multipart
    @POST("setProfile.api")
    Call<String> setProfile(@Part MultipartBody.Part profileImg, @Part("nickName")RequestBody nickName, @Part("email")RequestBody email);

    @FormUrlEncoded
    @POST("admin/almightySearch.api")
    Call<List<CPInfoVO>> almightySearch(@Field("searchWord")String search);

    @FormUrlEncoded
    @POST("admin/almightyLogin.api")
    Call<Integer> almightyLogin(@Field("id")String id, @Field("pw")String pw);

    @FormUrlEncoded
    @POST("profileDelete.api")
    Call<Integer> profileDelete(@Field("email")String email, @Field("url")String url);

    @FormUrlEncoded
    @POST("anterMain.api")
    Call<AnterMainVO> anterMain(@Field("longitude")double longitude, @Field("latitude")double latitude, @Field("version")int version, @Field("user")String user, @Field("os")String os);

    @FormUrlEncoded
    @POST("page.api")
    Call<PageVO> page(@Field("user") String user, @Field("root") String root, @Field("service") String service, @Field("thirdPerson") String thirdPerson, @Field("searchWord") String searchWord, @Field("categoryDiv") String categoryDiv,
                      @Field("location") String location, @Field("longitude") double longitude, @Field("latitude") double latitude, @Field("tabDiv") String tabDiv, @Field("sortDiv") String sortDiv, @Field("topPageInt")int topPageInt,
                      @Field("botPageInt")int botPageInt);

    @FormUrlEncoded
    @POST("menuAct.api")
    Call<MenuActivityVO> menu(@Field("cpName")String cpName, @Field("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("cpMenu.api")
    Call<CpMenuVO> cpMenu(@Field("cpName")String cpName, @Field("cpSeq")int cpSeq, @Field("email")String email);

    @FormUrlEncoded
    @POST("recommendCP.api")
    Call<List<CPInfoVO>> recommendCP(@Field("cpSeq")int cpSeq);

    @GET("cpAllEvent.api")
    Call<List<EventCpVO>> cpAllEvent(@Query("cpSeq")int cpSeq);

    @GET("cpEvent.api")
    Call<List<EventCpVO>> cpEvent(@Query("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("makeEvent.api")
    Call<Integer> makeEvent(@Field("cpSeq")int cpSeq, @Field("data")String data);

    @FormUrlEncoded
    @POST("updateEvent.api")
    Call<Integer> updateEvent(@Field("data")String data, @Field("isMain")boolean isMain);

    @FormUrlEncoded
    @POST("deleteEvent.api")
    Call<Integer> deleteEvent(@Field("data")String data);

    @FormUrlEncoded
    @POST("turnUpdateEvent.api")
    Call<Integer> turnUpdateEvent(@Field("data")String data);

    @GET("optionNameSelect.api")
    Call<String> optionNameSelect(@Query("serial")String serial);

    @FormUrlEncoded
    @POST("eventPayInsert.api")
    Call<Integer> eventPayInsert(@Field("payment")String payment, @Field("event")String event);

    @FormUrlEncoded
    @POST("getUserTicket.api")
    Call<List<UserTicketVO>> getUserTicket(@Field("email")String email);

    @FormUrlEncoded
    @POST("getPaidMenus.api")
    Call<List<PayMenusVO>> getPaidMenus(@Field("orderNum")String orderNum);

    @FormUrlEncoded
    @POST("sendMessage.api")
    Call<Integer> sendMessage(@Field("title")String title, @Field("message")String message, @Field("div")String div, @Field("json")String json);

    @FormUrlEncoded
    @POST("usedTicket.api")
    Call<Integer> usedTicket(@Field("serial")String serial, @Field("scanner")String scanner, @Field("email")String email, @Field("cpSeq") int cpSeq);

    @FormUrlEncoded
    @POST("useTicketHistory.api")
    Call<List<UserTicketHistoryVO>> useTicketHistory(@Field("email")String email);

    @FormUrlEncoded
    @POST("useTicketHistoryDetail.api")
    Call<List<PayMenusVO>> useTicketHistoryDetail(@Field("orderNum")String orderNum);

    @FormUrlEncoded
    @POST("hitsUp.api")
    Call<Integer> hitsUp(@Field("vo")String vo);

    @FormUrlEncoded
    @POST("getPayment.api")
    Call<List<PaymentVO>> getPayment(@Field("cpSeq")int cpSeq, @Field("date")String date);

    @FormUrlEncoded
    @POST("paidHistory.api")
    Call<CpPaidHistoryVO> paidHistory(@Field("orderNum")String orderNum);

    @GET("setMainEvent.api")
    Call<Integer> setMainEvent(@Query("cpSeq")int cpSeq, @Query("menuSeq")int menuSeq);

    @GET("getMainEvent.api")
    Call<Integer> getMainEvent(@Query("cpSeq")int cpSeq);

    @GET("cpStat.api")
    Call<StatisticsVO> cpStat(@Query("cpSeq")int cpSeq, @Query("curDate")String curDate, @Query("statDiv")String statDiv);

    @GET("eventStatList.api")
    Call<List<SalesHistoryVO>> eventStatList(@Query("cpSeq") int cpSeq, @Query("start")String start, @Query("end")String end);

    @FormUrlEncoded
    @POST("eventDetailStat.api")
    Call<List<EventOptionStatVO>> eventDetailStat(@Field("eventSerial")String eventSerial, @Field("start")String start, @Field("end")String end);

    @FormUrlEncoded
    @POST("payCancelCheck.api")
    Call<String> payCancelCheck(@Field("oid")String oid);

    @FormUrlEncoded
    @POST("cpPwCheck.api")
    Call<Integer> cpPwCheck(@Field("pw")String pw, @Field("cpSeq")int cpSeq);

    @FormUrlEncoded
    @POST("changeCpPw.api")
    Call<Integer> changeCpPw(@Field("pw")String shaPw, @Field("email")String email);

    @GET("getStaffSeq.api")
    Call<Integer> getStaffSeq(@Query("email")String email);

    @GET("getAllMenu.api")
    Call<List<MenuVO>> getAllMenu(@Query("cpSeq")int cpSeq, @Query("menuName")String menuName);

    @FormUrlEncoded
    @POST("pushPayment.api")
    Call<Integer> pushPayment(@Field("data")String data);

    @FormUrlEncoded
    @POST("payCancel.api")
    Call<PaycancelVO> payCancel(@Field("orderNum")String oid);

    @FormUrlEncoded
    @POST("accountCheck.api")
    Call<List<PaypleAccountVO>> accountCheck(@Field("email")String email);

    @FormUrlEncoded
    @POST("accountDelete.api")
    Call<String> accountDelete(@Field("payerId")String payerId);

    @GET("accountInsert.api")
    Call<Integer> accountInsert(@Query("email")String email, @Query("payerId")String payerId);

    @GET("eventOnOff.api")
    Call<String> eventOnOff(@Query("eventSerial")String eventSerial, @Query("onOff")String onOff, @Query("mainSeq")int mainSeq, @Query("cpSeq")int cpSeq);

    @GET("cpOnOff.api")
    Call<Integer> cpOnOff(@Query("ownerEmail")String ownerEmail, @Query("cpName")String cpName, @Query("status")String status);

    @GET("getCpStatus.api")
    Call<String> getCpStatus(@Query("ownerEmail")String ownerEmail, @Query("cpName")String cpName);

    @FormUrlEncoded
    @POST("revInsert.api")
    Call<Integer> revInsert(@Field("userEmail")String userEmail, @Field("nickName")String nickName, @Field("score")int score, @Field("cpSeq")int cpSeq, @Field("content")String content, @Field("revCode")String revCode, @Field("orderNum")String orderNum);

    @Multipart
    @POST("revImgInsert.api")
    Call<Integer> revImgInsert(@Part MultipartBody.Part revImg, @Part("revCode")RequestBody revCode, @Part("imgName")RequestBody imgName);

    @FormUrlEncoded
    @POST("checkUserData.api")
    Call<UserAllVO> checkUserData(@Field("email")String email);

    @FormUrlEncoded
    @POST("accountCert.api")
    Call<String> accountCert(@Field("bank")String bank, @Field("account")String account);

    @GET("accountBack.api")
    Call<Integer> accountBack(@Query("email")String email);

    @GET("selectAddrGroup.api")
    Call<List<String>> selectAddrGroup();

    @GET("bottomSafeDetail.api")
    Call<SafeCpVO> bottomSafeDetail(@Query("safeNo")int safeNo);

    @GET("bottomCpDetail.api")
    Call<CPInfoVO> bottomCpDetail(@Query("seq")int seq);

    @FormUrlEncoded
    @POST("freeTicket.api")
    Call<Integer> freeTicket(@Field("email")String email, @Field("price")int price, @Field("disprice")int disPrice, @Field("cpName")String cpName, @Field("cpSeq")int cpSeq);
}