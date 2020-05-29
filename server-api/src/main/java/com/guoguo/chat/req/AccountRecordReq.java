package com.guoguo.chat.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AccountRecordReq {
    @NotNull(message = "uid不能为空")
    @NotBlank(message = "uid不能为空")
    private String uid;
    @NotNull(message = "flowType不能为空")
    private Integer flowType=0;
    private Integer flow=0;
    private Integer pageSize =10;
    private Integer page=1;
}
