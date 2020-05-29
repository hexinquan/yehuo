package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class RedPacketReq extends BaseReq implements Serializable {
    @NotNull(message = "userId不能为空")
    private String senderId;    //发送者用户Id
    @NotNull(message = "targetId不能为空")
    @NotBlank(message = "targetId不能为空")
    private String targetId;   // 发送到用户还是群
    @NotNull(message = "targetName不能为空")
    private String targetName;
    @NotNull(message = "senderName不能为空")
    private String senderName;  //红包发送者昵称
    private String greetings;  //祝福语
    @NotNull(message = "type不能为空")
    private Integer redType;      //红包类型 1：单聊普通红包  2：群聊普通红包 3.群聊拼手气红包
    @NotNull(message = "count不能为空")
    private Integer count;     //红包个数
    @NotNull(message = "money不能为空")
    private Integer amount;     //红包金额
}
