package com.guoguo.chat.enums;

import lombok.Getter;

/**
 * 金额流向
 */
@Getter
public enum AmountFlowTypeEnums {
    ALL(0,"全部"),
    FLOW_IN(1,"流入"),
    FLOW_OUT(2,"流出");
    private Integer code;
    private String name;

    AmountFlowTypeEnums(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
