package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class UserLocationVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("user")
    private String user;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("addr")
    private String addr;
    @SerializedName("push_token")
    private String push_token;
    @SerializedName("locationDate")
    private Date locationDate;
}