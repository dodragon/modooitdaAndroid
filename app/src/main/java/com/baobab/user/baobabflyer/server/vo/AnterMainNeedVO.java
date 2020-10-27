package com.baobab.user.baobabflyer.server.vo;

import lombok.Data;

@Data
public class AnterMainNeedVO {
    double longitude;
    double latitude;
    String addr;
    String user;
    int thisVersion;
}
