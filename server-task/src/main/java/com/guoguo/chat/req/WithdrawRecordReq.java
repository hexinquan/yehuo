package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BaseReq;
import lombok.Data;

import java.io.Serializable;

@Data
public class WithdrawRecordReq extends BaseReq implements Serializable {
    private String uid;
    private Integer amount;
    private String mobile;
    private String userName;
    private String platform;
    private String userAccount;
    private String remark;
    private String auditUser;
}
