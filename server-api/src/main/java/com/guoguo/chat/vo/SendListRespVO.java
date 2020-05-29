package com.guoguo.chat.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class SendListRespVO implements Serializable {
    private String senderId;    //发送者Id
    private String targetId;   // 目标ID
    private String senderName;  //发送者昵称
    private String greetings;  //祝福语
    private Long sendTime;   //发送时间
    private Integer redType;   //红包类型 1：普通红包  2：群聊普通红包 3拼手气
    private Integer count;     //红包个数
    private Integer receiveCount=0;  //已领取个数
    private Integer amount;     //红包金额
    private Integer redBalance;  //红包剩余金额
    private Long outTime;      //超时时间
    private  Integer redStatus=1;   // 红包状态1 ：发出   2：已领完  -1：已退款  3:未领完超时退款
    private String targetName; //领取人名称
}
