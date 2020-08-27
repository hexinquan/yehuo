package com.guoguo.chat.task;

import com.guoguo.chat.entity.AccountRecord;
import com.guoguo.chat.entity.RedPacket;
import com.guoguo.chat.entity.User;
import com.guoguo.chat.enums.AccountRecordTypeEnums;
import com.guoguo.chat.enums.AmountFlowTypeEnums;
import com.guoguo.chat.enums.RedStatusEnums;
import com.guoguo.chat.enums.RedTypeEnums;
import com.guoguo.chat.repository.AccountRecordRepository;
import com.guoguo.chat.repository.RedPacketRepository;
import com.guoguo.chat.repository.UserRepository;
import com.guoguo.chat.utils.LocalDateAndTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
public class RedPacketRollbackRunnable implements Runnable {

    private RedPacketRepository redPacketRepository;

    private UserRepository userRepository;

    private AccountRecordRepository accountRecordRepository;

    private RedisTemplate<String,Object> redisTemplate;

    public RedPacketRollbackRunnable(RedPacketRepository redPacketRepository, UserRepository userRepository,
                                     AccountRecordRepository accountRecordRepository,
                                     RedisTemplate<String, Object> redisTemplate) {
        this.redPacketRepository = redPacketRepository;
        this.userRepository = userRepository;
        this.accountRecordRepository = accountRecordRepository;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void run() {
        try {
            List<RedPacket> redPackets = redPacketRepository.findByOutTimeEquals();
            if (!CollectionUtils.isEmpty(redPackets)) {
                redPackets.forEach(redPacket -> {
                    Long sendTime = redPacket.getSendTime();
                    LocalDateTime sendDateTime = LocalDateAndTimeUtils.timestamToDatetime(sendTime);
                    LocalDateTime outDateTime =
                            LocalDateAndTimeUtils.timestamToDatetime(LocalDateAndTimeUtils.datatimeToTimestamp(LocalDateTime.now()));
                   //过期24小时
                    if (Duration.between(sendDateTime, outDateTime).toHours() >= 24) {
                        log.info("redPackets size:" + redPackets.size());
                        redPacket.setRedStatus(RedStatusEnums.TIME_OUT.getCode());
                        redPacketRepository.saveAndFlush(redPacket);
                        User user = userRepository.findByUid(redPacket.getSenderId());
                        if (RedTypeEnums.SIGNLE_SIMPLE.getCode().intValue() == redPacket.getRedType().intValue()) {
                            log.info("单聊红包过期退还:" + redPacket);
                            int amount = user.getBalanceAmount() + redPacket.getAmount();
                            log.info("单聊红包过期退还金额为:" + amount);
                            user.setBalanceAmount(amount);
                            userRepository.saveAndFlush(user);
                            AccountRecord accountRecord = new AccountRecord();
                            accountRecord.setAmount(redPacket.getAmount());
                            accountRecord.setCreateTime(System.currentTimeMillis());
                            accountRecord.setFlow(AmountFlowTypeEnums.FLOW_IN.getCode());
                            accountRecord.setId(UUID.randomUUID().toString());
                            accountRecord.setRecordType(AccountRecordTypeEnums.RED_TYPE.getCode());
                            accountRecord.setTargetName(redPacket.getTargetName());
                            accountRecord.setUid(redPacket.getSenderId());
                            accountRecord.setBalanceAmount(amount);
                            accountRecord.setRecordTypeId(redPacket.getId());
                            accountRecord.setRedStatus(RedStatusEnums.GIVE_BACK.getCode());
                            accountRecord.setAuditStatus(0);
                            accountRecordRepository.saveAndFlush(accountRecord);
                        }
                        if (RedTypeEnums.SIMPLE_GROUP.getCode().intValue() == redPacket.getRedType().intValue()) {
                            log.info("普通群聊红包过期退还:" + redPacket);
                            int count = redPacket.getCount().intValue() - redPacket.getReceiveCount().intValue();
                            int incAmount = redPacket.getAmount() * count;
                            log.info("普通群聊红包过期退还金额为:" + incAmount);
                            int balanceAmount = user.getBalanceAmount() + incAmount;
                            user.setBalanceAmount(balanceAmount);
                            userRepository.saveAndFlush(user);
                            AccountRecord accountRecord = new AccountRecord();
                            accountRecord.setAmount(incAmount);
                            accountRecord.setCreateTime(System.currentTimeMillis());
                            accountRecord.setFlow(AmountFlowTypeEnums.FLOW_IN.getCode());
                            accountRecord.setId(UUID.randomUUID().toString());
                            accountRecord.setRecordType(AccountRecordTypeEnums.RED_TYPE.getCode());
                            accountRecord.setTargetName(redPacket.getTargetName());
                            accountRecord.setUid(redPacket.getSenderId());
                            accountRecord.setBalanceAmount(balanceAmount);
                            accountRecord.setRecordTypeId(redPacket.getId());
                            accountRecord.setRedStatus(RedStatusEnums.GIVE_BACK.getCode());
                            accountRecord.setAuditStatus(0);
                            accountRecordRepository.saveAndFlush(accountRecord);
                        }
                        if (RedTypeEnums.LUCK.getCode().intValue() == redPacket.getRedType().intValue()) {
                                if(redPacket.getRedBalance()>0){
                                    log.info("群聊手气红包过期退还:" + redPacket);
                                    log.info("群聊手气红包过期退还金额为:" + redPacket.getRedBalance());
                                    int balanceAmount = user.getBalanceAmount() +  redPacket.getRedBalance();
                                    user.setBalanceAmount(balanceAmount);
                                    userRepository.saveAndFlush(user);
                                    AccountRecord accountRecord = new AccountRecord();
                                    accountRecord.setAmount( redPacket.getRedBalance());
                                    accountRecord.setCreateTime(System.currentTimeMillis());
                                    accountRecord.setFlow(AmountFlowTypeEnums.FLOW_IN.getCode());
                                    accountRecord.setId(UUID.randomUUID().toString());
                                    accountRecord.setRecordType(AccountRecordTypeEnums.RED_TYPE.getCode());
                                    accountRecord.setTargetName(redPacket.getTargetName());
                                    accountRecord.setUid(redPacket.getSenderId());
                                    accountRecord.setBalanceAmount(balanceAmount);
                                    accountRecord.setRecordTypeId(redPacket.getId());
                                    accountRecord.setRedStatus(RedStatusEnums.GIVE_BACK.getCode());
                                    accountRecord.setAuditStatus(0);
                                    accountRecordRepository.saveAndFlush(accountRecord);
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
            log.error("定时红包退还错误",e);
        }
    }
}
