package com.guoguo.chat.service;

import com.guoguo.chat.entity.Activity;

import java.util.List;

public interface ActivtiyService {
    List<Activity> findAllByStatus(int status);
}
