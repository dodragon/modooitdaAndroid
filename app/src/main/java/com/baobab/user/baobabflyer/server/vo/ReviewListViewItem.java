package com.baobab.user.baobabflyer.server.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ReviewListViewItem implements Serializable {
    private static final long serialVersionUID = 1L;
    public String profile;
    public String nickName;
    public String cpName;
    public Date postData;
    public int stars;
    public String text;
    public List<String> imgs;
    public int cpSeq;
}
