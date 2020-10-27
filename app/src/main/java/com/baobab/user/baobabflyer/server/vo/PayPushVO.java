package com.baobab.user.baobabflyer.server.vo;

import lombok.Data;

@Data
public class PayPushVO {
    String title;
    String message;
    String customer;
    int cpSeq;
    String div;

    public PayPushVO(String title, String message, String customer, int cpSeq, String div) {
        this.title = title;
        this.message = message;
        this.customer = customer;
        this.cpSeq = cpSeq;
        this.div = div;
    }
}