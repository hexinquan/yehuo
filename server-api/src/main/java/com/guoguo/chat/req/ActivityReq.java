package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BasePageReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityReq extends BasePageReq {
    private Integer status;
}
