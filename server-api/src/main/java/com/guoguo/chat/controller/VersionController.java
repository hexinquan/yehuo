package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.Activity;
import com.guoguo.chat.entity.Version;
import com.guoguo.chat.repository.ActivityRepository;
import com.guoguo.chat.repository.VersionRepository;
import com.guoguo.chat.req.VersionReq;
import com.guoguo.chat.req.base.BasePageReq;
import com.guoguo.chat.vo.PageResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionController {

    @Autowired
    VersionRepository versionRepository;

    @PostMapping("/list")
    public RestResult list(@RequestBody VersionReq versionReq){
        Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //创建时间降序排序
        Pageable pageable = new PageRequest(versionReq.getPage()-1,versionReq.getPageSize(),sort);
        Version version = new Version();
        Page<Version> page = null;
        if(versionReq.getReleaseStatus()!=null){
            version.setReleaseStatus(versionReq.getReleaseStatus());
        }
        if(versionReq.getStatus()!=null){
            version.setStatus(versionReq.getStatus());
        }
        Example<Version> versionExample = Example.of(version);
        page = versionRepository.findAll(versionExample,pageable);
        if (versionReq.getReleaseStatus()==null && versionReq.getStatus() ==null){
            page = versionRepository.findAll(pageable);
        }
        PageResponseVO responseVO = new PageResponseVO();
        responseVO.setContent(page.getContent());
        responseVO.setPage(versionReq.getPage());
        responseVO.setPageSize(versionReq.getPageSize());
        responseVO.setTotalPage(page.getTotalPages());
        responseVO.setTotalSize(page.getTotalElements());
        return RestResult.ok(responseVO);
    }
}
