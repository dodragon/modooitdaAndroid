package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CallHistoryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seq_num")
    private int seq_num;
    @SerializedName("user_phone")
    private String user_phone;
    @SerializedName("cp_phone")
    private String cp_phone;
    @SerializedName("cp_name")
    private String cp_name;
    @SerializedName("cp_address")
    private String cp_address;
    @SerializedName("call_date")
    private Date call_date;
    @SerializedName("cp_div")
    private String cp_div;
}