package com.summit.utils;

import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.entity.LockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.FaceLibType;
import com.summit.service.impl.NBLockServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientFaceInfoCallbackImpl implements ClientFaceInfoCallback {

    @Autowired
    private NBLockServiceImpl unLockService;
    @Override
    public void invoke(FaceInfo faceInfo) {
        log.debug("============客户端调用开始=============");
        log.debug("设备IP:" + faceInfo.getDeviceIp());
        log.debug("名字:" + faceInfo.getName());
        log.debug("性别:{}", faceInfo.getGender().getGenderDescription());
        log.debug("生日:" + faceInfo.getBirthday());
        log.debug("省级:" + faceInfo.getProvince());
        log.debug("地市:" + faceInfo.getCity());
        log.debug("证件类型:{}", faceInfo.getCardType().getCardTypeDescription());
        log.debug("证件号:" + faceInfo.getCardId());
        log.debug("人脸匹配率:{}%", faceInfo.getFaceMatchRate());
        log.debug("名单库名称:{}", faceInfo.getFaceLibName());
        log.debug("名单库类型:{}", faceInfo.getFaceLibType().getFaceLibTypeDescription());
        FaceLibType faceLibType = faceInfo.getFaceLibType();
        if(faceLibType.equals(FaceLibType.FACE_LIB_WHITE)){
            RestfulEntityBySummit result = unLockService.toUnLock(new LockRequest("NB100001", "张三"));
            LockInfo lockInfo = result.getData() == null ? null : (LockInfo)result.getData();
            log.info("rmid={},type={},content={},objx={},time={}" ,
                    lockInfo.getRmid(),lockInfo.getType(),lockInfo.getContent(),lockInfo.getObjx(),lockInfo.getTime());
        }
    }


}
