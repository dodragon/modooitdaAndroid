package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class EventCpMenuVO   implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("menuName")
    private String menuName;
    @SerializedName("optionSeq")
    private int optionSeq;
    @SerializedName("price")
    private int price;
    @SerializedName("disPrice")
    private int disPrice;
    @SerializedName("percentAge")
    private int percentAge;
    @SerializedName("salesRate")
    private int salesRate;
    @SerializedName("optionSerial")
    private String optionSerial;
    @SerializedName("menuInfo")
    private String menuInfo;
    private int selectedEa = 0;
}
