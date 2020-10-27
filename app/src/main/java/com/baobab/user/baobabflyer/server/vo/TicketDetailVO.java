package com.baobab.user.baobabflyer.server.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class TicketDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    List<PayMenusVO> payMenusVOS;
    UserTicketVO userTicketVO;

}
