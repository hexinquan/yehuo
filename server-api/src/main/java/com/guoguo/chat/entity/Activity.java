package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_activity")
@Data
public class Activity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String activityTitle;
    private String activityUrl;
    private Long startTime;
    private Long endTime;
    private String linkUrl;
    private Integer status;
    private String createName;
    private Long createTime;
    private Long updateTime;
}
