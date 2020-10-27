package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class PayMenusVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("orderNum")
    private String orderNum;
    @SerializedName("menuName")
    private String menuName;
    @SerializedName("optionSerial")
    private String optionSerial;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("payEmail")
    private String payEmail;
    @SerializedName("price")
    private int price;
    @SerializedName("disPrice")
    private int disPrice;
    @SerializedName("ea")
    private int ea;
    @SerializedName("payDate")
    private Date payDate;
}
