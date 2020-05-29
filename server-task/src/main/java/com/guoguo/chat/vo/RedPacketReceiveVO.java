package com.guoguo.chat.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RedPacketReceiveVO implements Serializable {
    private String senderId;    //发送者Id
    private String targetId;   // 目标ID
    private String senderName;  //发送者昵称
    private String greetings;  //祝福语
    private Long sendTime;   //发送时间
    private Integer count;     //红包个数
    private Integer receiveCount=0;  //已领取个数
    private Integer receiveTotalAmount=0;
    private String senderPic; //fa
    private Integer totalAmount;
    private List<RedReceiveVO> redReceiveVOS; //领取记录
}
