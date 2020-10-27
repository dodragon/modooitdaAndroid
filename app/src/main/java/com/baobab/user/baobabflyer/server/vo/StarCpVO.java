package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class StarCpVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("cpName")
    private String cpName;
    @SerializedName("mainSerial")
    private String mainSerial;
    @SerializedName("mainImg")
    private String mainImg;
    @SerializedName("starDate")
    private Date starDate;
}
