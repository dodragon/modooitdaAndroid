package com.baobab.user.baobabflyer.server.vo;

import lombok.Data;

@Data
public class EventStatListVO {
    String eventName;
    String eventSerial;
    int paidCount = 0;
    int scanCount = 0;
    int cancelCount = 0;
    int allPaid = 0;
}
