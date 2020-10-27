package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CPUserDBVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("email")
    private String email;
    @SerializedName("CP_name")
    private String CP_name;
    @SerializedName("owner_name")
    private String owner_name;
    @SerializedName("CP_license")
    private String CP_license;
    @SerializedName("CP_license_num")
    private String CP_license_num;
    @SerializedName("account_holder")
    private String account_holder;
    @SerializedName("bank")
    private String bank;
    @SerializedName("account_number")
    private String account_number;
    @SerializedName("CP_SGL")
    private String CP_SGL;
    @SerializedName("CP_Ela_Tra_con")
    private String CP_Ela_Tra_con;
    @SerializedName("con_date")
    private Date con_date;
    @SerializedName("CP_con")
    private String CP_con;
    @SerializedName("CP_SS_NUM")
    private String CP_SS_NUM;
    @SerializedName("push_ea")
    private int push_ea;
    @SerializedName("cpPw")
    private String cpPw;
}
