package com.guoguo.chat.req.base;

import lombok.Data;

@Data
public class BasePageReq {
    private Integer page=1;
    private Integer pageSize=10;
}
