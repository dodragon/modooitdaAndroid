package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PageNeedVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("user")
    String user;
    @SerializedName("root")
    String root;
    @SerializedName("service")
    String service;
    @SerializedName("thirdPerson")
    String thirdPerson;
    @SerializedName("searchWord")
    String searchWord;
    @SerializedName("categoryDiv")
    String categoryDiv;
    @SerializedName("location")
    String location;
    @SerializedName("longitude")
    double longitude;
    @SerializedName("latitude")
    double latitude;
    @SerializedName("tabDiv")
    String tabDiv;
    @SerializedName("sortDiv")
    String sortDiv;
    @SerializedName("topPageInt")
    int topPageInt;
    @SerializedName("botPageInt")
    int botPageInt;
    @SerializedName("titleList")
    List<MainTitleVO> titleList;
}
