package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ReviewsVO   implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("revCode")
    private String revCode;
    @SerializedName("userEmail")
    private String userEmail;
    @SerializedName("nickName")
    private String nickName;
    @SerializedName("score")
    private int score;
    @SerializedName("content")
    private String content;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("orderNum")
    private String orderNum;
    @SerializedName("insertDate")
    private Date insertDate;
}
