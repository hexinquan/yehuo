package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_envelopes_config")
@Data
public class EnvelopesConfig {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private Integer maxMoney;
    private Integer minMoney;
    private Integer type;
    private String createName;
    private String updateName;
    private Long createTime;
    private Long updateTime;
}
