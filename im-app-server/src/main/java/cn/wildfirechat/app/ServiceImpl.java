package cn.wildfirechat.app;


import cn.wildfirechat.app.enums.SetPasswordEnum;
import cn.wildfirechat.app.jpa.Announcement;
import cn.wildfirechat.app.jpa.AnnouncementRepository;
import cn.wildfirechat.app.jpa.User;
import cn.wildfirechat.app.jpa.UserRepository;
import cn.wildfirechat.app.model.PCSession;
import cn.wildfirechat.app.model.Record;
import cn.wildfirechat.app.pojo.*;
import cn.wildfirechat.app.shiro.AuthDataSource;
import cn.wildfirechat.app.shiro.TokenAuthenticationToken;
import cn.wildfirechat.app.sms.SmsService;
import cn.wildfirechat.app.tools.UUIDUserNameGenerator;
import cn.wildfirechat.app.tools.Utils;
import cn.wildfirechat.app.utils.RedisUtil;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.sdk.ChatConfig;
import cn.wildfirechat.sdk.GroupAdmin;
import cn.wildfirechat.sdk.MessageAdmin;
import cn.wildfirechat.sdk.UserAdmin;
import cn.wildfirechat.sdk.model.IMResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static cn.wildfirechat.app.RestResult.RestCode.*;


@org.springframework.stereotype.Service
public class ServiceImpl implements Service {
    static class Count {
        long count;
        long startTime;

        void reset() {
            count = 1;
            startTime = System.currentTimeMillis();
        }

        boolean increaseAndCheck() {
            long now = System.currentTimeMillis();
            if (now - startTime > 86400000) {
                reset();
                return true;
            }
            count++;
            if (count > 10) {
                return false;
            }
            return true;
        }
    }

    private static ConcurrentHashMap<String, Record> mRecords = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, PCSession> mPCSession = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Count> mCounts = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(ServiceImpl.class);

    @Autowired
    private SmsService smsService;

    @Autowired
    private IMConfig mIMConfig;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Value("${sms.super_code}")
    private String superCode;

    @Value("${logs.user_logs_path}")
    private String userLogPath;

    @Value("${bet_url}")
    private String betUrl;

    @Autowired
    private UUIDUserNameGenerator userNameGenerator;

    @Autowired
    private AuthDataSource authDataSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @PostConstruct
    private void init() {
        ChatConfig.initAdmin(mIMConfig.admin_url, mIMConfig.admin_secret);
    }

    @Override
    public RestResult sendCode(String mobile) {
        try {
            String code = Utils.getRandomCode(4);
            RestResult.RestCode restCode = authDataSource.insertRecord(mobile, code);
            RedisUtil redisUtil = new RedisUtil(redisTemplate);
            redisUtil.set(mobile,code,60);
            if (restCode != SUCCESS) {
                return RestResult.error(restCode);
            }
            restCode = smsService.sendCode(mobile, code);
            if (restCode == RestResult.RestCode.SUCCESS) {
                return RestResult.ok(restCode);
            } else {
                authDataSource.clearRecode(mobile);
                return RestResult.error(restCode);
            }
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
            authDataSource.clearRecode(mobile);
        }
        return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
    }

