package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_account_record")
@Data
public class AccountRecord {
    @Id
    private String id;
    private String uid;
    private Integer amount;
    private Integer recordType;
    private Integer flow;
    private String targetName;
    private Long createTime;
    private Integer balanceAmount;
    private String recordTypeId;
    private Integer auditStatus;
    private Integer redStatus;
    private String auditRemark;
}
