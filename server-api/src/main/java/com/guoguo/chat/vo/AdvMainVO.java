package com.guoguo.chat.vo;

import com.guoguo.chat.entity.AdvDetails;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AdvMainVO implements Serializable {
    private Integer id;
    private String title;
    private Long startTime;
    private Long endTime;
    private Integer advStatus;
    private Integer displayTime;
    private String createName;
    private Long createTime;
    private Long updateTime;
    private List<AdvDetailsVO> advDetailsVOS;
}
