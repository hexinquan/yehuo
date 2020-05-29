package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RedMessageReq extends BaseReq {
    @NotNull(message = "redpacketId不能为空")
    @NotBlank(message = "redpacketId不能为空")
    private String redpacketId;
    @NotNull(message = "userId不能为空")
    @NotBlank(message = "userId不能为空")
    private String userId;
}
