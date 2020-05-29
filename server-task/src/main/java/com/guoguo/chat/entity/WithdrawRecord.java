package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_withdraw_record")
public class WithdrawRecord {
    @Id
    private String id;
    private String uid;
    private Integer amount;
    private String mobile;
    private Integer withdrawStatus;
    private String userName;
    private String platform;
    private String userAccount;
    private Long withdrawTime;
    private String remark;
    private Long auditTime;
    private String auditUser;
}
