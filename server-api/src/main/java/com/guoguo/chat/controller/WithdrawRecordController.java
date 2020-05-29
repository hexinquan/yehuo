package com.guoguo.chat.controller;

import com.alibaba.fastjson.JSON;
import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.WithdrawAuditReq;
import com.guoguo.chat.req.WithdrawListReq;
import com.guoguo.chat.req.WithdrawRecordReq;
import com.guoguo.chat.service.WithdrawRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/withdraw")
public class WithdrawRecordController {

    @Autowired
    private WithdrawRecordService withdrawRecordService;

    @PostMapping("/add")
    public RestResult add(@Valid @RequestBody WithdrawRecordReq withdrawRecordReq){
        log.info("提现请求参数:".concat(JSON.toJSONString(withdrawRecordReq)));
        return withdrawRecordService.add(withdrawRecordReq);
    }
    @PostMapping("/list")
    public RestResult list(@RequestBody WithdrawListReq withdrawListReq){
        log.info("提现记录请求参数:".concat(JSON.toJSONString(withdrawListReq)));
        return withdrawRecordService.list(withdrawListReq);
    }

    @PostMapping("/pass")
    public RestResult pass(@Valid @RequestBody WithdrawAuditReq withdrawAuditReq){
        log.info("提现审核通过请求参数:".concat(JSON.toJSONString(withdrawAuditReq)));
        return withdrawRecordService.pass(withdrawAuditReq);
    }
    @PostMapping("/rollback")
    public RestResult rollback(@Valid @RequestBody WithdrawAuditReq withdrawAuditReq){
        log.info("提现审核驳回请求参数:".concat(JSON.toJSONString(withdrawAuditReq)));
        return withdrawRecordService.rollback(withdrawAuditReq);
    }
    @GetMapping("/edit")
    public RestResult edit(String id){
        log.info("提现详情:".concat(id));
        return withdrawRecordService.findOne(id);
    }
}
