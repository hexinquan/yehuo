package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_notice")
@Data
public class Notice {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String noticeTitle;
    private String noticeContent;
    private Long startTime;
    private Long endTime;
    private Integer displayMode;
    private String buttonOne;
    private String buttonOneUrl;
    private String buttonTwo;
    private String buttonTwoUrl;
    private Integer noticeStatus;
    private String createName;
    private Long createTime;
    private Long updateTime;
}
