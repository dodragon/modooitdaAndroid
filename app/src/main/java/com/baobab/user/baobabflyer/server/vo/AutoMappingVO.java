package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class AutoMappingVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("iid")
    private String iid;
    @SerializedName("rn")
    private String rn;
    @SerializedName("memo")
    private String memo;
    @SerializedName("memo2")
    private String memo2;
    @SerializedName("auth")
    private String auth;
    @SerializedName("rt")
    private int rt;
    @SerializedName("rs")
    private String rs;
    @SerializedName("vn")
    private String vn;
}