    @Override
    public RestResult login(String mobile, String code, String clientId, int platform) {
        Subject subject = SecurityUtils.getSubject();
        // 在认证提交前准备 token（令牌）
        UsernamePasswordToken token = new UsernamePasswordToken(mobile, code);
        // 执行认证登陆
        try {
            subject.login(token);
        } catch (UnknownAccountException uae) {
            return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
        } catch (IncorrectCredentialsException ice) {
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        } catch (LockedAccountException lae) {
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        } catch (ExcessiveAttemptsException eae) {
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        } catch (AuthenticationException ae) {
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        }
        if (subject.isAuthenticated()) {
            long timeout = subject.getSession().getTimeout();
            LOG.info("Login success " + timeout);
        } else {
            token.clear();
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        }
        try {
            //使用电话号码查询用户信息。
            IMResult<InputOutputUserInfo> userResult = UserAdmin.getUserByMobile(mobile);

            //如果用户信息不存在，创建用户
            InputOutputUserInfo user;
            boolean isNewUser = false;
            if (userResult.getErrorCode() == ErrorCode.ERROR_CODE_NOT_EXIST) {
                LOG.info("User not exist, try to create");
                user = new InputOutputUserInfo();
                String userName = userNameGenerator.getUserName(mobile);
                user.setName(userName);
                if (mIMConfig.use_random_name) {
                    String displayName = "用户" + (int) (Math.random() * 10000);
                    user.setDisplayName(displayName);
                } else {
                    user.setDisplayName(mobile);
                }
                user.setMobile(mobile);
                IMResult<OutputCreateUser> userIdResult = UserAdmin.createUser(user);
                if (userIdResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                    user.setUserId(userIdResult.getResult().getUserId());
                    isNewUser = true;
                } else {
                    LOG.info("Create user failure {}", userIdResult.code);
                    return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
                }
            } else if(userResult.getCode() != 0){
                LOG.error("Get user failure {}", userResult.code);
                return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
            } else {
                user = userResult.getResult();
            }

            //使用用户id获取token
            IMResult<OutputGetIMTokenData> tokenResult = UserAdmin.getUserToken(user.getUserId(), clientId, platform);
            if (tokenResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
                LOG.error("Get user failure {}", tokenResult.code);
                return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
            }

            subject.getSession().setAttribute("userId", user.getUserId());

            //返回用户id，token和是否新建
            LoginResponse response = new LoginResponse();
            response.setUserId(user.getUserId());
            response.setToken(tokenResult.getResult().getToken());
            response.setRegister(isNewUser);

            if (isNewUser) {
                if (!StringUtils.isEmpty(mIMConfig.welcome_for_new_user)) {
                    sendTextMessage(user.getUserId(), mIMConfig.welcome_for_new_user);
                }
            } else {
                if (!StringUtils.isEmpty(mIMConfig.welcome_for_back_user)) {
                    sendTextMessage(user.getUserId(), mIMConfig.welcome_for_back_user);
                }
            }

            return RestResult.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Exception happens {}", e);
            return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
        }
    }

    private void sendTextMessage(String toUser, String text) {
        Conversation conversation = new Conversation();
        conversation.setTarget(toUser);
        conversation.setType(ProtoConstants.ConversationType.ConversationType_Private);
        MessagePayload payload = new MessagePayload();
        payload.setType(1);
        payload.setSearchableContent(text);


        try {
            IMResult<SendMessageResult> resultSendMessage = MessageAdmin.sendMessage("admin", conversation, payload);
            if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                LOG.info("send message success");
            } else {
                LOG.error("send message error {}", resultSendMessage != null ? resultSendMessage.getErrorCode().code : "unknown");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("send message error {}", e.getLocalizedMessage());
        }

    }


    @Override
    public RestResult createPcSession(CreateSessionRequest request) {
        PCSession session = authDataSource.createSession(request.getClientId(), request.getToken(), request.getPlatform());
        SessionOutput output = session.toOutput();
        return RestResult.ok(output);
    }

    @Override
    public RestResult loginWithSession(String token) {
        Subject subject = SecurityUtils.getSubject();
        // 在认证提交前准备 token（令牌）
        // comment start 如果确定登录不成功，就不通过Shiro尝试登录了
        TokenAuthenticationToken tt = new TokenAuthenticationToken(token);
        RestResult.RestCode restCode = authDataSource.checkPcSession(token);
        if (restCode != SUCCESS) {
            return RestResult.error(restCode);
        }
        // comment end

        // 执行认证登陆
        // comment start 由于PC端登录之后，可以请求app server创建群公告等。为了保证安全, PC端登录时，也需要在app server创建session。
        try {
            subject.login(tt);
        } catch (UnknownAccountException uae) {
            return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
        } catch (IncorrectCredentialsException ice) {
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        } catch (LockedAccountException lae) {
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        } catch (ExcessiveAttemptsException eae) {
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        } catch (AuthenticationException ae) {
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        }
        if (subject.isAuthenticated()) {
            LOG.info("Login success");
        } else {
            return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
        }
        // comment end

        PCSession session = authDataSource.getSession(token, true);
        if (session == null) {
            subject.logout();
            return RestResult.error(RestResult.RestCode.ERROR_CODE_EXPIRED);
        }
        subject.getSession().setAttribute("userId", session.getConfirmedUserId());

        try {
            //使用用户id获取token
            IMResult<OutputGetIMTokenData> tokenResult = UserAdmin.getUserToken(session.getConfirmedUserId(), session.getClientId(), session.getPlatform());
            if (tokenResult.getCode() != 0) {
                LOG.error("Get user failure {}", tokenResult.code);
                subject.logout();
                return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
            }
            //返回用户id，token和是否新建
            LoginResponse response = new LoginResponse();
            response.setUserId(session.getConfirmedUserId());
            response.setToken(tokenResult.getResult().getToken());
            return RestResult.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            subject.logout();
            return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
        }
    }

