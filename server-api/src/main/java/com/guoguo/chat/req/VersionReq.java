package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BasePageReq;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VersionReq extends BasePageReq {
    private Integer status;
    private Integer releaseStatus;
    private Integer appType;
}
