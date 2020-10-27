package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class AdImgVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName( "seqNum" )
    private int seqNum;
    @SerializedName( "sm_img" )
    private String sm_img;
    @SerializedName( "big_img" )
    private String big_img;
}
