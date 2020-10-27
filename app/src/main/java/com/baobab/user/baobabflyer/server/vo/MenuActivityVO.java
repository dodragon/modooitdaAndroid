package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MenuActivityVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("menuVOList")
    List<MenuVO> menuVOList;
    @SerializedName("cPmainImgVOList")
    List<CPmainImgVO> cPmainImgVOList;
    @SerializedName("recommendCPVO")
    List<CPInfoVO> recommendCPVO;
    @SerializedName("eventCpVOList")
    List<EventCpVO> eventCpVOList;
    @SerializedName("reviewsListVO")
    List<ReviewsSelectVO> reviewsListVO;
}
