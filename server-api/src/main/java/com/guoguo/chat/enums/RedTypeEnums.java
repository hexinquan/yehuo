package com.guoguo.chat.enums;

import lombok.Getter;

@Getter
public enum RedTypeEnums {
        SIGNLE_SIMPLE(1,"单聊普通"),
        SIMPLE_GROUP(2,"群聊普通"),
        LUCK(3,"群聊拼手气");
        private Integer code;
        private String name;

    RedTypeEnums(Integer code, String name){
            this.code = code;
            this.name = name;
        }


}
