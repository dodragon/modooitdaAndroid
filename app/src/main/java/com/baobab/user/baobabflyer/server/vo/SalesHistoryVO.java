package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SalesHistoryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("salesPrice")
    private int salesPrice;
    @SerializedName("menuName")
    private String menuName;
    @SerializedName("optionName")
    private String optionName;
    @SerializedName("optionSerial")
    private String optionSerial;
    @SerializedName("eventName")
    private String eventName;
    @SerializedName("eventSerial")
    private String eventSerial;
    @SerializedName("salesStatus")
    private String salesStatus;
    @SerializedName("ea")
    private int ea;
    @SerializedName("orderNum")
    private String orderNum;
    @SerializedName("curDate")
    private Date curDate;
}
