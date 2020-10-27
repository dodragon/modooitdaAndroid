package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class PageSearchVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("searchWord")
    private String searchWord;
    @SerializedName("div")
    private String div;
    @SerializedName("location")
    private String location;
    @SerializedName("kind")
    private String kind;
    @SerializedName("sortBy")
    private String sortBy;
    @SerializedName("theme1_1")
    private String theme1_1;
    @SerializedName("theme1_2")
    private String theme1_2;
    @SerializedName("theme1_3")
    private String theme1_3;
    @SerializedName("theme1_4")
    private String theme1_4;
    @SerializedName("theme1_5")
    private String theme1_5;
    @SerializedName("theme1_6")
    private String theme1_6;
    @SerializedName("theme1_7")
    private String theme1_7;
    @SerializedName("theme1_8")
    private String theme1_8;
    @SerializedName("theme1_9")
    private String theme1_9;
    @SerializedName("theme2_1")
    private String theme2_1;
    @SerializedName("theme2_2")
    private String theme2_2;
    @SerializedName("theme2_3")
    private String theme2_3;
    @SerializedName("theme2_4")
    private String theme2_4;
    @SerializedName("theme2_5")
    private String theme2_5;
    @SerializedName("theme2_6")
    private String theme2_6;
    @SerializedName("theme2_7")
    private String theme2_7;
    @SerializedName("theme2_8")
    private String theme2_8;
    @SerializedName("theme2_9")
    private String theme2_9;
}