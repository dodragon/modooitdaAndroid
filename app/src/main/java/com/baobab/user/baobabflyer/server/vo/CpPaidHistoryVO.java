package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CpPaidHistoryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("payMenusVOS")
    List<PayMenusVO> payMenusVOS;
    @SerializedName("ticketSerial")
    String ticketSerial;
}
