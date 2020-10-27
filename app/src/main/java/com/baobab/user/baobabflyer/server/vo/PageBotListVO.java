package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class PageBotListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("cpSeq")
    int cpSeq;
    @SerializedName("cpName")
    String cpName;
    @SerializedName("distance")
    double distance;
    @SerializedName("safeDiv")
    String safeDiv;
}
