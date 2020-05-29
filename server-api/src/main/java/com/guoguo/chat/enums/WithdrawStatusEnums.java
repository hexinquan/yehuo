package com.guoguo.chat.enums;

import lombok.Getter;

@Getter
public enum WithdrawStatusEnums {
    AUDIT_ING(1,"审核中"),
    AUDIT_NO_PASS(2,"未通过"),
    AUDIT_PASS(3,"已通过");

    private Integer code;
    private String name;

    WithdrawStatusEnums(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
