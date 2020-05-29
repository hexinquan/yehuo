package com.guoguo.chat.enums;

import lombok.Getter;

@Getter
public enum AccountRecordTypeEnums {

    RED_TYPE(1,"红包"),
    RCHARGE_TYPE(2,"充值"),
    WITHDRAW_TYPE(3,"提现")
//    WITHDRAW_FAIL_TYPE(4,"提现失败"),
//    RED_ROLLBACK_TYPE(5,"红包退还")
    ;

    private Integer code;
    private String name;

    AccountRecordTypeEnums(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
