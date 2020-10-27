package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ShowPokeVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("cpInfoVO")
    private CPInfoVO cpInfoVO;
    @SerializedName("imgVoList")
    private List<CPmainImgVO> imgVoList;
    @SerializedName("pokeVO")
    private PokeVO pokeVO;
}
