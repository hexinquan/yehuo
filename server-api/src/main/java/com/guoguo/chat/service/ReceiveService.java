package com.guoguo.chat.service;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.RedReceive;
import com.guoguo.chat.req.ReceiveRedPacketReq;
import com.guoguo.chat.vo.ReceiveListrRespVO;

import java.util.List;

/**
 * @author alex
 */
public interface ReceiveService {
    RedReceive findByRedIdAndUserId(String redId, String uuid);
    RestResult receiveList(ReceiveRedPacketReq receiveRedPacketReq);
}
