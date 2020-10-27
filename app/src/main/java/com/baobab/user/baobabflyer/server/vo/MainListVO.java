package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class MainListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("cpName")
    private String cpName;
    @SerializedName("menuSeq")
    private int menuSeq;
    @SerializedName("imgUrl")
    private String imgUrl;
    @SerializedName("listDiv")
    private String listDiv;
    @SerializedName("selectDate")
    private Date selectDate;
    @SerializedName("menuName")
    private String menuName;
}
