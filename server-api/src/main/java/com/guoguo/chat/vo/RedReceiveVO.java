package com.guoguo.chat.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedReceiveVO implements Serializable {
    private Integer receiveAmount;
    private String receiveName;
    private String senderName;
    private Long receiveTime;
    private String greetings;  //祝福语
    private String senderPic; //发送方的头像
    private String receivePic;
    private String receiveId;
    private Integer isBestLuck=0;
}
