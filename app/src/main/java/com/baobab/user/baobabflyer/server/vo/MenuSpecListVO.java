package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class MenuSpecListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("menuName")
    String menuName;
    @SerializedName("paidEa")
    int paidEa;
    @SerializedName("scanEa")
    int scanEa;
    @SerializedName("cancelEa")
    int cancelEa;
    @SerializedName("allPaid")
    int allPaid;
}
