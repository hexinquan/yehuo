package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RchargeReq extends BaseReq {
    @NotNull(message = "uuid不能为空")
    private String uid;
    @NotNull(message = "name不能为空")
    private String name;
    @NotNull(message = "displayName不能为空")
    private String displayName;
    @NotNull(message = "mobile不能为空")
    private String mobile;
    @NotNull(message = "rchargeName不能为空")
    private String rchargeName;
    private Integer rchargeMoney;
}
