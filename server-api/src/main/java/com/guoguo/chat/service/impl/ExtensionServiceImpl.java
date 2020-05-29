package com.guoguo.chat.service.impl;

import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.entity.Extension;
import com.guoguo.chat.enums.RestCodeEnums;
import com.guoguo.chat.repository.ExtensionRepository;
import com.guoguo.chat.req.base.BasePageReq;
import com.guoguo.chat.service.ExtensionService;
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
public class ExtensionServiceImpl implements ExtensionService {

    @Autowired
    private ExtensionRepository extensionRepository;

    @Override
    public RestResult list(BasePageReq basePageReq) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //创建时间降序排序
            Pageable pageable = new PageRequest(basePageReq.getPage()-1,basePageReq.getPageSize(),sort);
            Page<Extension> page =  extensionRepository.findAll(pageable);
            PageResponseVO responseVO = new PageResponseVO();
            responseVO.setContent(page.getContent());
            responseVO.setPage(basePageReq.getPage());
            responseVO.setPageSize(basePageReq.getPageSize());
            responseVO.setTotalPage(page.getTotalPages());
            responseVO.setTotalSize(page.getTotalElements());
            return RestResult.ok(responseVO);
        }catch (Exception e){
            log.error("推广平台列表接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }
    }
}
