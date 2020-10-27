package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class CPInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("CP_name")
    private String CP_name;
    @SerializedName("owner_email")
    private String owner_email;
    @SerializedName("CP_grade")
    private String CP_grade;
    @SerializedName("CP_level")
    private String CP_level;
    @SerializedName("CP_phon")
    private String CP_phon;
    @SerializedName("CP_address")
    private String CP_address;
    @SerializedName("CP_addr_details")
    private String CP_addr_details;
    @SerializedName("CP_zipcode")
    private String CP_zipcode;
    @SerializedName("CP_location")
    private String CP_location;
    @SerializedName("rev_grade")
    private double rev_grade;
    @SerializedName("Business_start")
    private String Business_start;
    @SerializedName("Business_end")
    private String Business_end;
    @SerializedName("CP_intro")
    private String CP_intro;
    @SerializedName("CP_kind")
    private String CP_kind;
    @SerializedName("CP_type")
    private String CP_type;
    @SerializedName("CP_Theme1")
    private String CP_Theme1;
    @SerializedName("CP_Theme2")
    private String CP_Theme2;
    @SerializedName("parking")
    private String parking;
    @SerializedName("close_day")
    private String close_day;
    @SerializedName("close_ect")
    private String close_ect;
    @SerializedName("CP_lis_num")
    private String CP_lis_num;
    @SerializedName("CP_hits")
    private int CP_hits;
    @SerializedName("cur_hits")
    private int cur_hits;
    @SerializedName("fran_name")
    private String fran_name;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("cp_div")
    private String cp_div;
    @SerializedName("cpStatus")
    private String cpStatus;
    @SerializedName("seq_num")
    private int seq_num;
    @SerializedName("distance")
    private double distance;
    @SerializedName("img_url")
    private String img_url;
    @SerializedName("imgFlag")
    private int imgFlag;
    @SerializedName("mainEvent")
    private int mainEvent;
    @SerializedName("cpPassword")
    private String cpPassword;
    @SerializedName("ecmVO")
    private EventCpMenuVO ecmVO;
    @SerializedName("cpLogo")
    private String cpLogo;
    @SerializedName("cpPercentage")
    private double cpPercentage;
}