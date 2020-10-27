package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class EventCpVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("eventName")
    private String eventName;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("salesRate")
    private int salesRate;
    @SerializedName("optionList")
    private List<EventCpOptionVO> optionList;
    @SerializedName("eventSerial")
    private String eventSerial;
    @SerializedName("eventStatus")
    private String eventStatus;
    @SerializedName("turnNum")
    private int turnNum;
}
