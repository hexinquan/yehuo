package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_bulletin_board")
@Data
public class BulletinBoard {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String noticeContent;
    private String jumpUrl;
    private Long startTime;
    private Long endTime;
    private Integer noticeStatus;
    private String createName;
    private Long createTime;
    private Long updateTime;
}
