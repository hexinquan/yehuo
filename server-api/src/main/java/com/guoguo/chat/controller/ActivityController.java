package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.Activity;
import com.guoguo.chat.repository.ActivityRepository;
import com.guoguo.chat.req.ActivityReq;
import com.guoguo.chat.vo.PageResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    @PostMapping("/list")
    public RestResult list(@RequestBody ActivityReq activityReq){
        Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //创建时间降序排序
        PageResponseVO responseVO = new PageResponseVO();
        Pageable pageable = new PageRequest(activityReq.getPage()-1,activityReq.getPageSize(),sort);
        Activity activity = new Activity();
        Page<Activity> page = null;
        if(activityReq.getStatus()!=null){
            activity.setStatus(activityReq.getStatus());
            Example<Activity> example = Example.of(activity);
            page = activityRepository.findAll(example,pageable);
        }else {
            page = activityRepository.findAll(pageable);
        }
        responseVO.setContent(page.getContent());
        responseVO.setPage(activityReq.getPage());
        responseVO.setPageSize(activityReq.getPageSize());
        responseVO.setTotalPage(page.getTotalPages());
        responseVO.setTotalSize(page.getTotalElements());
        return RestResult.ok(responseVO);
    }
}
