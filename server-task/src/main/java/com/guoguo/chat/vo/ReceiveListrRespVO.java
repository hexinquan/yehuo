package com.guoguo.chat.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class ReceiveListrRespVO implements Serializable {
    private String redId; //红包标识
    private String receiveId; //用户标识
    private String senderId; //发送用户标识
    private String receiveName; //用户名称
    private String senderName; //发送名称
    private Integer receiveAmount; //金额
    private Long receiveTime; //领取时间
    private Long createTime; //领取时间
    private String reply; //红包回复
    private Integer amountFlowType; //资金流向
}
