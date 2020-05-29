package com.guoguo.chat.req.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseReq implements Serializable {
    private Integer time;   //时间戳
    private String  secret; //签名
}
