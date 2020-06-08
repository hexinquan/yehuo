package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.Version;
import com.guoguo.chat.repository.VersionRepository;
import com.guoguo.chat.req.VersionReq;
import com.guoguo.chat.vo.PageResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        if(versionReq.getAppType()!=null){
            version.setAppType(versionReq.getAppType());
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
    @PostMapping("/list/v2")
    public RestResult listV(@RequestBody VersionReq versionReq){
        Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //创建时间降序排序
        Version version = new Version();
        List<Version> versions = null;
        if(versionReq.getReleaseStatus()!=null){
            version.setReleaseStatus(versionReq.getReleaseStatus());
        }
        if(versionReq.getStatus()!=null){
            version.setStatus(versionReq.getStatus());
        }
        if(versionReq.getAppType()!=null){
            version.setAppType(versionReq.getAppType());
        }
        Example<Version> versionExample = Example.of(version);
        versions = versionRepository.findAll(versionExample);
        if (versionReq.getReleaseStatus()==null && versionReq.getStatus() ==null){
            versions = versionRepository.findAll();
        }
        return RestResult.ok(versions);
    }
}
