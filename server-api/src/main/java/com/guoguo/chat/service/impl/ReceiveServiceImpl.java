package com.guoguo.chat.service.impl;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.RedReceive;
import com.guoguo.chat.enums.RestCodeEnums;
import com.guoguo.chat.repository.RedReceiveRepository;
import com.guoguo.chat.req.ReceiveRedPacketReq;
import com.guoguo.chat.service.ReceiveService;
import com.guoguo.chat.vo.ReceiveListrRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *  红包发放相关服务
 */
@Slf4j
@Service
@Transactional
public class ReceiveServiceImpl implements ReceiveService {

    @Autowired
    private RedReceiveRepository redReceiveRepository;

    @Override
    public RedReceive findByRedIdAndUserId(String redId, String userId){
        try{
            return redReceiveRepository.findByRedIdAndUserId(redId,userId);
        }catch (Exception e){
            log.error("查询领取记录接口错误",e);
            return null;
        }
    }

    /**
     * 根据用户ID领取记录
     * @param receiveRedPacketReq
     * @return
     */
    @Override
    public RestResult receiveList(ReceiveRedPacketReq receiveRedPacketReq) {
        try{
            Sort sort = new Sort(Sort.Direction.DESC,"receiveTime"); //创建时间降序排序
            Pageable pageable = new PageRequest(receiveRedPacketReq.getPage()-1,receiveRedPacketReq.getPageSize(),sort);
            Page<RedReceive> redReceives = redReceiveRepository.findByReceiveId(receiveRedPacketReq.getUuid(),pageable);
            if(!CollectionUtils.isEmpty(redReceives.getContent())){
                List<ReceiveListrRespVO> receiveListrRespVOS = new ArrayList<>();
                redReceives.forEach(redReceive -> {
                    ReceiveListrRespVO receiveListrRespVO = new ReceiveListrRespVO();
                    BeanUtils.copyProperties(redReceive,receiveListrRespVO);
                    receiveListrRespVOS.add(receiveListrRespVO);
                });
                return RestResult.ok(RestResult.ok(receiveListrRespVOS));
            }
            return RestResult.ok(RestResult.ok(redReceives));
        }catch (Exception e){
            log.error("领取记录接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }

    }



}
