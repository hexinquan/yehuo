package com.guoguo.chat.service.impl;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.AdvDetails;
import com.guoguo.chat.entity.AdvMain;
import com.guoguo.chat.enums.AdvAndNoticeStatusEnums;
import com.guoguo.chat.repository.AdvDetailsRepository;
import com.guoguo.chat.repository.AdvMainRepository;
import com.guoguo.chat.req.base.BasePageReq;
import com.guoguo.chat.service.AdvMainService;
import com.guoguo.chat.vo.AdvDetailsVO;
import com.guoguo.chat.vo.AdvMainVO;
import com.guoguo.chat.vo.PageResponseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdvMainServiceImpl implements AdvMainService {

    @Autowired
    private AdvDetailsRepository advDetailsRepository;
    @Autowired
    private AdvMainRepository advMainRepository;

    @Override
    public RestResult list(BasePageReq basePageReq) {
        Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //创建时间降序排序
        Pageable pageable = new PageRequest(basePageReq.getPage()-1,basePageReq.getPageSize(),sort);
        AdvMain am = new AdvMain();
        am.setAdvStatus(AdvAndNoticeStatusEnums.ENABLE_ON.getCode());
        Example<AdvMain> example = Example.of(am);
        Page<AdvMain> page = advMainRepository.findAll(example,pageable);
        List<AdvMain> content = page.getContent();
        List<AdvMainVO> advMainResponseVOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(content)){
            content.forEach(advMain -> {
                AdvMainVO advMainVO = new AdvMainVO();
                BeanUtils.copyProperties(advMain,advMainVO);
                AdvDetails advDetails = new AdvDetails();
                advDetails.setAdvId(advMain.getId());
                Example<AdvDetails> of = Example.of(advDetails);
                List<AdvDetailsVO> advDetailsVOS = new ArrayList<>();
                List<AdvDetails> advDetailsList = advDetailsRepository.findAll(of);
                if (!CollectionUtils.isEmpty(advDetailsList)){
                    advDetailsList.forEach(ad -> {
                        AdvDetailsVO advDetailsVO = new AdvDetailsVO();
                        BeanUtils.copyProperties(ad,advDetailsVO);
                        advDetailsVOS.add(advDetailsVO);
                    });
                }
                advMainVO.setAdvDetailsVOS(advDetailsVOS);
                advMainResponseVOS.add(advMainVO);
            });
        }
        PageResponseVO responseVO = new PageResponseVO();
        responseVO.setContent(advMainResponseVOS);
        responseVO.setPage(basePageReq.getPage());
        responseVO.setPageSize(basePageReq.getPageSize());
        responseVO.setTotalPage(page.getTotalPages());
        responseVO.setTotalSize(page.getTotalElements());
        return RestResult.ok(responseVO);
    }
}
