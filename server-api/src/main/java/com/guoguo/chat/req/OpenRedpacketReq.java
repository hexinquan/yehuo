package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BaseReq;
import lombok.Data;

import java.io.Serializable;
@Data
public class OpenRedpacketReq extends BaseReq implements Serializable {
    private String redpacketId;
    private String userId;
}
