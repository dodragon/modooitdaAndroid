package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class MenuAdapterListVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("menuName")
    private String menuName;
    @SerializedName("price")
    private int price;
    @SerializedName("option")
    private String option;
    @SerializedName("menuImg")
    private String menuImg;
    @SerializedName("menuDiv")
    private String menuDiv;
}
