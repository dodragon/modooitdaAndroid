package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class AlmightyVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("almightyID")
    private String almightyID;
    @SerializedName("almightyPW")
    private String almightyPW;
}
