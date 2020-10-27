package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class MainTitleVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("writer")
    private String writer;
    @SerializedName("mainText")
    private String mainText;
    @SerializedName("subText")
    private String subText;
    @SerializedName("titleDiv")
    private String titleDiv;
    @SerializedName("titleStatus")
    private String titleStatus;
    @SerializedName("writeDate")
    private Date writeDate;
}
