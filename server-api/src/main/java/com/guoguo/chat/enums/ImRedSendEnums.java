package com.guoguo.chat.enums;

import lombok.Getter;

@Getter
public enum ImRedSendEnums {
    SINGLE_(0,"单聊"),
    GROUP_(1,"群组");
    private Integer code;
    private String name;

    ImRedSendEnums(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
