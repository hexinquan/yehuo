package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.RchargeReq;
import com.guoguo.chat.req.UserReq;
import com.guoguo.chat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/rcharge")
    public RestResult recharge(@RequestBody RchargeReq rchargeReq){
        LOG.info("充值:"+rchargeReq);
        return userService.recharge(rchargeReq);
    }

    @PostMapping("/extension-info")
    public RestResult userExtensionInfo(@RequestBody @Valid UserReq userReq){
         return userService.findUserByUid(userReq.getUid());
    }

    @PostMapping("/password")
    public RestResult password(@RequestBody @Valid UserReq userReq){
        return userService.findUserByUid(userReq.getUid());
    }

}
