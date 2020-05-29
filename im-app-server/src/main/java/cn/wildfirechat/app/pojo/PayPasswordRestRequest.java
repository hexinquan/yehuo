package cn.wildfirechat.app.pojo;

import lombok.Data;

@Data
public class PayPasswordRestRequest {
    private String mobile;
    private String oldPassword;
    private String newPassword;
    private String code;
}
