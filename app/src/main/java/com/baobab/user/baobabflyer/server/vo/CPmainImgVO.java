package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class CPmainImgVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seq_num")
    private int seq_num;
    @SerializedName("cp_name")
    private String cp_name;
    @SerializedName("img_url")
    private String img_url;
    @SerializedName("updateFlag")
    private int updateFlag;
    @SerializedName("cp_seq")
    private int cp_seq;
}
