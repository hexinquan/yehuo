package com.guoguo.chat.controller;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.Activity;
import com.guoguo.chat.repository.ActivityRepository;
import com.guoguo.chat.req.ActivityReq;
import com.guoguo.chat.service.ActivtiyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivtiyService activtiyService;

    @Autowired
    private ActivityRepository activityRepository;

    @PostMapping("/list")
    public RestResult list(@RequestBody ActivityReq activityReq){
        if(activityReq.getStatus()!=null){
            if(activityReq.getStatus().intValue()==0){
                Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //创建时间降序排序
                List<Activity> all = activityRepository.findAll(sort);
                return RestResult.ok(all);
            }
            if(activityReq.getStatus().intValue()==4){
                List<Activity> activitieList = activtiyService.findAllByStatus(1);
                List<Activity> activities = activtiyService.findAllByStatus(3);
                activitieList.addAll(activities);
                return RestResult.ok(activitieList);
            }
            List<Activity> allByStatus = activtiyService.findAllByStatus(activityReq.getStatus());
            return RestResult.ok(allByStatus);
        }
        return RestResult.ok(null);

    }
}
