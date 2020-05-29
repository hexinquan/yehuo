package com.guoguo.chat.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdvDetailsVO implements Serializable {
    private Integer id;
    private Integer advId;
    private String imgUrl;
    private String jumpUrl;
    private Integer displayTime;
    private String createName;
    private Long createTime;
    private Long updateTime;
}
