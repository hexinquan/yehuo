package com.guoguo.chat.req;

import com.guoguo.chat.req.base.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserReq extends BaseReq implements Serializable {
    private Integer id;
    @NotNull
    @NotEmpty
    private String uid;
    private String mobile;
    private String name;
}
