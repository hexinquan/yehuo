package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_version")
@Data
public class Version {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String versionCode;
    private String minVersionCode;
    private Integer appType;
    private String updateUrl;
    private Integer status;
    private Integer releaseStatus;
    private String content;
    private String createName;
    private Long createTime;
    private Long updateTime;
}
