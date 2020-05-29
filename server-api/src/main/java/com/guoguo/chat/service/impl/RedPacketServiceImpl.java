package com.guoguo.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.guoguo.chat.common.RestResult;
import com.guoguo.chat.constant.Amount;
import com.guoguo.chat.constant.ConversationType;
import com.guoguo.chat.entity.*;
import com.guoguo.chat.enums.*;
import com.guoguo.chat.repository.*;
import com.guoguo.chat.req.*;
import com.guoguo.chat.rest.RestService;
import com.guoguo.chat.service.RedPacketService;
import com.guoguo.chat.utils.LocalDateAndTimeUtils;
import com.guoguo.chat.utils.RedPacketUtil;
import com.guoguo.chat.utils.RedisUtil;
import com.guoguo.chat.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
@Transactional
public class RedPacketServiceImpl implements RedPacketService {

    private static final Logger LOG = LoggerFactory.getLogger(RedPacketServiceImpl.class);

    @Autowired
    private RedPacketRepository redpacketRepository;

    @Autowired
    private RedReceiveRepository redReceiveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestService restService;

    @Autowired
    private AccountRecordRepository accountRecordRepository;

    @Autowired
    private EnvelopesConfigRepository envelopesConfigRepository;

    @Value("${imserver.url}")
    private String imMessageUrl;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 发送红包
     * @param redPacketReq
     * @return
     */
    @Override
    public RestResult send(final RedPacketReq redPacketReq) throws Exception{
        RedisUtil redisUtil =new RedisUtil(redisTemplate);
        try {
            User user = userRepository.findByUid(redPacketReq.getSenderId());
            if (null == user) {
                return RestResult.error(RestCodeEnums.ERROR_USER_NOT_FOUND);
            }
            if (redPacketReq.getCount() <= 0) {
                return RestResult.error(RestCodeEnums.ERROR_RED_COUNT);
            }
            Integer totalAMount = redPacketReq.getAmount();
            EnvelopesConfig envelopesConfig = envelopesConfigRepository.findByType(redPacketReq.getRedType());
            if (RedTypeEnums.SIMPLE_GROUP.getCode().intValue() == redPacketReq.getRedType().intValue()) {
                totalAMount = redPacketReq.getAmount() * redPacketReq.getCount();
            }
            LOG.info("后台配置的限制金额:" + envelopesConfig);
            if (envelopesConfig != null) {
                int luckMinAmount = envelopesConfig.getMinMoney().intValue() * redPacketReq.getCount().intValue();
                //群聊手气红包必须剩余红包个数
                if (RedTypeEnums.LUCK.getCode().intValue() == redPacketReq.getRedType().intValue()) {
                    if (redPacketReq.getAmount().intValue() < luckMinAmount) {
                        return RestResult.error(RestCodeEnums.MESSAGE_RED_MIX);
                    }
                } else {
                    if (redPacketReq.getAmount().intValue() < envelopesConfig.getMinMoney().intValue()) {
                        return RestResult.error(RestCodeEnums.MESSAGE_RED_MIX);
                    }
                }
                if (redPacketReq.getAmount().intValue() > envelopesConfig.getMaxMoney().intValue()) {
                    return RestResult.error(RestCodeEnums.MESSAGE_RED_MAX);
                }
            } else {
                //群聊手气红包必须剩余红包个数
                if (RedTypeEnums.LUCK.getCode().intValue() == redPacketReq.getRedType().intValue()) {
                    if (Amount.MIN_AMOUNT * redPacketReq.getCount() > redPacketReq.getAmount().intValue()) {
                        return RestResult.error(RestCodeEnums.MESSAGE_RED_MIX);
                    }
                } else {
                    if (Amount.MIN_AMOUNT > redPacketReq.getAmount()) {
                        return RestResult.error(RestCodeEnums.MESSAGE_RED_MIX);
                    }
                }
                if (Amount.MAX_AMOUNT < redPacketReq.getAmount()) {
                    return RestResult.error(RestCodeEnums.MESSAGE_RED_MAX);
                }
            }
            //判断余额
            if (user.getBalanceAmount() < totalAMount) {
                return RestResult.error(RestCodeEnums.ERROR_USER_BALANCE);
            }
            if (RedTypeEnums.SIGNLE_SIMPLE.getCode().intValue() == redPacketReq.getRedType() ||
                    RedTypeEnums.SIMPLE_GROUP.getCode().intValue() == redPacketReq.getRedType().intValue()) {
                return signleAndGroupSend(redPacketReq, redisUtil, user);
            } else {
                return groupLuckSend(redPacketReq, redisUtil, user);
            }
        }catch (Exception e){
            log.error("发送红包接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }

    }

    /**
     * 单聊与群普通发红包
     * @param redPacketReq
     * @param redisUtil
     * @param user
     * @return
     */
    public RestResult signleAndGroupSend(RedPacketReq redPacketReq,RedisUtil redisUtil,User user)throws Exception{
        RedPacket redPacket = new RedPacket();
        String uuid = UUID.randomUUID().toString();
        BeanUtils.copyProperties(redPacketReq,redPacket);
        long sendTime = System.currentTimeMillis();
        int totalAmount= redPacketReq.getAmount();
        if(RedTypeEnums.SIMPLE_GROUP.getCode().intValue()==redPacketReq.getRedType().intValue()){
            totalAmount=redPacketReq.getAmount()*redPacketReq.getCount();
        }
        redPacket.setId(uuid);
        redPacket.setRedBalance(totalAmount);
        redPacket.setSendTime(sendTime);
        //24小时过期
        long outTime = LocalDateAndTimeUtils.datatimeToTimestamp(LocalDateAndTimeUtils.timestamToDatetime(sendTime).plusHours(24));
        redPacket.setOutTime(outTime); //24小时过期
        redPacket.setRedStatus(1);
        CacheRedpacketVO cacheRedpacketVO = buildCacheList(redPacketReq,sendTime);
        redpacketRepository.saveAndFlush(redPacket);
        int balanceAmount = user.getBalanceAmount()- totalAmount;
        user.setBalanceAmount(balanceAmount);//发红包减余额
        userRepository.saveAndFlush(user); //更新余额
        //账变记录
        this.buildAccountRecord(AmountFlowTypeEnums.FLOW_OUT,AccountRecordTypeEnums.RED_TYPE,totalAmount,
                redPacketReq.getSenderId(),redPacket.getTargetName(),balanceAmount,redPacket.getId());
        redisUtil.set(uuid, cacheRedpacketVO, 86400);//红包失效时间
        //需要推送消息
        ImServerRespVO imServerRespVO =
                restService.doPost(imMessageUrl, buildSendPacketMsg(redPacket, ImRedTypeEnums.SEND_RED_PACKET,user),new ImServerRespVO().getClass());
        LOG.info("推送IM消息返回:"+imServerRespVO);
        return RestResult.ok(uuid);
    }


    /**
     * 发红包消息
     */
    private ImServerMessageReq buildSendPacketMsg(RedPacket redPacket,ImRedTypeEnums imRedTypeEnums,User user)throws Exception{
        ImServerMessageReq imServerMessageReq =new ImServerMessageReq();
        imServerMessageReq.setSearchAbleContent(redPacket.getGreetings());
        imServerMessageReq.setPushContent(user.getDisplayName()+"发送了一个红包");
        imServerMessageReq.setSender(redPacket.getSenderId());
        imServerMessageReq.setTarget(redPacket.getTargetId());
        imServerMessageReq.setType(imRedTypeEnums.getCode().intValue());
        int conversationType = ConversationType.ConversationType_Private;
        if (redPacket.getRedType() != RedTypeEnums.SIGNLE_SIMPLE.getCode().intValue()) {
            conversationType = ConversationType.ConversationType_Group;
        }
        imServerMessageReq.setConversationType(conversationType);
        HashMap<String,String> content = new HashMap<>();
        content.put("redpacketId",redPacket.getId());
        imServerMessageReq.setContent(JSON.toJSONString(content));
        LOG.info("推送IM请求参数:"+imServerMessageReq);
        return imServerMessageReq;
    }


    /**
     * 收红包消息
     */
    private void openPacketSend(RedPacket redPacket,ImRedTypeEnums imRedTypeEnums,User user)throws Exception{
        ImServerMessageReq imServerMessageReq = null ;
        if (imRedTypeEnums == ImRedTypeEnums.RECEIVE_RED_PACKET ) {
            if (RedTypeEnums.SIGNLE_SIMPLE.getCode().intValue() == redPacket.getRedType().intValue()) {
                User targetUser = userRepository.findByUid(redPacket.getTargetId());
                imServerMessageReq = buildImServerMessageReq( redPacket,imRedTypeEnums,
                        redPacket.getSenderId(),redPacket.getSenderName(),targetUser.getUid(),targetUser.getDisplayName());
                imServerMessageReq.setSender(redPacket.getSenderId());
                imServerMessageReq.setTarget(redPacket.getTargetId());
                imServerMessageReq.setSearchAbleContent(targetUser.getDisplayName() +"领取了的红包");
            } else {
                imServerMessageReq = buildImServerMessageReq(redPacket, imRedTypeEnums,
                        redPacket.getSenderId(), redPacket.getSenderName(), user.getUid(), user.getDisplayName());
                imServerMessageReq.setSearchAbleContent( user.getDisplayName() +"领取了" + redPacket.getSenderName() + "的红包");
                imServerMessageReq.setSender(user.getUid());
                imServerMessageReq.setTarget(redPacket.getTargetId());
            }
        }
        LOG.info("推送IM请求参数:{}",imServerMessageReq);
        if (null != imServerMessageReq) {
            ImServerRespVO imServerRespVO = restService.doPost(imMessageUrl, imServerMessageReq,
                    new ImServerRespVO().getClass());
            LOG.info("推送IM消息返回:" + imServerRespVO);
        }
    }

    public ImServerMessageReq buildImServerMessageReq(RedPacket redPacket,ImRedTypeEnums imRedTypeEnums,
                                                      String senderId,String senderName,String receiverId,String  receiverName ){
        ImServerMessageReq imServerMessageReq =new ImServerMessageReq();
        imServerMessageReq.setType(imRedTypeEnums.getCode().intValue());
        int conversationType = ConversationType.ConversationType_Private;
        if (redPacket.getRedType() != RedTypeEnums.SIGNLE_SIMPLE.getCode().intValue()) {
            conversationType = ConversationType.ConversationType_Group;
        }
        imServerMessageReq.setConversationType(conversationType);
        Map content = new HashMap<>();
        content.put("redpacketId",redPacket.getId());
        content.put("senderId",senderId);
        content.put("senderName",senderName);
        content.put("receiverId",receiverId);
        content.put("receiverName",receiverName);
        imServerMessageReq.setContent(JSON.toJSONString(content));
        return imServerMessageReq;
    }

    /**
     * 群聊拼手气红包发送
     */
    public RestResult groupLuckSend(RedPacketReq redPacketReq,RedisUtil redisUtil,User user)throws Exception{
        RedPacketUtil redPacketUtil = new RedPacketUtil();
        RedPacket redPacket = new RedPacket();
        String uuid = UUID.randomUUID().toString();
        BeanUtils.copyProperties(redPacketReq,redPacket);
        long sendTime = System.currentTimeMillis();
        redPacket.setId(uuid);
        redPacket.setRedBalance(redPacketReq.getAmount());
        redPacket.setSendTime(sendTime);
        //24小时过期
        long outTime = LocalDateAndTimeUtils.datatimeToTimestamp(LocalDateAndTimeUtils.timestamToDatetime(sendTime).plusHours(24));
        redPacket.setOutTime(outTime); //过期时间
        redPacket.setRedStatus(1);
        int decAmount = redPacketReq.getAmount(); //扣减发出的金额
        //创建红包
        List<CacheRedpacketVO> cacheRedpacketVOList = new ArrayList<>();
        //拼手气算法
        List<Integer> redPackets = redPacketUtil.splitRedPacket(redPacketReq.getAmount(), redPacketReq.getCount());
        for (int i=0;i<redPackets.size();i++){
            redPacketReq.setAmount(redPackets.get(i));//设置随机的金额
            CacheRedpacketVO cacheRedpacketVO = buildCacheList(redPacketReq,sendTime);
            cacheRedpacketVOList.add(cacheRedpacketVO);
        }
        redpacketRepository.saveAndFlush(redPacket);
        int balanaceAmount = user.getBalanceAmount()-decAmount;
        user.setBalanceAmount(balanaceAmount);//发红包减余额
        userRepository.saveAndFlush(user); //更新余额
        //账变记录
        this.buildAccountRecord(AmountFlowTypeEnums.FLOW_OUT,AccountRecordTypeEnums.RED_TYPE,decAmount,
                redPacketReq.getSenderId(),redPacket.getTargetName(),balanaceAmount,redPacket.getId());
        redisUtil.set(uuid, cacheRedpacketVOList, 86400);//红包24小时失效时间
        //需要推送消息
        ImServerRespVO imServerRespVO = restService.doPost(imMessageUrl, buildSendPacketMsg(redPacket, ImRedTypeEnums.SEND_RED_PACKET,user),
                new ImServerRespVO().getClass());
        LOG.info("推送IM消息返回:"+imServerRespVO);
        return RestResult.ok(uuid);
    }

    /**
     * 构建红包缓存对象
     * @return
     */
    private CacheRedpacketVO buildCacheList(RedPacketReq redPacketReq,long sndTime){
        CacheRedpacketVO cacheRedpacketVO=new CacheRedpacketVO();
        cacheRedpacketVO.setRedStatus(1);
        cacheRedpacketVO.setSenderName(redPacketReq.getSenderName());
        cacheRedpacketVO.setGreetings(redPacketReq.getGreetings());
        cacheRedpacketVO.setAmount(redPacketReq.getAmount());
        cacheRedpacketVO.setSendTime(sndTime);
        cacheRedpacketVO.setRedType(redPacketReq.getRedType());
        cacheRedpacketVO.setSenderId(redPacketReq.getSenderId());
        cacheRedpacketVO.setTargetId(redPacketReq.getTargetId());
        cacheRedpacketVO.setTargetName(redPacketReq.getTargetName());
        cacheRedpacketVO.setCount(redPacketReq.getCount());
        return cacheRedpacketVO;
    }

    /**
     * 领取红包
     * @param openRedpacketReq
     * @return
     */
    @Override
    public synchronized RestResult open(final OpenRedpacketReq openRedpacketReq){
        try{
            RedPacket redPacket = redpacketRepository.findById(openRedpacketReq.getRedpacketId()).get();
            if(redPacket==null){
                return RestResult.error(RestCodeEnums.ERROR_RED_NOTFOUND);
            }
            //领红包用户
            User user = userRepository.findByUid(openRedpacketReq.getUserId());

            if(user==null){
                return RestResult.error(RestCodeEnums.ERROR_USER_NOT_FOUND);
            }
            //是否领取过
            RedReceive currentUserReceive =
                    redReceiveRepository.findByRedIdAndUserId(openRedpacketReq.getRedpacketId(), openRedpacketReq.getUserId());
            if(currentUserReceive!=null){
                return  RestResult.error(RestCodeEnums.MESSAGE_RED_RECEIVED);
            }
            //判断是否过期
            if(RedStatusEnums.TIME_OUT.getCode().intValue()==redPacket.getRedStatus()){
                return RestResult.error(RestCodeEnums.ERROR_RED_TIMEOUT);
            }
            //是否已退还
            if(RedStatusEnums.GIVE_BACK.getCode().intValue()==redPacket.getRedStatus()){
                return RestResult.error(RestCodeEnums.ERROR_RED_GIVBACK);
            }
            //领取单聊红包
            if(RedTypeEnums.SIGNLE_SIMPLE.getCode().intValue()==redPacket.getRedType()){
                return this.singleOpen(redPacket,openRedpacketReq,user);
            }
            //领取群聊普通红包
            if(RedTypeEnums.SIMPLE_GROUP.getCode().intValue()==redPacket.getRedType()){
                return this.groupOpen(redPacket,openRedpacketReq,user);
            }
            //领取群聊拼手气红包
            if(RedTypeEnums.LUCK.getCode().intValue()==redPacket.getRedType()){
                return this.luckOpen(redPacket,openRedpacketReq,user);
            }
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }catch (Exception e){
            log.error("领取红包接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }
    }

    /**
     * 单聊红包领取
     * @param redPacket
     * @param openRedpacketReq
     * @return
     */
    private RestResult singleOpen(RedPacket redPacket,OpenRedpacketReq openRedpacketReq,User user)throws Exception{
        String redPacketId = openRedpacketReq.getRedpacketId();
        String userId=openRedpacketReq.getUserId();
        if(RedStatusEnums.RECEIVE.getCode().intValue()==redPacket.getRedStatus()){
            return RestResult.error(RestCodeEnums.MESSAGE_RED_RECEIVED);
        }
        //单聊不能领取自己发的红包,返回红包信息即可
        if(openRedpacketReq.getUserId().equals(redPacket.getSenderId())){
            return RestResult.error(RestCodeEnums.ERROR_RED_USER_RECEIVE);
        }
        //已领取
        redPacket.setRedStatus(RedStatusEnums.RECEIVE.getCode().intValue());
        redPacket.setReceiveCount(redPacket.getReceiveCount()+1);
        redpacketRepository.saveAndFlush(redPacket);
        int balanceAmount = user.getBalanceAmount()+redPacket.getAmount();
        userRepository.incBalanceAmountByUserId(redPacket.getAmount(),userId); //领取成功
        redReceiveRepository.saveAndFlush(buildRedReceiveRecord(redPacket,user));
        //账变记录
        this.buildAccountRecord(AmountFlowTypeEnums.FLOW_IN,AccountRecordTypeEnums.RED_TYPE,redPacket.getAmount(),
                openRedpacketReq.getUserId(),redPacket.getSenderName(),balanceAmount,redPacketId);
        //需要推送消息
        openPacketSend(redPacket,ImRedTypeEnums.RECEIVE_RED_PACKET,user);
        return RestResult.ok(buildReceiveMessage(redPacket));
           // restService.doPost() 推送消息
    }
    private void buildAccountRecord(AmountFlowTypeEnums amountFlowTypeEnums,
                                             AccountRecordTypeEnums accountRecordTypeEnums,
                                             Integer amount,String uid,String targetName,Integer balanceAmount,String recordTypeId){
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
        accountRecord.setAuditStatus(0);
        accountRecord.setRedStatus(0);
        accountRecordRepository.saveAndFlush(accountRecord);
    }

    /**
     * 普通群聊红包领取
     * @param redPacket
     * @param openRedpacketReq
     * @return
     */
    private synchronized RestResult groupOpen(RedPacket redPacket,final OpenRedpacketReq openRedpacketReq,final User user)throws Exception{
        //手气慢了
        if(redPacket.getReceiveCount()>=redPacket.getCount()){
            return RestResult.error(RestCodeEnums.MESSAGE_RED_NOTLUCK);
        }
        redPacket.setReceiveCount(redPacket.getReceiveCount()+1);
        redpacketRepository.saveAndFlush(redPacket);
        int balanceAmount = user.getBalanceAmount()+redPacket.getAmount();
        userRepository.incBalanceAmountByUserId(redPacket.getAmount(),openRedpacketReq.getUserId()); //领取成功
        redReceiveRepository.saveAndFlush(buildRedReceiveRecord(redPacket,user));
        //账变记录
        this.buildAccountRecord(AmountFlowTypeEnums.FLOW_IN,AccountRecordTypeEnums.RED_TYPE,redPacket.getAmount(),
                openRedpacketReq.getUserId(),redPacket.getSenderName(),balanceAmount,redPacket.getId());
        //需要推送消息
        openPacketSend(redPacket,ImRedTypeEnums.RECEIVE_RED_PACKET,user);
        return RestResult.ok(buildReceiveMessage(redPacket));
    }

    /**
     * 群聊手气红包领取
     * @param redPacket
     * @param openRedpacketReq
     * @return
     */
    private synchronized RestResult luckOpen(final RedPacket redPacket,final OpenRedpacketReq openRedpacketReq,final User user){
        LOG.info("群聊手气红包领取redPacket:"+redPacket);
        LOG.info("群聊手气红包领取openRedpacketReq:"+openRedpacketReq);
        LOG.info("群聊手气红包领取的用户user:"+user);
        try{
            String redPacketId = openRedpacketReq.getRedpacketId();
            //手气慢了
            if(redPacket.getReceiveCount()>=redPacket.getCount()){
                return RestResult.error(RestCodeEnums.MESSAGE_RED_NOTLUCK);
            }
            RedisUtil redisUtil =new RedisUtil(redisTemplate);
            //已经分配好的手气红包
            List<CacheRedpacketVO> cacheRedpacketVOList =
                    (List<CacheRedpacketVO>)redisUtil.get(redPacketId);
            log.info("领取前缓存的手气红包大小"+cacheRedpacketVOList.size());
            log.info("领取前缓存的手气红包"+cacheRedpacketVOList);
            if(cacheRedpacketVOList.size()<=0){
                return RestResult.error(RestCodeEnums.MESSAGE_RED_NOTLUCK);
            }
            RedReceiveVO redReceiveServiceVO=new RedReceiveVO();
            //synchronized (cacheRedpacketVOList){
                if(cacheRedpacketVOList!=null){
                    Iterator<CacheRedpacketVO> iterator = cacheRedpacketVOList.iterator();
                    while (iterator.hasNext()){
                        log.info("领取前余额:"+redPacket.getRedBalance());
                        CacheRedpacketVO next = iterator.next();
                        redPacket.setReceiveCount(redPacket.getReceiveCount()+1);
                        RedPacket returnRedPacket= new RedPacket();
                        BeanUtils.copyProperties(redPacket,returnRedPacket);
                        returnRedPacket.setAmount(next.getAmount());
                        int redBalanceAmount = redPacket.getRedBalance()-next.getAmount();
                        returnRedPacket.setRedBalance(redBalanceAmount);
                        redPacket.setRedBalance(redBalanceAmount);
                        redReceiveServiceVO = buildReceiveMessage(returnRedPacket);
                        redpacketRepository.saveAndFlush(redPacket);
                        log.info("领取后余额:"+redPacket.getRedBalance());
                        int balanceAmount = user.getBalanceAmount()+next.getAmount();
                        user.setBalanceAmount(balanceAmount);
                        userRepository.saveAndFlush(user); //领取成功
                        redReceiveRepository.saveAndFlush(buildRedReceiveRecord(returnRedPacket,user));
                        //账变记录
                        this.buildAccountRecord(AmountFlowTypeEnums.FLOW_IN,AccountRecordTypeEnums.RED_TYPE,next.getAmount(),
                                openRedpacketReq.getUserId(),redPacket.getSenderName(),balanceAmount,redPacketId);
                        iterator.remove();
                        break;
                    }
                    redisUtil.set(redPacketId,cacheRedpacketVOList);
                }
            //}
            log.info("领取后缓存的手气红包大小"+cacheRedpacketVOList.size());
            log.info("领取后缓存的手气红包"+cacheRedpacketVOList);
            //需要推送消息
            openPacketSend(redPacket,ImRedTypeEnums.RECEIVE_RED_PACKET,user);
            return RestResult.ok(redReceiveServiceVO);
        }catch (Exception e){
            log.error("手气红包出错",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }
    }

    /**
     * 构建领取返回信息
     * @param redPacket
     * @return
     */
    private RedReceiveVO buildReceiveMessage(RedPacket redPacket){
        RedReceiveVO redReceiveServiceVO = new RedReceiveVO();
        redReceiveServiceVO.setGreetings(redPacket.getGreetings());
        redReceiveServiceVO.setSenderName(redPacket.getSenderName());
        redReceiveServiceVO.setReceiveAmount(redPacket.getAmount());
        redReceiveServiceVO.setReceiveTime(System.currentTimeMillis());
        User senderUser = userRepository.findByUid(redPacket.getSenderId());
        redReceiveServiceVO.setSenderPic(senderUser.getPortrait());
        return redReceiveServiceVO;
    }


    /**
     * 生成领取记录
     * @param redPacket
     * @param receiveUser
     * @return
     */
    private RedReceive buildRedReceiveRecord(RedPacket redPacket,User receiveUser){
        RedReceive redReceive = new RedReceive();
        redReceive.setId(UUID.randomUUID().toString());
        redReceive.setRedId(redPacket.getId());
        redReceive.setCreateTime(System.currentTimeMillis());
        redReceive.setSenderId(redPacket.getSenderId());
        redReceive.setSenderName(redPacket.getSenderName());
        redReceive.setReceiveName(receiveUser.getDisplayName());
        redReceive.setReceiveId(receiveUser.getUid());
        redReceive.setReceiveAmount(redPacket.getAmount());
        redReceive.setReceiveTime(System.currentTimeMillis());
        redReceive.setReply(redPacket.getGreetings());
        redReceive.setReceivePic(receiveUser.getPortrait());
        return redReceive;
    }
    /**
     * 返回红包记录
     * @param redpacketId
     * @return
     */
    private RestResult getRedReceives(String redpacketId){
        RedPacket redPacket = redpacketRepository.findById(redpacketId).get();
        RedPacketReceiveVO redPacketReceiveVO = new RedPacketReceiveVO();
        if(RedTypeEnums.SIMPLE_GROUP.getCode().intValue()==redPacket.getRedType().intValue()){
            redPacketReceiveVO.setTotalAmount(redPacket.getCount()*redPacket.getAmount());
        }
        BeanUtils.copyProperties(redPacket,redPacketReceiveVO);
        redPacketReceiveVO.setTotalAmount(redPacket.getAmount());
        List<RedReceive> redReceives = redReceiveRepository.findByRedId(redpacketId);
        List<RedReceiveVO> redReceiveServiceVOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(redReceives)){
            redReceives.forEach(redReceive -> {
                RedReceiveVO redReceiveServiceVO =new RedReceiveVO();
                BeanUtils.copyProperties(redReceive,redReceiveServiceVO);
                redPacketReceiveVO.setReceiveTotalAmount(redPacketReceiveVO.getReceiveTotalAmount()+
                        redReceive.getReceiveAmount()); //已领取总金额
                redReceiveServiceVOS.add(redReceiveServiceVO);
            });
        }
        redPacketReceiveVO.setRedReceiveVOS(redReceiveServiceVOS);
        return RestResult.ok(redPacketReceiveVO);
    }

    /**
     * 发送红包记录列表
     * @param sendRedPacketReq
     * @return
     */
    @Override
    public RestResult sendList(SendRedPacketReq sendRedPacketReq) {
        Sort sort = new Sort(Sort.Direction.DESC,"sendTime"); //创建时间降序排序
        Pageable pageable = new PageRequest(sendRedPacketReq.getPage()-1,sendRedPacketReq.getPageSize(),sort);
        Page<RedPacket> redPackets = redpacketRepository.findBySenderIdEquals(sendRedPacketReq.getUuid(),pageable);
        if(CollectionUtils.isEmpty(redPackets.getContent())){
            return RestResult.ok(redPackets);
        }
        LOG.info("返回的列表:"+redPackets);
        List<SendListRespVO> sendListRespVOS = new ArrayList<>();
        redPackets.getContent().forEach(redPacket -> {
            SendListRespVO sendListRespVO =new SendListRespVO();
            BeanUtils.copyProperties(redPacket,sendListRespVO);
            sendListRespVOS.add(sendListRespVO);
        });
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("totalPage",redPackets.getTotalPages());
        hashMap.put("totalSize",redPackets.getTotalElements());
        hashMap.put("page",sendRedPacketReq.getPage());
        hashMap.put("pageSize",sendRedPacketReq.getPageSize());
        hashMap.put("content",sendListRespVOS);
        return RestResult.ok(hashMap);
    }

    @Override
    public RestResult edit(String redPacketId) {
        return RestResult.ok(redpacketRepository.findById(redPacketId).get());
    }

    /**
     * 返回红包记录
     * @param redpacketId
     * @return
     */
    @Override
    public RestResult detail(String redpacketId) {
        try {
            return getRedReceives(redpacketId);
        }catch (Exception e){
            log.error("红包记录接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }
    }

    /**
     * 点击打开红包之前查询红包信息
     * @param redMessageReq
     * @return
     */
    @Override
    public RestResult message( RedMessageReq redMessageReq) {
        try {
            RedPacket redPacket = redpacketRepository.findById(redMessageReq.getRedpacketId()).get();
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("status",redPacket.getRedStatus());
            if(redPacket.getCount().intValue()==redPacket.getReceiveCount().intValue()){
                redPacket.setRedStatus(RedStatusEnums.RECEIVE_END.getCode());
            }
            if(RedStatusEnums.TIME_OUT.getCode().intValue()!=redPacket.getRedStatus().intValue()&&
                    RedStatusEnums.GIVE_BACK.getCode().intValue()!=redPacket.getRedStatus().intValue()){
                RedReceive redReceive =redReceiveRepository.findByRedIdAndUserId(redMessageReq.getRedpacketId(),redMessageReq.getUserId());
                if(null!=redReceive){
                    hashMap.put("status", RedStatusEnums.RECEIVE.getCode());
                }
            }
            User user = userRepository.findByUid(redPacket.getSenderId());
            hashMap.put("senderName",user.getDisplayName());
            hashMap.put("senderPic",user.getPortrait());
            hashMap.put("senderId",redPacket.getSenderId());
            return RestResult.ok(hashMap);
        }catch (Exception e){
            log.error("查询红包信息接口错误",e);
            return RestResult.error(RestCodeEnums.ERROR_SERVER);
        }

    }

}
