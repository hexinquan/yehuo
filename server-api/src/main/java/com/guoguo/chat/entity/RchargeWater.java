package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_rcharge_water")
@Data
public class RchargeWater {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String uid;
    private String name;
    private String displayName;
    private String mobile;
    private String rchargeName;
    private Integer rchargeMoney;
    private Long rchargeTime;
    private Long createTime;
}
