package cn.wildfirechat.app;


import cn.wildfirechat.app.pojo.*;
import cn.wildfirechat.pojos.InputCreateDevice;
import org.springframework.web.multipart.MultipartFile;

public interface Service {
    RestResult sendCode(String mobile);
    RestResult login(String mobile, String code, String clientId, int platform);


    RestResult createPcSession(CreateSessionRequest request);
    RestResult loginWithSession(String token);

    RestResult scanPc(String token);
    RestResult confirmPc(ConfirmSessionRequest request);

    RestResult putGroupAnnouncement(GroupAnnouncementPojo request);
    RestResult getGroupAnnouncement(String groupId);

    RestResult saveUserLogs(String userId, MultipartFile file);

    RestResult getBetUrl();
    RestResult addDevice(InputCreateDevice createDevice);
    RestResult getDeviceList();
    RestResult payPasswordRest(PayPasswordRestRequest payPasswordRestRequest);
    RestResult payPassword(PayPasswordRequest passwordRequest);
    RestResult payPasswordVal(PayPasswordValRequest passwordRequest);
    RestResult userPassword(String id);
}
