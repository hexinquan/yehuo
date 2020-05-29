package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.AccountRecordReq;
import com.guoguo.chat.service.AccountRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/accountRecord")
public class AccountRecordController {

    private static  final Logger LOG  = LoggerFactory.getLogger(AccountRecordController.class);

    @Autowired
    private AccountRecordService accountRecordService;

    /**
     * 账变记录
     * @param accountRecordReq
     * @return
     */

    @PostMapping("/list")
    public RestResult list(@Valid @RequestBody AccountRecordReq accountRecordReq){
        LOG.info("账变记录:"+accountRecordReq);
        return accountRecordService.findByUid(accountRecordReq);
    }

}
