package com.baobab.user.baobabflyer.server.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class PushPayVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    private int seq_num;
    private String menu_name;
    private int ea;
    private int all_ea;
    private int pay;
    private String name;
    private String email;
    private Date buy_date;
    private String cp_name;
    private String acc_num;
    private String tu_num;
    private String bl_num;
    private String tid;
    private String status;
    private Date cancel_date;
    private int cp_seq;
}
