package com.guoguo.chat.service.impl;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.AccountRecord;
import com.guoguo.chat.enums.AmountFlowTypeEnums;
import com.guoguo.chat.enums.RestCodeEnums;
import com.guoguo.chat.repository.AccountRecordRepository;
import com.guoguo.chat.req.AccountRecordReq;
import com.guoguo.chat.service.AccountRecordService;
import com.guoguo.chat.vo.PageResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AccountRecordServiceImpl implements AccountRecordService {

    @Autowired
    AccountRecordRepository accountRecordRepository;

    @Override
    public RestResult findByUid(AccountRecordReq accountRecordReq) {
        try{
            Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //创建时间降序排序
            Pageable pageable = new PageRequest(accountRecordReq.getPage()-1,accountRecordReq.getPageSize(),sort);
            Page<AccountRecord> page = null;
            if(AmountFlowTypeEnums.ALL.getCode().intValue() == accountRecordReq.getFlowType().intValue()
                    &&AmountFlowTypeEnums.ALL.getCode().intValue() ==accountRecordReq.getFlow().intValue()){
                page = accountRecordRepository.findByUid(accountRecordReq.getUid(),pageable);
            }
            else if(AmountFlowTypeEnums.ALL.getCode().intValue() == accountRecordReq.getFlowType().intValue()
                    &&accountRecordReq.getFlow().intValue()!=AmountFlowTypeEnums.ALL.getCode().intValue()) {
                page = accountRecordRepository.findByUidAndFlow(accountRecordReq.getUid(),accountRecordReq.getFlow(),pageable);
            }else if(AmountFlowTypeEnums.ALL.getCode().intValue() != accountRecordReq.getFlowType().intValue()
                    &&accountRecordReq.getFlow().intValue()==AmountFlowTypeEnums.ALL.getCode().intValue()){
                page = accountRecordRepository.findByUidAndRecordType(accountRecordReq.getUid(),accountRecordReq.getFlowType().intValue(),pageable);
            }else if(AmountFlowTypeEnums.ALL.getCode().intValue() != accountRecordReq.getFlowType().intValue()&&
                    accountRecordReq.getFlow().intValue()!=AmountFlowTypeEnums.ALL.getCode().intValue()){
                page = accountRecordRepository.findByUidAndRecordTypeAndFlow(accountRecordReq.getUid(),accountRecordReq.getFlowType().intValue(),
                        accountRecordReq.getFlow().intValue(),pageable);
            }
            PageResponseVO responseVO = new PageResponseVO();
            responseVO.setContent(page.getContent());
            responseVO.setPage(accountRecordReq.getPage());
            responseVO.setPageSize(accountRecordReq.getPageSize());
            responseVO.setTotalPage(page.getTotalPages());
            responseVO.setTotalSize(page.getTotalElements());
            return RestResult.ok(responseVO);
        }catch (Exception e){
            log.error("账变记录接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }
    }
}
