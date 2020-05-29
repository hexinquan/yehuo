package com.guoguo.chat.enums;

import lombok.Getter;

@Getter
public enum ImRedTypeEnums {

    SEND_RED_PACKET(1001,"发红包"),
    RECEIVE_RED_PACKET(1002,"收红包"),;
    private Integer code;
    private String name;

    ImRedTypeEnums(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
