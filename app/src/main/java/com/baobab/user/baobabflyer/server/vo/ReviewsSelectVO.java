package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ReviewsSelectVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("nickName")
    private String nickName;
    @SerializedName("score")
    private int score;
    @SerializedName("profileImg")
    private String profileImg;
    @SerializedName("payMenus")
    private String payMenus;
    @SerializedName("content")
    private String content;
    @SerializedName("insertDate")
    private Date insertDate;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("cpName")
    private String cpName;
    @SerializedName("imgUrls")
    private List<String> imgUrls;
    @SerializedName("orderNum")
    private String orderNum;
    @SerializedName("revCode")
    private String revCode;
}
