package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.base.BasePageReq;
import com.guoguo.chat.service.ExtensionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/extension")
public class ExtensionController {
    @Autowired
    private ExtensionService extensionService;
    @PostMapping("/list")
    public RestResult list(@RequestBody BasePageReq basePageReq){
        log.info("推广列表接口:"+basePageReq);
        return extensionService.list(basePageReq);
    }
}
