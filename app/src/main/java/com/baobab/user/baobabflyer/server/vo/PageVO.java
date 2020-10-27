package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PageVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("tobList")
    List<PageTopListVO> tobList;
    @SerializedName("botList")
    List<PageBotListVO> botList;
    @SerializedName("loclog")
    int loclog;
}
