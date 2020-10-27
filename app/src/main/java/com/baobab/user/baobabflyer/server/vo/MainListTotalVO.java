package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class MainListTotalVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("mainListVO")
    MainListVO mainListVO;
    @SerializedName("cpInfoVO")
    CPInfoVO cpInfoVO;
    @SerializedName("menuVO")
    EventCpMenuVO menuVO;
    @SerializedName("totalSales")
    int totalSales;
}
