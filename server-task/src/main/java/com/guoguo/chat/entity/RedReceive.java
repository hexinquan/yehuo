package com.guoguo.chat.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "t_red_receive")
@Data
public class RedReceive implements Serializable {
    @Id
    private String id; //唯一标识
    private String redId; //红包标识
    private String receiveId; //用户标识
    private String senderId; //发送用户标识
    private String receiveName; //用户名称
    private String senderName; //发送名称
    private Integer receiveAmount=0; //领取金额
    private Long receiveTime; //领取时间
    private Long createTime; //领取时间
    private String reply; //红包回复
}
