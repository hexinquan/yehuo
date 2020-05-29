package com.guoguo.chat.service;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.User;
import com.guoguo.chat.req.RchargeReq;

public interface UserService {
    User findUserById(int userId);
    RestResult recharge(RchargeReq rechargeReq);
    RestResult findUserByUid(String uId);
    User findUserByMobile(String mobile);
    User findUserByName(String name);
}
