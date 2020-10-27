package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class AnterMainVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("mainListVO")
    List<MainListTotalVO> mainListVO;
    @SerializedName("mainTitle")
    List<MainTitleVO> mainTitle;
    @SerializedName("adImgVOList")
    List<AdImgVO> adImgVOList;
    @SerializedName("version")
    int version;
}
