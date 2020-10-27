package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class UserAllVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @SerializedName("email")
    private String email;
    @SerializedName("user_password")
    private String user_password;
    @SerializedName("phon_num")
    private String phon_num;
    @SerializedName("div_code")
    private String div_code;
    @SerializedName("push_agree")
    private String push_agree;
    @SerializedName("email_agree")
    private String email_agree;
    @SerializedName("sms_agree")
    private String sms_agree;
    @SerializedName("point")
    private int point;
    @SerializedName("nickName")
    private String nickName;
    @SerializedName("profileImg")
    private String profileImg;
    @SerializedName("joinDate")
    private Date joinDate;
    @SerializedName("userName")
    private String userName;
    @SerializedName("phone_corp")
    private String phone_corp;
    @SerializedName("birth")
    private String birth;
    @SerializedName("gender")
    private int gender;
    @SerializedName("nation")
    private int nation;
}