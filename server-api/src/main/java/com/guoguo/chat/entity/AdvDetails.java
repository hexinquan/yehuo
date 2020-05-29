package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_adv_details")
@Data
public class AdvDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private Integer advId;
    private String imgUrl;
    private String jumpUrl;
    private Integer displayTime;
    private String createName;
    private Long createTime;
    private Long updateTime;
}
