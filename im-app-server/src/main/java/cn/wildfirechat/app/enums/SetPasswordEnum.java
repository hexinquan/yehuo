package cn.wildfirechat.app.enums;

import lombok.Getter;

@Getter
public enum SetPasswordEnum {
    PASSWORD_TYPE_SET(1,"设置密码"),
    OLD_PASSWORD_SET(2,"原密码修改"),
    CODE_PASSWORD_SET(3,"验证码修改密码");
    private Integer code;
    private String name;

    SetPasswordEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

}
