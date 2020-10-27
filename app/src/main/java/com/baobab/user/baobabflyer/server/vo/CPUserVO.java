package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CPUserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("email")
    private String email;
    @SerializedName("cpName")
    private String cpName;
    @SerializedName("ownerName")
    private String ownerName;
    @SerializedName("license")
    private String license;
    @SerializedName("cpLicenseNum")
    private String cpLicenseNum;
    @SerializedName("account_holder")
    private String account_holder;
    @SerializedName("bank")
    private String bank;
    @SerializedName("accountNumber")
    private String accountNumber;
    @SerializedName("cpSGL")
    private String cpSGL;
    @SerializedName("cpEleTraCon")
    private String cpEleTraCon;
    @SerializedName("con_date")
    private Date con_date;
    @SerializedName("cpCon")
    private String cpCon;
    @SerializedName("cpSSNum")
    private String cpSSNum;
    @SerializedName("push_ea")
    private int push_ea;
}
