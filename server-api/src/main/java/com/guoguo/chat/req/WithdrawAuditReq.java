package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class WithdrawAuditReq extends BaseReq implements Serializable {
    @NotNull(message = "id不能为空")
    private String id;  //提现记录标识
    @NotNull(message = "auditUser不能为空")
    @NotBlank(message = "auditUser不能为空")
    private String auditUser; //审核用户
    private String auditRemark;
}
