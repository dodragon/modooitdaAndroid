package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class PaymentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("email")
    private String email;
    @SerializedName("userPhone")
    private String userPhone;
    @SerializedName("orderNum")
    private String orderNum;
    @SerializedName("cpName")
    private String cpName;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("goods")
    private String goods;
    @SerializedName("totalPrice")
    private int totalPrice;
    @SerializedName("totalDisPrice")
    private int totalDisPrice;
    @SerializedName("payStatus")
    private String payStatus;
    @SerializedName("tid")
    private String tid;
    @SerializedName("used")
    private String used;
    @SerializedName("payDate")
    private Date payDate;
    @SerializedName("useDate")
    private Date useDate;
    @SerializedName("cancelDate")
    private Date cancelDate;
    @SerializedName("curDate")
    private Date curDate;
    @SerializedName("pg")
    private String pg;
    @SerializedName("revFlag")
    private int revFlag;
}