package com.guoguo.chat.req.base;

import lombok.Data;

@Data
public class BasePageReq {
    private Integer page;
    private Integer pageSize;
}
