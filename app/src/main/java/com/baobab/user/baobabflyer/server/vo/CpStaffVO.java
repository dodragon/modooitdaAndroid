package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CpStaffVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("email")
    private String email;
    @SerializedName("staffPhone")
    private String staffPhone;
    @SerializedName("staffName")
    private String staffName;
    @SerializedName("staffGender")
    private String staffGender;
    @SerializedName("divCode")
    private String divCode;
    @SerializedName("cpSeq")
    private int cpSeq;
    @SerializedName("registDate")
    private Date registDate;
    @SerializedName("status")
    private String status;
}
