package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.AccountRecord;
import com.guoguo.chat.entity.Notice;
import com.guoguo.chat.enums.AdvAndNoticeStatusEnums;
import com.guoguo.chat.repository.NoticeRepository;
import com.guoguo.chat.req.base.BasePageReq;
import com.guoguo.chat.vo.PageResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeRepository noticeRepository;

    @PostMapping("/list")
    public RestResult list(@RequestBody BasePageReq basePageReq){
        Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //创建时间降序排序
        Pageable pageable = new PageRequest(basePageReq.getPage()-1,basePageReq.getPageSize(),sort);
        Notice notice = new Notice();
        notice.setNoticeStatus(AdvAndNoticeStatusEnums.ENABLE_ON.getCode());
        Example<Notice> example = Example.of(notice);
        Page<Notice> page = noticeRepository.findAll(example,pageable);
        PageResponseVO responseVO = new PageResponseVO();
        responseVO.setContent(page.getContent());
        responseVO.setPage(basePageReq.getPage());
        responseVO.setPageSize(basePageReq.getPageSize());
        responseVO.setTotalPage(page.getTotalPages());
        responseVO.setTotalSize(page.getTotalElements());
        return RestResult.ok(responseVO);
    }
}
