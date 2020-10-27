package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class PartsVO implements Serializable {

    @SerializedName( "companyPartNm" )
    private String companyPartNm;
}
