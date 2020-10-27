package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CpMenuVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("cpInfoVO")
    CPInfoVO cpInfoVO;
    @SerializedName("menuVOList")
    List<MenuVO> menuVOList;
}
