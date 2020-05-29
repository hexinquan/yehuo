package com.guoguo.chat.common;

import com.guoguo.chat.enums.RestCodeEnums;
import lombok.Data;

@Data
public class RestResult {
    private int code;
    private String message;
    private Object data;

    public static RestResult ok(Object object) {
        return new RestResult(RestCodeEnums.SUCCESS, object);
    }

    public static RestResult error(RestCodeEnums code) {
        return new RestResult(code, null);
    }

    private RestResult(RestCodeEnums code, Object result) {
        this.code = code.code;
        this.message = code.msg;
        this.data = result;
    }
}
