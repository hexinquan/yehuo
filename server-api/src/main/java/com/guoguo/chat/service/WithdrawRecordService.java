package com.guoguo.chat.service;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.WithdrawAuditReq;
import com.guoguo.chat.req.WithdrawListReq;
import com.guoguo.chat.req.WithdrawRecordReq;
import com.guoguo.chat.req.base.BasePageReq;

public interface WithdrawRecordService {
    RestResult add(WithdrawRecordReq withdrawRecordReq);
    RestResult list(WithdrawListReq withdrawListReq);
    RestResult pass(WithdrawAuditReq withdrawAuditReq);
    RestResult rollback(WithdrawAuditReq withdrawAuditReq);
    RestResult findOne(String id);
}
