package com.summit.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.MainAction;
import com.summit.common.Common;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.repository.*;
import com.summit.entity.GetLockCodeParam;
import com.summit.entity.UnlockResultEnum;
import com.summit.entity.UnlockResultInfo;
import com.summit.sdk.huawei.model.CameraUploadType;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.service.AccessControlLockOperationService;
import com.summit.util.JwtSettings;
import com.summit.utils.BaiduSdkClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "登录app人脸识别操作接口")
@RestController
@RequestMapping("/face-recognition")
public class LoginFaceRecognitionController {

    @Autowired
    JwtSettings jwtSettings;
    @Autowired
    RedisTemplate<String, Object> genericRedisTemplate;
    @Autowired
    AccessControlLockOperationService accessControlLockOperationService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    private BaiduSdkClient baiduSdkClient;
    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;
    @Autowired
    private FaceInfoAccCtrlDao faceInfoAccCtrlDao;
    @Autowired
    private AccessControlDao accessControlDao;
    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;
    @Autowired
    private LockInfoDao lockInfoDao;



    @ApiOperation(value = "智能锁编码解析")
    @PostMapping(value = "/lock-code-parse")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lockCodes", value = "智能锁编码", paramType = "body", required = true),
    })
    public RestfulEntityBySummit<List<LockInfo>> lockCodeParse(@RequestBody List<String> lockCodes) {
        try {
            if (lockCodes.size() == 0) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "锁列表为空", null);
            }
            return ResultBuilder.buildSuccess(lockInfoDao.selectByLockCodes(lockCodes));
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸扫描信息上传失败", null);
        }
    }

    @ApiOperation(value = "获取智能锁密码")
    @PostMapping(value = "/lock-code-password")
    public RestfulEntityBySummit<LockInfo> lockCodePassword(@RequestBody GetLockCodeParam getLockCodeParam) {
        try {
            if (getLockCodeParam ==null){
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "获取智能锁密码参数为空", null);
            }
            if (Common.getLogUser() ==null && StrUtil.isNotBlank(Common.getLogUser().getUserName())) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "用户名不存在", null);
            }
            String lockCode = getLockCodeParam.getLockCode();
            Integer enterOrExit = getLockCodeParam.getEnterOrExit();
            String userName = Common.getLogUser().getUserName();
            List rolesList = null;
            if (Common.getLogUser() != null && Common.getLogUser().getRoles()!=null) {
                rolesList = Arrays.asList(Common.getLogUser().getRoles());
            }
            int count = faceInfoAccCtrlDao.selectCountByUserNameAndLockCode(userName,lockCode);
            if (count < 1 && rolesList !=null && !rolesList.contains("ROLE_SUPERUSER")) {
                //没有操作权限时需要执行报警操作
                String failReason = "没有操作该门禁锁的权限";
                accessControlLockOperationService.insertAccessControlLockOperationEvent(lockCode, CameraUploadType.Illegal_Alarm,
                        LockProcessResultType.Failure, failReason,enterOrExit);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, failReason, null);
            }
            return ResultBuilder.buildSuccess(lockInfoDao.selectLockPassWordByLockCode(lockCode));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸扫描信息上传失败", null);
        }
    }

    @ApiOperation(value = "提交智能锁开锁结果")
    @PostMapping(value = "/unlock-result")
    public RestfulEntityBySummit<String> unlockResult(@RequestBody UnlockResultInfo unlockResultInfo) {
        try {
            String lockCode = unlockResultInfo.getLockCode();
            if (StrUtil.isBlank(lockCode)) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "锁编码为空", null);
            }
            if (Common.getLogUser() ==null && StrUtil.isNotBlank(Common.getLogUser().getUserName())) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "用户名不存在", null);
            }
            String userName = Common.getLogUser().getUserName();
            int count = faceInfoAccCtrlDao.selectCountByUserNameAndLockCode(userName,lockCode);
            if (count < 1) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "没有操作该门禁锁的权限", null);
            }
            LockProcessResultType processResult;
            Integer enterOrExit = unlockResultInfo.getEnterOrExit();
            String failReason = null;
            CameraUploadType cameraUploadType;
            if (unlockResultInfo.isSuccess()) {
                processResult = LockProcessResultType.Success;
                cameraUploadType = CameraUploadType.Unlock;
                //开启分布式锁，然后再更新数据库中的密码
                RLock lock = redissonClient.getLock(MainAction.CHANGE_LOCK_PSW_LOCK_PREFIX + lockCode);
                boolean acquire = lock.tryLock(2, 20, TimeUnit.SECONDS);
                if (acquire) {
                    try {
                        int lockCount = lockInfoDao.selectCount(Wrappers.<LockInfo>lambdaQuery()
                                .eq(LockInfo::getLockCode, lockCode)
                                .eq(LockInfo::getCurrentPassword, unlockResultInfo.getCurrentPassword())
                                .eq(LockInfo::getNewPassword, unlockResultInfo.getNewPassword()));
                        if (lockCount == 0) {
                            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "开锁信息不匹配，无法确认开锁结果的真伪", null);
                        }
                        lockInfoDao.update(null, Wrappers.<LockInfo>lambdaUpdate()
                                .set(LockInfo::getCurrentPassword, unlockResultInfo.getNewPassword())
                                .set(LockInfo::getNewPassword, RandomUtil.randomStringUpper(6))
                                .eq(LockInfo::getLockCode, lockCode)
                                .eq(LockInfo::getCurrentPassword, unlockResultInfo.getCurrentPassword())
                                .eq(LockInfo::getNewPassword, unlockResultInfo.getNewPassword()));
                    } finally {
                        try {
                            lock.unlock();
                        } catch (Exception er) {
                            log.error("当前锁不存在");
                        }
                    }
                }
            } else {
                processResult = LockProcessResultType.Failure;
                cameraUploadType = CameraUploadType.Illegal_Alarm;
                failReason = UnlockResultEnum.codeOf(unlockResultInfo.getResult()).getDescription();
            }
            accessControlLockOperationService.insertAccessControlLockOperationEvent(lockCode, cameraUploadType, processResult, failReason,enterOrExit);
            return ResultBuilder.buildSuccess();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "开锁结果提交失败", null);
        }
    }

}
