package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class NoticeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seq_num")
    private int seq_num;
    @SerializedName("writer")
    private String writer;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("read_div")
    private String read_div;
    @SerializedName("noti_date")
    private Date noti_date;
}