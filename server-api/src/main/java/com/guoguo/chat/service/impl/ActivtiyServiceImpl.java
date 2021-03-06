package com.guoguo.chat.service.impl;

import com.guoguo.chat.entity.Activity;
import com.guoguo.chat.repository.ActivityRepository;
import com.guoguo.chat.service.ActivtiyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivtiyServiceImpl implements ActivtiyService {

    @Autowired
    ActivityRepository activityRepository;

    @Override
    public List<Activity> findAllByStartTimeStatus(int status) {
        return activityRepository.findAllByStartTimeStatus(status);
    }
    @Override
    public List<Activity> findAllByEndTimeStatus(int status) {
        return activityRepository.findAllByEndTimeStatus(status);
    }

}
