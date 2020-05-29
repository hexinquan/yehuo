package com.guoguo.chat.service;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.AccountRecordReq;


public interface AccountRecordService {
    RestResult findByUid(AccountRecordReq accountRecordReq);
}
