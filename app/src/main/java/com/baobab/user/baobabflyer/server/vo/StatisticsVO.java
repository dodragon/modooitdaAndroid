package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class StatisticsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("aHit")
    private int aHit;
    @SerializedName("bHit")
    private int bHit;
    @SerializedName("aPoke")
    private int aPoke;
    @SerializedName("bPoke")
    private int bPoke;
    @SerializedName("aPay")
    private int aPay;
    @SerializedName("bPay")
    private int bPay;
    @SerializedName("aScan")
    private int aScan;
    @SerializedName("bScan")
    private int bScan;
    @SerializedName("aCancel")
    private int aCancel;
    @SerializedName("bCancel")
    private int bCancel;
    @SerializedName("aPush")
    private int aPush;
    @SerializedName("bPush")
    private int bPush;
    @SerializedName("aPushClick")
    private int aPushClick;
    @SerializedName("bPushClick")
    private int bPushClick;
    @SerializedName("allSales")
    private int allSales;
    @SerializedName("useSales")
    private int useSales;
    @SerializedName("cancelSales")
    private int cancelSales;
    @SerializedName("lineChartHits")
    private List<StatChartDataVO> lineChartHits;
    @SerializedName("lineChartPoke")
    private List<StatChartDataVO> lineChartPoke;
    @SerializedName("lineChartPaySuccess")
    private List<StatChartDataVO> lineChartPaySuccess;
    @SerializedName("lineChartScan")
    private List<StatChartDataVO> lineChartScan;
    @SerializedName("lineChartPayCancel")
    private List<StatChartDataVO> lineChartPayCancel;
    @SerializedName("lineChartPush")
    private List<StatChartDataVO> lineChartPush;
    @SerializedName("lineChartPushClick")
    private List<StatChartDataVO> lineChartPushClick;
}
