package com.guoguo.chat.service;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.base.BasePageReq;

public interface ExtensionService {
    RestResult list(BasePageReq basePageReq);
}
