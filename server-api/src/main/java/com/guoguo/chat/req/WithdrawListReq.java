package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BasePageReq;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WithdrawListReq extends BasePageReq {
    private Integer withdrawStatus =0; //0全部 审核状态1.审核中2.审核失败3审核通过
    @NotNull(message = "uuid不能为空")
    private String uuid;
}
