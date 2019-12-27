package com.summit.service;

import com.summit.sdk.huawei.model.CameraUploadType;
import com.summit.sdk.huawei.model.LockProcessResultType;

/**
 * 门禁锁操作相关
 */
public interface AccessControlLockOperationService {


    /**
     * 新增门禁锁操作事件
     * @param lockCode
     * @param type
     * @param processResult
     * @param failReason
     * @throws Exception
     */
    void insertAccessControlLockOperationEvent(String lockCode, CameraUploadType type, LockProcessResultType processResult, String failReason) throws Exception;
}
