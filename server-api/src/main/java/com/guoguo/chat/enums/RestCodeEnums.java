package com.guoguo.chat.enums;

public enum RestCodeEnums {
    SUCCESS(200, "success"),
    ERROR_SERVER(500,"系统错误"),
    ERROR_RED_GROUP(501,"群聊拼手气红包暂未开发完成"),
    ERROR_RED_AMOUNT(5001, "红包金额有误"),
    ERROR_RED_COUNT(5002, "红包个数有误"),
    ERROR_RED_NOTFOUND(5003,"红包不存在"),
    ERROR_RED_TIMEOUT(5004,"红包已过期"),
    ERROR_RED_NOTRECEIVE(5005,"无权限领取此红包"),
    ERROR_RED_GIVBACK(5005,"红包已退还"),
    ERROR_USER_NOT_FOUND(6001, "用户不存在"),
    ERROR_USER_BALANCE(6002, "余额不足"),
    MESSAGE_RED_RECEIVED(6003,"红包已领取"),
    MESSAGE_RED_NOTLUCK(6004,"手气慢了,红包已领完"),
    MESSAGE_RED_MAX(6005,"红包金额超过最大金额限制"),
    MESSAGE_RED_MIX(6006,"红包金额小于最低金额限制"),
    ERROR_RED_USER_RECEIVE(6001, "不能领取自己发的红包"),
    ERROR_RCHARGE_AMOUNT(7001,"充值金额有误"),
    ERROR_WITHDRAW_PLATFROM_NOT(7002,"提现平台不存在"),
    ERROR_WITHDRAW_AMOUNT_FAIL(7003,"最底兑现提额错误"),
    ERROR_CODE_INCORRECT(6, "验证码错误"),
    ERROR_SERVER_CONFIG_ERROR(7, "服务器配置错误"),
    ERROR_SESSION_EXPIRED(8, "会话不存在或已过期"),
    ERROR_SESSION_NOT_VERIFIED(9, "会话没有验证"),
    ERROR_SESSION_NOT_SCANED(10, "会话没有被扫码"),
    ERROR_SERVER_NOT_IMPLEMENT(11, "功能没有实现"),
    ERROR_GROUP_ANNOUNCEMENT_NOT_EXIST(12, "群公告不存在"),
    ERROR_NOT_LOGIN(13, "没有登录");
    public int code;
    public String msg;

    RestCodeEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
