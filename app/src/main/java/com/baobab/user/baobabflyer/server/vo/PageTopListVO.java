package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class PageTopListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("infoVO")
    private CPInfoVO infoVO;
    @SerializedName("menuVO")
    private EventCpMenuVO menuVO;
}
