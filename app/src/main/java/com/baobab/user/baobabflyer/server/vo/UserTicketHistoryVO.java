package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class UserTicketHistoryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("ticketTitle")
    private String ticketTitle;
    @SerializedName("email")
    private String email;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("orderNum")
    private String orderNum;
    @SerializedName("ticketSerial")
    private String ticketSerial;
    @SerializedName("amount")
    private int amount;
    @SerializedName("curDate")
    private Date curDate;
    @SerializedName("ticketStatus")
    private String ticketStatus;
    @SerializedName("cpName")
    private String cpName;
}
