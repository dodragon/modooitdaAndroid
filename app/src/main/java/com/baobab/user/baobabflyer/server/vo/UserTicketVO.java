package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class UserTicketVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("ticketTitle")
    private String ticketTitle;
    @SerializedName("email")
    private String email;
    @SerializedName("orderNum")
    private String orderNum;
    @SerializedName("ticketSerial")
    private String ticketSerial;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("cpName")
    private String cpName;
    @SerializedName("amount")
    private int amount;
    @SerializedName("payDate")
    private Date payDate;
    @SerializedName("periodDate")
    private Date periodDate;
    @SerializedName("imgUrl")
    private String imgUrl;
}
