package cn.wildfirechat.app.pojo;

import lombok.Data;

@Data
public class PayPasswordRequest {
    private String mobile;
    private String newPassword1;
    private String newPassword2;
    private String oldPassword;
    private String code;
    private int type;
    private String userId;
}
