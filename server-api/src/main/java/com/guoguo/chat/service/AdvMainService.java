package com.guoguo.chat.service;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.base.BasePageReq;

public interface AdvMainService {
    RestResult list(BasePageReq basePageReq);
}
