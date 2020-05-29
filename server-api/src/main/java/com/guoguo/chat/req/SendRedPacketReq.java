package com.guoguo.chat.req;

import lombok.Data;

@Data
public class SendRedPacketReq{
    private Integer pageSize =10;
    private Integer page=1;
    private String uuid;
}
