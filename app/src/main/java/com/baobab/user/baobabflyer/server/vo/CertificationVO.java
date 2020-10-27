package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class CertificationVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("certNo")
    private String certNo;
    @SerializedName("reqDate")
    private String reqDate;
    @SerializedName("result")
    private String result;
    @SerializedName("resultCode")
    private String resultCode;
    @SerializedName("phoneNo")
    private String phoneNo;
    @SerializedName("authNo")
    private String authNo;
    @SerializedName("extendVar")
    private String extendVar;
}
