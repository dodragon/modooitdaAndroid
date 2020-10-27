package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class MenuVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seq_num")
    private int seq_num;
    @SerializedName("menu_name")
    private String menu_name;
    @SerializedName("cp_name")
    private String cp_name;
    @SerializedName("menu_img")
    private String menu_img;
    @SerializedName("menu_price")
    private int menu_price;
    @SerializedName("menu_dis_price")
    private int menu_dis_price;
    @SerializedName("menu_option")
    private String menu_option;
    @SerializedName("menu_intro")
    private String menu_intro;
    @SerializedName("menu_div")
    private String menu_div;
    @SerializedName("menu_detail")
    private String menu_detail;
    @SerializedName("set_menu_name")
    private String set_menu_name;
    //아래는 메뉴 수정시 사용 필드
    @SerializedName("update_menuName")
    private String update_menuName;
    @SerializedName("update_option")
    private String update_option;
    @SerializedName("update_disPrice")
    private int update_disPrice;
    @SerializedName("update_price")
    private int update_price;
}