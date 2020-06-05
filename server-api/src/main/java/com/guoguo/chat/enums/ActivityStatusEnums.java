package com.guoguo.chat.enums;

import lombok.Getter;

@Getter
public enum ActivityStatusEnums {
    IS_ENABLE(1,"生效"),
    NO_ENABLE(2,"未生效"),
    ENABLE_FAIL(3,"失效");
    private Integer code;
    private String name;

    ActivityStatusEnums(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
