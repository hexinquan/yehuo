package com.guoguo.chat.service;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.req.OpenRedpacketReq;
import com.guoguo.chat.req.RedMessageReq;
import com.guoguo.chat.req.RedPacketReq;
import com.guoguo.chat.req.SendRedPacketReq;

public interface RedPacketService {
    public RestResult send(final RedPacketReq redPacketReq)throws Exception;
    public RestResult open(final OpenRedpacketReq openRedpacketReq)throws Exception;
    public RestResult detail(String redPacketId);
    public RestResult message(final RedMessageReq redMessageReq);
    public RestResult sendList(SendRedPacketReq sendRedPacketReq);
    public RestResult edit(String redPacketId);
}
