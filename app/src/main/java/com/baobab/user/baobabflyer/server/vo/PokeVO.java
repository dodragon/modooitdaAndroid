package com.baobab.user.baobabflyer.server.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class PokeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("seq_num")
    private int seq_num;
    @SerializedName("email")
    private String email;
    @SerializedName("cp_name")
    private String cp_name;
    @SerializedName("cp_address")
    private String cp_address;
    @SerializedName("score")
    private int score;
    @SerializedName("rev_num")
    private int rev_num;
    @SerializedName("div_code")
    private String div_code;
    @SerializedName("cp_seq")
    private int cp_seq;
    @SerializedName("poke_date")
    private Date poke_date;
}