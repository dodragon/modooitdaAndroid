package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class EventOptionStatVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("optionName")
    String optionName;
    @SerializedName("optionSaleEa")
    int optionSaleEa;
    @SerializedName("optionSales")
    int optionSales;
    @SerializedName("menuSpecs")
    List<MenuSpecListVO> menuSpecs;
}
