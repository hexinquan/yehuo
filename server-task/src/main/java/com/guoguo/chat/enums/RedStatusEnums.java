package com.guoguo.chat.enums;

import lombok.Getter;

@Getter
public enum RedStatusEnums {
    NO_RECEIVE(1,"未领取"),
    RECEIVE(2,"已领取"),
    TIME_OUT(3,"已超时"),
    GIVE_BACK(4,"已退还"),
    RECEIVE_END(5,"已领完");
    private Integer code;
    private String name;

    RedStatusEnums(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
