package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.AccountRecord;
import com.guoguo.chat.entity.AdvDetails;
import com.guoguo.chat.repository.AdvDetailsRepository;
import com.guoguo.chat.req.base.BasePageReq;
import com.guoguo.chat.vo.PageResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/advDetails")
public class AdvDetailsController {

    @Autowired
    AdvDetailsRepository advDetailsRepository;

    @PostMapping("/list")
    public RestResult list(@RequestBody BasePageReq basePageReq){
        Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //创建时间降序排序
        Pageable pageable = new PageRequest(basePageReq.getPage()-1,basePageReq.getPageSize(),sort);
        Page<AdvDetails> page = advDetailsRepository.findAll(pageable);
        PageResponseVO responseVO = new PageResponseVO();
        responseVO.setContent(page.getContent());
        responseVO.setPage(basePageReq.getPage());
        responseVO.setPageSize(basePageReq.getPageSize());
        responseVO.setTotalPage(page.getTotalPages());
        responseVO.setTotalSize(page.getTotalElements());
        return RestResult.ok(responseVO);
    }
}
