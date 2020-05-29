package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.repository.RchargeWaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rchargeWater")
public class RchargeWaterController {

    @Autowired
    RchargeWaterRepository rchargeWaterRepository;

    @GetMapping("/list")
    public RestResult list(){
        return null;
    }

}
