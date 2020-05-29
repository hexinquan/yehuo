package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.base.BasePageReq;
import com.guoguo.chat.service.AdvMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/advMain")
public class AdvMainController {

    @Autowired
    private AdvMainService advMainService;

    @PostMapping("/list")
    public RestResult list(@RequestBody BasePageReq basePageReq){
        return  advMainService.list(basePageReq);
    }
}