    @Override
    public RestResult scanPc(String token) {
        PCSession session = mPCSession.get(token);
        if (session != null) {
            SessionOutput output = session.toOutput();
            if (output.getExpired() > 0) {
                session.setStatus(1);
                output.setStatus(1);
                return RestResult.ok(output);
            } else {
                return RestResult.error(RestResult.RestCode.ERROR_SESSION_EXPIRED);
            }
        } else {
            return RestResult.error(RestResult.RestCode.ERROR_SESSION_EXPIRED);
        }
    }

    @Override
    public RestResult confirmPc(ConfirmSessionRequest request) {
        PCSession session = mPCSession.get(request.getToken());
        if (session != null) {
            SessionOutput output = session.toOutput();
            if (output.getExpired() > 0) {
                //todo 检查IMtoken，确认用户id不是冒充的
                session.setStatus(2);
                output.setStatus(2);
                session.setConfirmedUserId(request.getUser_id());
                return RestResult.ok(output);
            } else {
                return RestResult.error(RestResult.RestCode.ERROR_SESSION_EXPIRED);
            }
        } else {
            return RestResult.error(RestResult.RestCode.ERROR_SESSION_EXPIRED);
        }
    }

    @Override
    public RestResult getGroupAnnouncement(String groupId) {
        Optional<Announcement>  announcement = announcementRepository.findById(groupId);
        if (announcement.isPresent()){
            GroupAnnouncementPojo pojo = new GroupAnnouncementPojo();
            pojo.groupId = announcement.get().getGroupId();
            pojo.author = announcement.get().getAuthor();
            pojo.text = announcement.get().getAnnouncement();
            pojo.timestamp = announcement.get().getTimestamp();
            return RestResult.ok(pojo);
        } else {
            return RestResult.error(ERROR_GROUP_ANNOUNCEMENT_NOT_EXIST);
        }
    }

    @Override
    public RestResult putGroupAnnouncement(GroupAnnouncementPojo request) {
        if (!StringUtils.isEmpty(request.text)) {
            Conversation conversation = new Conversation();
            conversation.setTarget(request.groupId);
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Group);
            MessagePayload payload = new MessagePayload();
            payload.setType(1);
            payload.setSearchableContent("@所有人 " + request.text);
            payload.setMentionedType(2);


            try {
                IMResult<SendMessageResult> resultSendMessage = MessageAdmin.sendMessage(request.author, conversation, payload);
                if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                    LOG.info("send message success");
                } else {
                    LOG.error("send message error {}", resultSendMessage != null ? resultSendMessage.getErrorCode().code : "unknown");
                    return RestResult.error(ERROR_SERVER_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("send message error {}", e.getLocalizedMessage());
                return RestResult.error(ERROR_SERVER_ERROR);
            }
        }

        Announcement announcement = new Announcement();
        announcement.setGroupId(request.groupId);
        announcement.setAuthor(request.author);
        announcement.setAnnouncement(request.text);
        request.timestamp = System.currentTimeMillis();
        announcement.setTimestamp(request.timestamp);

        announcementRepository.save(announcement);
        return RestResult.ok(request);
    }

    @Override
    public RestResult saveUserLogs(String userId, MultipartFile file) {
        File localFile = new File(userLogPath, userId + "_" + file.getOriginalFilename());

        try {
            file.transferTo(localFile);
        } catch (IOException e) {
            e.printStackTrace();
            return RestResult.error(ERROR_SERVER_ERROR);
        }

        return RestResult.ok(null);
    }

