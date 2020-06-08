package com.guoguo.chat.service;

import com.guoguo.chat.entity.Activity;

import java.util.List;

public interface ActivtiyService {
    List<Activity> findAllByStartTimeStatus(int status);
    List<Activity> findAllByEndTimeStatus(int status);
}
