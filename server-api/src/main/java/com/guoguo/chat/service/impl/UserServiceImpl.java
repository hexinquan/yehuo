package com.guoguo.chat.service.impl;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.RchargeWater;
import com.guoguo.chat.entity.User;
import com.guoguo.chat.enums.RestCodeEnums;
import com.guoguo.chat.repository.RchargeWaterRepository;
import com.guoguo.chat.repository.UserRepository;
import com.guoguo.chat.req.RchargeReq;
import com.guoguo.chat.res.UserExtensionRes;
import com.guoguo.chat.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RchargeWaterRepository rchargeWaterRepository;

    @Override
    public User findUserById(int userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.get();
    }

    @Override
    public RestResult recharge(RchargeReq rchargeReq) {
        if(rchargeReq.getRchargeMoney()<=0){
            return RestResult.error(RestCodeEnums.ERROR_RCHARGE_AMOUNT);
        }
        int result = userRepository.incBalanceAmountByUserId(rchargeReq.getRchargeMoney(),rchargeReq.getUid());
        if(result>0){
            RchargeWater rchargeWater =new RchargeWater();
            BeanUtils.copyProperties(rchargeReq,rchargeWater);
            rchargeWater.setCreateTime(System.currentTimeMillis());
            rchargeWater.setRchargeTime(System.currentTimeMillis());
            rchargeWaterRepository.saveAndFlush(rchargeWater);
            return RestResult.ok(result);
        }
        return RestResult.ok(RestCodeEnums.ERROR_SERVER);
    }

    @Override
    public RestResult findUserByUid(String uid) {
        User user = userRepository.findByUid(uid);
        if(null == user){
            return RestResult.error(RestCodeEnums.ERROR_USER_NOT_FOUND);
        }
        UserExtensionRes extensionUser = new UserExtensionRes();
        extensionUser.setUid(user.getUid());
        extensionUser.setBalanceAmount(user.getBalanceAmount());
        extensionUser.setRoleId(user.getRoleId());
        return RestResult.ok(extensionUser);
    }

    @Override
    public User findUserByMobile(String mobile) {
        return userRepository.findByMobileEquals(mobile);
    }

    @Override
    public User findUserByName(String name) {
        return userRepository.findByNameEquals(name);
    }


}
