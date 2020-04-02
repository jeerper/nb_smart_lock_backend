package com.summit.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.summit.MainAction;
import com.summit.common.util.ApplicationContextUtil;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.entity.FaceRecognitionInfo;
import com.summit.sdk.huawei.model.CameraUploadType;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.service.AccessControlLockOperationService;
import com.summit.util.AccCtrlProcessUtil;
import com.summit.util.CommonUtil;
import com.summit.util.FaceInfoContextHolder;
import com.summit.utils.ClientFaceInfoCallbackImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;


@Slf4j
@Service
public class AccessControlLockOperationServiceImpl implements AccessControlLockOperationService {

    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;


    @Override
    public void insertAccessControlLockOperationEvent(String lockCode, CameraUploadType type, LockProcessResultType processResult,
                                                      String failReason,Integer enterOrExit) throws Exception {
        try {
            Date date = new Date();
            String snapshotTime = CommonUtil.dateFormat.get().format(date);
            FaceRecognitionInfo faceRecognitionInfo= FaceInfoContextHolder.getFaceRecognitionInfo();
            String fileName = FileUtil.getName(faceRecognitionInfo.getFaceImagePath());

            //人脸扫描图片URL
            String facePanoramaUrl = new StringBuilder()
                    .append(MainAction.SnapshotFileName)
                    .append(CommonConstants.URL_SEPARATOR)
                    .append(snapshotTime)
                    .append(CommonConstants.URL_SEPARATOR)
                    .append(type)
                    .append(CommonConstants.URL_SEPARATOR)
                    .append(fileName)
                    .toString();
            String facePanoramaFilePath = SystemUtil.getUserInfo().getCurrentDir() + File.separator + facePanoramaUrl;
            FileUtil.copy(faceRecognitionInfo.getFaceImagePath(), facePanoramaFilePath, true);
            FileInfo facePanoramaFile = new FileInfo(snapshotTime + CommonConstants.FACE_PANORAMA_SUFFIX, facePanoramaUrl, "人脸全景图");
            FaceInfo faceInfo = faceInfoManagerDao.selectById(faceRecognitionInfo.getFaceId());

            AccCtrlProcess accCtrlProcess = accCtrlProcessUtil.getAccCtrlProcess(lockCode, faceInfo,faceRecognitionInfo.getScore(), type, facePanoramaFile, date, processResult,
                    failReason,enterOrExit);
            //在事务控制下插入门禁操作记录、门禁实时信息、告警
            ApplicationContextUtil.getBean(ClientFaceInfoCallbackImpl.class).insertData(type, accCtrlProcess);
        } catch (Exception e) {
            log.error("门禁锁操作事件入库失败", e);
            throw new Exception("门禁锁操作事件入库失败");
        }
    }
}
