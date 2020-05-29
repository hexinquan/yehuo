package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BaseReq;
import lombok.Data;

import java.io.Serializable;

@Data
public class ImServerMessageReq extends BaseReq implements Serializable {
   private String sender;// 发送者uid
   private String target;  //接受者uid如果是群聊红包  则是 群组gid
   private String searchAbleContent;// 红包标题
   private Integer conversationType;//  0 表示单聊  1表示群组
   private Integer type;//  1001 发红包   1002 收红包
   private String pushContent;//   推送的通知内容
   private String content; //消息内容 json字符串
}
