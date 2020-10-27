package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class PaycancelVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("resultCode")
    private String resultCode;
    @SerializedName("resultMsg")
    private String resultMsg;
    @SerializedName("cancelDate")
    private String cancelDate;
    @SerializedName("cancelTime")
    private String cancelTime;
}
