package com.summit.service;

import com.summit.sdk.huawei.model.CameraUploadType;
import com.summit.sdk.huawei.model.LockProcessResultType;

/**
 * 门禁锁操作相关
 */
public interface AccessControlLockOperationService {


    /**
     * 新增门禁锁操作事件
     * @param lockCode 锁编码
     * @param type 操作类型
     * @param processResult  操作结果
     * @param failReason  失败信息描述 成功则传入null
     * @throws Exception
     */
    void insertAccessControlLockOperationEvent(String lockCode, CameraUploadType type, LockProcessResultType processResult, String failReason,Integer enterOrExit) throws Exception;
}
