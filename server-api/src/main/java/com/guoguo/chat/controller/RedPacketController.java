package com.guoguo.chat.controller;


import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.enums.RestCodeEnums;
import com.guoguo.chat.req.*;
import com.guoguo.chat.service.ReceiveService;
import com.guoguo.chat.service.RedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 红包相关接口
 * alex
 */

@RestController
@RequestMapping("/redpacket")
public class RedPacketController {

    @Autowired
    private RedPacketService redPacketService;
    @Autowired
    private ReceiveService receiveService;
    private static final Logger LOG = LoggerFactory.getLogger(RedPacketController.class);
    /**
     * 发送红包
     * @param redPacketReq
     * @return
     */
    @PostMapping(value = "send")
    public RestResult send(@Valid @RequestBody RedPacketReq redPacketReq)throws Exception{
        LOG.info("发送红包请求参数:"+redPacketReq);
        return redPacketService.send(redPacketReq);
    }
    /**
     *  领取红包
     * @param openRedpacketReq
     * @return
     */
    @PostMapping(value = "open")
    public RestResult open(@Valid @RequestBody OpenRedpacketReq openRedpacketReq)throws Exception{
        LOG.info("领取红包请求参数:"+openRedpacketReq);
        try {
            return  redPacketService.open(openRedpacketReq);
        }catch (Exception e) {
            LOG.error("failed open redpacket",e);
            return  RestResult.error(RestCodeEnums.ERROR_SERVER);
        }

    }
    /**
     * 红包详情
     */
    @GetMapping(value = "detail")
    public RestResult detail(String redpacketId)throws Exception{
        LOG.info("红包详情请求参数:"+redpacketId);
        return redPacketService.detail(redpacketId);
    }

    /**
     * 红包信息展示
     */
    @PostMapping(value = "message")
    public RestResult message(@Valid @RequestBody RedMessageReq redMessageReq)throws Exception{
        LOG.info("红包信息展示请求参数:"+redMessageReq);
        return redPacketService.message(redMessageReq);
    }

    @GetMapping("/edit")
    public RestResult edit(String id){
        LOG.info("提现详情:".concat(id));
        return redPacketService.edit(id);
    }

    /**
     * 发出的红包列表
     */
    @PostMapping(value = "sendList")
    public RestResult sendList(@RequestBody SendRedPacketReq sendRedPacketReq)throws Exception{
        LOG.info("发出的红包列表请求参数:"+sendRedPacketReq);
        return redPacketService.sendList(sendRedPacketReq);
    }

    /**
     * 收到的红包列表
     */
    @PostMapping(value = "receiveList")
    public RestResult receiveList(@RequestBody ReceiveRedPacketReq receiveRedPacketReq)throws Exception{
        LOG.info("收到的红包列表请求参数:"+receiveRedPacketReq);
        return receiveService.receiveList(receiveRedPacketReq);
    }
}
