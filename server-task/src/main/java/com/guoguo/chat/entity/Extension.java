package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 推广配置表
 */

@Entity
@Table(name = "t_extension")
@Data
public class Extension {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String platformName;
    private String platformUrl;
    private String registeredAddress;
    private Integer minimumMoney;
    private Integer createId;
    private Long createTime;
    private Long updateTime;
}
