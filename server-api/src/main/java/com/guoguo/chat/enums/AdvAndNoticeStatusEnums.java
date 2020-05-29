package com.guoguo.chat.enums;

import lombok.Getter;

@Getter
public enum AdvAndNoticeStatusEnums {
    ENABLE_ON(1,"生效中"),
    ENABLE_OFF(2,"未生效"),
    ENABLE_END(3,"已失效")
    ;

    private Integer code;
    private String name;

    AdvAndNoticeStatusEnums(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
