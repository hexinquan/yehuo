package com.guoguo.chat.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CacheRedpacketVO implements Serializable {
    private Integer amount;
    private Integer count;
    private Integer redType; //1单聊普通红包2群聊普通红包3群聊拼手气红包
    private Integer receiveCount;
    private Integer redStatus; //1未领取2已领取3已超时
    private Long sendTime;
    private String targetId;
    private String targetName;
    private String senderName;
    private String senderId;
    private String greetings;  //祝福语
}
