package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_adv_main")
@Data
public class AdvMain {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Long startTime;
    private Long endTime;
    private Integer advStatus;
    private Integer displayTime;
    private String createName;
    private Long createTime;
    private Long updateTime;
}