    @Override
    public RestResult addDevice(InputCreateDevice createDevice) {
        try {
            Subject subject = SecurityUtils.getSubject();
            String userId = (String)subject.getSession().getAttribute("userId");

            if (!StringUtils.isEmpty(createDevice.getDeviceId())) {
                IMResult<OutputDevice> outputDeviceIMResult = UserAdmin.getDevice(createDevice.getDeviceId());
                if (outputDeviceIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                    if (!createDevice.getOwners().contains(userId)) {
                        return RestResult.error(ERROR_NO_RIGHT);
                    }
                } else if (outputDeviceIMResult.getErrorCode() != ErrorCode.ERROR_CODE_NOT_EXIST) {
                    return RestResult.error(ERROR_SERVER_ERROR);
                }
            }

            IMResult<OutputCreateDevice> result = UserAdmin.createOrUpdateDevice(createDevice);
            if (result!= null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                return RestResult.ok(result.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResult.error(ERROR_SERVER_ERROR);
    }

    @Override
    public RestResult getDeviceList() {
        Subject subject = SecurityUtils.getSubject();
        String userId = (String)subject.getSession().getAttribute("userId");
        try {
            IMResult<OutputDeviceList> imResult = UserAdmin.getUserDevices(userId);
            if (imResult != null && imResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                return RestResult.ok(imResult.getResult().getDevices());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResult.error(ERROR_SERVER_ERROR);
    }

    /**
     * 支付密码
     * @param request
     * @return
     */
    @Override
    public RestResult payPassword(PayPasswordRequest request) {
        try{
            RedisUtil redisUtil = new RedisUtil(redisTemplate);
            String cacheCode = String.valueOf(redisUtil.get(request.getMobile()));
                if(StringUtils.isEmpty(request.getNewPassword1())||request.getNewPassword1().length()<6){
                    return RestResult.error(ERROR_PASSWORD_LEN);
                }
                if(StringUtils.isEmpty(request.getNewPassword2())||request.getNewPassword2().length()<6){
                    return RestResult.error(ERROR_PASSWORD_LEN);
                }
                if(SetPasswordEnum.PASSWORD_TYPE_SET.getCode().intValue()==request.getType()){
                    LOG.info("缓存验证码:"+cacheCode+"  传入验证码:"+request.getCode());
                    if(!cacheCode.equals(request.getCode())){
                        return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
                    }
                    User user = userRepository.findByUid(request.getUserId());
                    if(user!=null){
                        if(!request.getNewPassword1().equals(request.getNewPassword2())){
                            return RestResult.error(ERROR_PASSWORD_COF);
                        }
                        user.setPayPassword(request.getNewPassword2());
                        userRepository.saveAndFlush(user);
                        return RestResult.ok(0);
                    }
                }
                if(SetPasswordEnum.OLD_PASSWORD_SET.getCode().intValue()==request.getType()){
                    if(StringUtils.isEmpty(request.getOldPassword())||request.getOldPassword().length()<6){
                        return RestResult.error(ERROR_PASSWORD_LEN);
                    }
                    User user = userRepository.findByUid(request.getUserId());
                    if(user!=null){
                        if(!user.getPayPassword().equals(request.getOldPassword())){
                            return RestResult.error(ERROR_PASSWORD_OLD);
                        }
                        if(!request.getNewPassword1().equals(request.getNewPassword2())){
                            return RestResult.error(ERROR_PASSWORD_COF);
                        }
                        if(user.getPayPassword().equals(request.getNewPassword2())||user.getPayPassword().equals(request.getNewPassword1())){
                            return RestResult.error(ERROR_PASSWORD_DIC);
                        }
                        user.setPayPassword(request.getNewPassword2());
                        userRepository.saveAndFlush(user);
                        return RestResult.ok(0);
                    }
                }
                if(SetPasswordEnum.CODE_PASSWORD_SET.getCode().intValue()==request.getType()){
                    LOG.info("缓存验证码:"+cacheCode+"  传入验证码:"+request.getCode());
                    if(!cacheCode.equals(request.getCode())){
                        return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
                    }
                    User user = userRepository.findByUid(request.getUserId());
                    if(user!=null){
                        if(!request.getNewPassword1().equals(request.getNewPassword2())){
                            return RestResult.error(ERROR_PASSWORD_COF);
                        }
                        if(user.getPayPassword().equals(request.getNewPassword2())||user.getPayPassword().equals(request.getNewPassword1())){
                            return RestResult.error(ERROR_PASSWORD_DIC);
                        }
                        user.setPayPassword(request.getNewPassword2());
                        userRepository.saveAndFlush(user);
                        return RestResult.ok(0);
                    }
                }
            return RestResult.error(ERROR_SERVER_ERROR);
        }catch (Exception e){
            e.printStackTrace();
            return RestResult.error(ERROR_SERVER_ERROR);
        }
    }

    /**
     * 支付密码验证
     * @param passwordRequest
     * @return
     */
    @Override
    public RestResult payPasswordVal(PayPasswordValRequest passwordRequest) {
        try {
            if(org.apache.commons.lang3.StringUtils.isBlank(passwordRequest.getPassword())){
                return  RestResult.error(ERROR_PARAM_FAL);
            }
            User user = userRepository.findByUid(passwordRequest.getId());
            if(user==null){
                return  RestResult.error(ERROR_USER_ERROR);
            }
            if (!passwordRequest.getPassword().equals(user.getPayPassword())) {
                return RestResult.error(ERROR_PASSWORD_FAL);
            }
            return RestResult.ok("验证成功");
        }catch (Exception e){
            LOG.error("验证密码错误");
            e.printStackTrace();
            return RestResult.error(ERROR_SERVER_ERROR);
        }
    }

    /**
     * 用户支付密码状态
     * @param id
     * @return
     */
    @Override
    public RestResult userPassword(String id) {
        try {
            HashMap<String,Object> map = new HashMap<>();
            User user = userRepository.findByUid(id);
            if(null==user){
                return RestResult.error(ERROR_USER_ERROR);
            }
            map.put("status",0);
            if(user!=null){
                if(org.apache.commons.lang3.StringUtils.isNotBlank(user.getPayPassword())){
                    map.put("status",2);
                }else {
                    map.put("status",1);
                }
            }
            return RestResult.ok(map);
        }catch (Exception e){
            e.printStackTrace();
            return RestResult.error(ERROR_SERVER_ERROR);
        }
    }

    /**
     * 支付密码重置
     * @param payPasswordRestRequest
     * @return
     */
    @Override
    public RestResult payPasswordRest(PayPasswordRestRequest payPasswordRestRequest) {
        try{
            Subject subject = SecurityUtils.getSubject();
            String userId = (String)subject.getSession().getAttribute("userId");
            Optional<User> user = userRepository.findById(Integer.valueOf(userId));
            if(org.apache.commons.lang3.StringUtils.isNotBlank(payPasswordRestRequest.getCode())){
                // 在认证提交前准备 token（令牌）
                UsernamePasswordToken token = new UsernamePasswordToken(payPasswordRestRequest.getMobile(), payPasswordRestRequest.getCode());
                // 执行认证登陆
                try {
                    subject.login(token);
                } catch (UnknownAccountException uae) {
                    return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
                } catch (IncorrectCredentialsException ice) {
                    return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
                } catch (LockedAccountException lae) {
                    return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
                } catch (ExcessiveAttemptsException eae) {
                    return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
                } catch (AuthenticationException ae) {
                    return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
                }
                if (subject.isAuthenticated()) {
                    long timeout = subject.getSession().getTimeout();
                    LOG.info("code success " + timeout);
                } else {
                    token.clear();
                    return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
                }
                if(payPasswordRestRequest.getNewPassword().length()<6||payPasswordRestRequest.getNewPassword().length()>6){
                    return RestResult.error(ERROR_PASSWORD_LEN);
                }
                if(payPasswordRestRequest.getOldPassword().length()<6||payPasswordRestRequest.getOldPassword().length()>6){
                    return RestResult.error(ERROR_PASSWORD_LEN);
                }

            }
            if(payPasswordRestRequest.getNewPassword().length()<6||payPasswordRestRequest.getNewPassword().length()>6){
                return RestResult.error(ERROR_PASSWORD_LEN);
            }
            if(!user.isPresent()){
                User u = user.get();
                if(!u.getPayPassword().equals(payPasswordRestRequest.getOldPassword())){
                    return RestResult.error(ERROR_PASSWORD_OLD);
                }
                u.setPayPassword(payPasswordRestRequest.getNewPassword());
                userRepository.saveAndFlush(u);
                return RestResult.ok(null);
            }
            return RestResult.error(ERROR_SERVER_ERROR);
        }catch (Exception e){
            e.printStackTrace();
            return RestResult.error(ERROR_SERVER_ERROR);
        }
    }

    @Override
    public RestResult getBetUrl() {
        GetBetUrlResponse response = new GetBetUrlResponse();
        response.setBetUrl(betUrl);
        return RestResult.ok(response);
    }
}
