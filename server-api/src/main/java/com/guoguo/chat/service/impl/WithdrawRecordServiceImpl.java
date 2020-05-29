package com.guoguo.chat.service.impl;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.AccountRecord;
import com.guoguo.chat.entity.Extension;
import com.guoguo.chat.entity.User;
import com.guoguo.chat.entity.WithdrawRecord;
import com.guoguo.chat.enums.AccountRecordTypeEnums;
import com.guoguo.chat.enums.AmountFlowTypeEnums;
import com.guoguo.chat.enums.RestCodeEnums;
import com.guoguo.chat.enums.WithdrawStatusEnums;
import com.guoguo.chat.repository.AccountRecordRepository;
import com.guoguo.chat.repository.ExtensionRepository;
import com.guoguo.chat.repository.UserRepository;
import com.guoguo.chat.repository.WithdrawRecordRepository;
import com.guoguo.chat.req.WithdrawAuditReq;
import com.guoguo.chat.req.WithdrawListReq;
import com.guoguo.chat.req.WithdrawRecordReq;
import com.guoguo.chat.service.WithdrawRecordService;
import com.guoguo.chat.vo.PageResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class WithdrawRecordServiceImpl implements WithdrawRecordService {

    @Autowired
    private WithdrawRecordRepository withdrawRecordRepository;

    @Autowired
    private AccountRecordRepository accountRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExtensionRepository extensionRepository;

    @Override
    public RestResult add(WithdrawRecordReq withdrawRecordReq) {
        try {
            User user = userRepository.findByUid(withdrawRecordReq.getUid());
            Optional<Extension> byId = extensionRepository.findById(withdrawRecordReq.getPlatformId());
            if(!byId.isPresent()){
                return RestResult.error(RestCodeEnums.ERROR_WITHDRAW_PLATFROM_NOT);
            }
            Extension extension = byId.get();
            if(withdrawRecordReq.getAmount()<extension.getMinimumMoney()){
                return RestResult.error(RestCodeEnums.ERROR_WITHDRAW_AMOUNT_FAIL);
            }
            if(user.getBalanceAmount().intValue()<withdrawRecordReq.getAmount()){
                return RestResult.error(RestCodeEnums.ERROR_USER_BALANCE);
            }
            if(user.getBalanceAmount().intValue()<withdrawRecordReq.getAmount()){
                return RestResult.error(RestCodeEnums.ERROR_USER_BALANCE);
            }
            int amount = withdrawRecordReq.getAmount().intValue();
            WithdrawRecord withdrawRecord = new WithdrawRecord();
            withdrawRecord.setAmount(withdrawRecordReq.getAmount());
            withdrawRecord.setId(UUID.randomUUID().toString());
            withdrawRecord.setMobile(withdrawRecordReq.getMobile());
            withdrawRecord.setPlatform(withdrawRecordReq.getPlatform());
            withdrawRecord.setRemark(withdrawRecordReq.getRemark());
            withdrawRecord.setUid(withdrawRecordReq.getUid());
            withdrawRecord.setUserName(withdrawRecordReq.getUserName());
            withdrawRecord.setUserAccount(withdrawRecordReq.getUserAccount());
            withdrawRecord.setWithdrawTime(System.currentTimeMillis());
            withdrawRecord.setWithdrawStatus(WithdrawStatusEnums.AUDIT_ING.getCode().intValue());
            withdrawRecord.setRemark(withdrawRecordReq.getRemark());
            withdrawRecord.setAuditRemark(null);
            int result = userRepository.decBalanceAmountByUserId(amount,withdrawRecordReq.getUid());
            withdrawRecordRepository.saveAndFlush(withdrawRecord);
            this.buildAccountRecord(AmountFlowTypeEnums.FLOW_OUT,AccountRecordTypeEnums.WITHDRAW_TYPE,amount
                    ,withdrawRecord.getUid(),withdrawRecord.getPlatform(),
                    user.getBalanceAmount()-amount,withdrawRecord.getId(),WithdrawStatusEnums.AUDIT_ING.getCode().intValue(),null);
            return RestResult.ok(withdrawRecord.getId());
        }catch (Exception e){
            log.error("提现接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }
    }

    @Override
    public RestResult list(WithdrawListReq withdrawListReq) {
        try{
            Sort sort = new Sort(Sort.Direction.DESC,"withdrawTime"); //创建时间降序排序
            Pageable pageable = new PageRequest(withdrawListReq.getPage()-1,withdrawListReq.getPageSize(),sort);
            WithdrawRecord withdrawRecord = new WithdrawRecord();
            if(withdrawListReq.getWithdrawStatus().intValue()!=0){
                withdrawRecord.setWithdrawStatus(withdrawListReq.getWithdrawStatus());
            }
            withdrawRecord.setUid(withdrawListReq.getUuid());
            Example<WithdrawRecord> example = Example.of(withdrawRecord);
            Page<WithdrawRecord> page = withdrawRecordRepository.findAll(example,pageable);
            PageResponseVO responseVO = new PageResponseVO();
            responseVO.setContent(page.getContent());
            responseVO.setPage(withdrawListReq.getPage());
            responseVO.setPageSize(withdrawListReq.getPageSize());
            responseVO.setTotalPage(page.getTotalPages());
            responseVO.setTotalSize(page.getTotalElements());
            return RestResult.ok(responseVO);
        }catch (Exception e){
            log.error("提现记录列表接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }
    }

    /**
     * 审核通过
     * @param withdrawAuditReq
     * @return
     */
    @Override
    public RestResult pass(WithdrawAuditReq withdrawAuditReq) {
        try{
            Optional<WithdrawRecord> withdrawRecord = withdrawRecordRepository.findById(withdrawAuditReq.getId());
            if(withdrawRecord.isPresent()){
                WithdrawRecord wr = withdrawRecord.get();
                if(WithdrawStatusEnums.AUDIT_ING.getCode().intValue()==wr.getWithdrawStatus()){
                    wr.setWithdrawStatus(WithdrawStatusEnums.AUDIT_PASS.getCode());
                    wr.setAuditTime(System.currentTimeMillis());
                    wr.setAuditUser(withdrawAuditReq.getAuditUser());
                    withdrawRecordRepository.saveAndFlush(wr);
                    AccountRecord byWithdrawId = accountRecordRepository.findByWithdrawId(withdrawAuditReq.getId());
                    byWithdrawId.setAuditStatus(WithdrawStatusEnums.AUDIT_PASS.getCode());
                    accountRecordRepository.saveAndFlush(byWithdrawId);
                    return RestResult.ok(null);
                }
            }
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }catch (Exception e){
            log.error("审核通过接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }
    }

    private void buildAccountRecord(AmountFlowTypeEnums amountFlowTypeEnums,
                                    AccountRecordTypeEnums accountRecordTypeEnums,
                                    Integer amount, String uid, String targetName, Integer balanceAmount,String recordTypeId,int auditStatus,String auditRemark){
        AccountRecord accountRecord = new AccountRecord();
        accountRecord.setId(UUID.randomUUID().toString());
        accountRecord.setAmount(amount);
        accountRecord.setCreateTime(System.currentTimeMillis());
        accountRecord.setRecordType(accountRecordTypeEnums.getCode());
        accountRecord.setFlow(amountFlowTypeEnums.getCode());
        accountRecord.setUid(uid);
        accountRecord.setTargetName(targetName);
        accountRecord.setBalanceAmount(balanceAmount);
        accountRecord.setRecordTypeId(recordTypeId);
        accountRecord.setRedStatus(0);
        accountRecord.setAuditStatus(auditStatus);
        accountRecord.setAuditRemark(auditRemark);
        accountRecordRepository.saveAndFlush(accountRecord);
    }
    /**
     * 审核驳回
     * @param withdrawAuditReq
     * @return
     */
    @Override
    public RestResult rollback(WithdrawAuditReq withdrawAuditReq) {
        try{
            Optional<WithdrawRecord> withdrawRecord = withdrawRecordRepository.findById(withdrawAuditReq.getId());
            if(withdrawRecord.isPresent()){
                WithdrawRecord wr = withdrawRecord.get();
                wr.setWithdrawStatus(WithdrawStatusEnums.AUDIT_NO_PASS.getCode());
                wr.setAuditTime(System.currentTimeMillis());
                wr.setAuditUser(withdrawAuditReq.getAuditUser());
                wr.setAuditRemark(withdrawAuditReq.getAuditRemark());
                withdrawRecordRepository.saveAndFlush(wr);
                User user = userRepository.findByUid(wr.getUid());
                int balanceAmount = user.getBalanceAmount()+wr.getAmount();
                int result = userRepository.incBalanceAmountByUserId(wr.getAmount(),wr.getUid());
                this.buildAccountRecord(AmountFlowTypeEnums.FLOW_IN,AccountRecordTypeEnums.WITHDRAW_TYPE,wr.getAmount()
                        ,wr.getUid(),wr.getPlatform(),balanceAmount,wr.getId(),WithdrawStatusEnums.AUDIT_NO_PASS.getCode(),
                        withdrawAuditReq.getAuditRemark());
                return RestResult.ok(null);
            }
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }catch (Exception e){
            log.error("审核驳回接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }

    }

    @Override
    public RestResult findOne(String id) {
        try {
            return RestResult.ok(withdrawRecordRepository.findById(id).get());
        }catch (Exception e){
            log.error("查询审核记录接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }
    }
}
