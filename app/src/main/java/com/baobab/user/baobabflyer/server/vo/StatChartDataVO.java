package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class StatChartDataVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("dt")
    private Date dt;
    @SerializedName("value")
    private int value;
    @SerializedName("date")
    private String date;
    @SerializedName("cpSeq")
    private int cpSeq;
}
