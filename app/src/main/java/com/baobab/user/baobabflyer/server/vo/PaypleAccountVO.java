package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class PaypleAccountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("accountHolder")
    private String accountHolder;
    @SerializedName("payerId")
    private String payerId;
    @SerializedName("email")
    private String email;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("bankCode")
    private String bankCode;
    @SerializedName("bankName")
    private String bankName;
    @SerializedName("accountNumber")
    private String accountNumber;
}
