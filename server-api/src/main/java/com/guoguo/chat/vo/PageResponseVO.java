package com.guoguo.chat.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageResponseVO implements Serializable {
    private Integer page;
    private Integer pageSize;
    private Object content;
    private Long totalSize;
    private Integer totalPage;
}
