package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class EventCpOptionVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("optionName")
    private String optionName;
    @SerializedName("eventSeq")
    private int eventSeq;
    @SerializedName("salesRate")
    private int salesRate;
    @SerializedName("menuList")
    private List<EventCpMenuVO> menuList;
    @SerializedName("eventSerial")
    private String eventSerial;
    @SerializedName("optionSerial")
    private String optionSerial;
}
