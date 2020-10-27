package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class SafeCpVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("safeNo")
    private int safeNo;
    @SerializedName("sido")
    private String sido;
    @SerializedName("sigungu")
    private String sigungu;
    @SerializedName("cpName")
    private String cpName;
    @SerializedName("addr")
    private String addr;
    @SerializedName("addrDetail")
    private String addrDetail;
    @SerializedName("kind")
    private String kind;
    @SerializedName("type")
    private String type;
    @SerializedName("tel")
    private String tel;
    @SerializedName("safetyDiv")
    private String safetyDiv;
    @SerializedName("postCode")
    private String postCode;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
}
