package com.guoguo.chat.req;

import lombok.Data;

@Data
public class AccountRecordReq {
    private String uid;
    private Integer pageSize =10;
    private Integer page;
}
