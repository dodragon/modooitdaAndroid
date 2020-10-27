package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class HitsVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("userInfo")
    private String userInfo;
    @SerializedName("cpName")
    private String cpName;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("hitDate")
    private Date hitDate;
    @SerializedName("hitDiv")
    private String hitDiv;
}
