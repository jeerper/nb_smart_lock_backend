package com.summit.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.MainAction;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.repository.*;
import com.summit.entity.*;
import com.summit.sdk.huawei.model.CameraUploadType;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.service.AccessControlLockOperationService;
import com.summit.service.FaceInfoManagerService;
import com.summit.util.FaceInfoContextHolder;
import com.summit.util.JwtSettings;
import com.summit.utils.BaiduSdkClient;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 人脸识别操作接口
 *
 * @author 刘源
 */
@Slf4j
@Api(tags = "人脸识别操作接口")
@RestController
@RequestMapping("/not-auth/face-recognition")
public class FaceRecognitionController {


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
    @Autowired
    private FaceInfoManagerService faceInfoManagerService;

    @ApiOperation(value = "人脸扫描")
    @PostMapping(value = "/face-scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestfulEntityBySummit<String> faceScan(@ApiParam(value = "人脸图片", required = true)
                                                  @RequestPart("faceImageFile") MultipartFile faceImageFile) {
        try {
            byte[] faceFileByteArray = faceImageFile.getBytes();
            String faceFileStr = Base64.encode(faceFileByteArray);
            SearchFaceResult searchFaceResult = baiduSdkClient.searchFace(faceFileStr);
            String faceId = searchFaceResult.getFaceId();
            if (StrUtil.isBlank(faceId)) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "没有匹配到人脸", null);
            }
            FaceInfo faceInfo = faceInfoManagerDao.selectById(faceId);
            if (faceInfo == null) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸信息不存在", null);
            }
            if (faceInfo.getIsValidTime() == 1) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸信息已过期", null);
            }

            //生成token
            String token = Jwts.builder()
                    .setId(IdWorker.getIdStr())
                    .setSubject(faceInfo.getUserName())
                    .claim(MainAction.FACE_ID, faceId)
                    .signWith(SignatureAlgorithm.HS512, jwtSettings.getSecretKey())
                    .compact();

            String extName = FileUtil.extName(faceImageFile.getOriginalFilename());
            String filePath = new StringBuilder()
                    .append(SystemUtil.getUserInfo().getCurrentDir())
                    .append(File.separator)
                    .append(MainAction.SnapshotFileName)
                    .append(File.separator)
                    .append(MainAction.FaceRecognitionFileName)
                    .append(File.separator)
                    .append(IdWorker.getIdStr())
                    .append(StrUtil.DOT)
                    .append(extName)
                    .toString();

            FaceRecognitionInfo faceRecognitionInfo = new FaceRecognitionInfo();
            faceRecognitionInfo.setFaceId(faceId);
            faceRecognitionInfo.setFaceImagePath(filePath);
            faceRecognitionInfo.setScore(searchFaceResult.getScore());
            genericRedisTemplate.opsForValue().set(MainAction.FACE_AUTH_CACHE_PREFIX + token, faceRecognitionInfo, jwtSettings.getExpireLength(),
                    TimeUnit.MINUTES);

            FileUtil.writeBytes(faceFileByteArray, filePath);
            return ResultBuilder.buildSuccess(token);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸扫描信息上传失败", null);
        }
    }

    @ApiOperation(value = "通过人脸扫描获取智能锁编码解析")
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
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "通过人脸扫描获取锁编码解析失败", null);
        }
    }

    @ApiOperation(value = "通过人脸扫描获取智能锁密码")
    @PostMapping(value = "/lock-code-password")
    public RestfulEntityBySummit<LockInfo> lockCodePassword(@RequestBody GetLockCodeParam getLockCodeParam) {
        try {
            if (getLockCodeParam ==null){
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "获取智能锁密码参数为空", null);
            }
            String lockCode = getLockCodeParam.getLockCode();
            Integer enterOrExit = getLockCodeParam.getEnterOrExit();
            int count = faceInfoAccCtrlDao.selectCountByFaceIdAndLockCode(FaceInfoContextHolder.getFaceRecognitionInfo().getFaceId(), lockCode);
            if (count < 1) {
                //没有操作权限时需要执行报警操作
                String failReason = "没有操作该门禁锁的权限";
                accessControlLockOperationService.insertAccessControlLockOperationEvent(lockCode, CameraUploadType.Illegal_Alarm,
                        LockProcessResultType.Failure, failReason,enterOrExit);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, failReason, null);
            }
            return ResultBuilder.buildSuccess(lockInfoDao.selectLockPassWordByLockCode(lockCode));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "通过人脸扫描获取智能锁密码失败", null);
        }
    }

    @ApiOperation(value = "通过人脸扫描提交智能锁开锁结果")
    @PostMapping(value = "/unlock-result")
    public RestfulEntityBySummit<String> unlockResult(@RequestBody UnlockResultInfo unlockResultInfo) {
        try {
            String lockCode = unlockResultInfo.getLockCode();
            if (StrUtil.isBlank(lockCode)) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "锁编码为空", null);
            }
            int count = faceInfoAccCtrlDao.selectCountByFaceIdAndLockCode(FaceInfoContextHolder.getFaceRecognitionInfo().getFaceId(), lockCode);
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
                boolean acquire = lock.tryLock(2, 20, TimeUnit.SECONDS);//获得
                if (acquire) {
                    try {
                        int lockCount = lockInfoDao.selectCount(Wrappers.<LockInfo>lambdaQuery()
                                .eq(LockInfo::getLockCode, lockCode)
                                .eq(LockInfo::getCurrentPassword, unlockResultInfo.getCurrentPassword())
                                .eq(LockInfo::getNewPassword, unlockResultInfo.getNewPassword()));
                        if (lockCount == 0) {
                            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "开锁信息不匹配，无法确认开锁结果的真伪", null);
                        }
                      /*  lockInfoDao.update(null, Wrappers.<LockInfo>lambdaUpdate()
                                .set(LockInfo::getCurrentPassword, unlockResultInfo.getNewPassword())
                                .set(LockInfo::getNewPassword, RandomUtil.randomStringUpper(6))
                                .eq(LockInfo::getLockCode, lockCode)
                                .eq(LockInfo::getCurrentPassword, unlockResultInfo.getCurrentPassword())
                                .eq(LockInfo::getNewPassword, unlockResultInfo.getNewPassword()));*/
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
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "通过人脸扫描提交智能锁开锁结果失败", null);
        }
    }


    @ApiOperation(value = "App上传人脸图片认证通过后获取人脸信息")
    @GetMapping(value = "/getFaceInfoByApp")
    public RestfulEntityBySummit<FaceInfo> getFaceInfo() throws Exception {
        FaceRecognitionInfo faceRecognitionInfo = FaceInfoContextHolder.getFaceRecognitionInfo();
        FaceInfo faceInfo=null;
        try {
            if (faceRecognitionInfo ==null){
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "没有上传人脸照片", null);
            }
            String faceId = faceRecognitionInfo.getFaceId();
            if (StrUtil.isBlank(faceId)) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "上传人脸照片Id为空", null);
            }
            faceInfo=faceInfoManagerDao.selectFaceInfoById(faceId);
        } catch (Exception e) {
            log.error("获取人脸信息失败",e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "获取人脸信息失败", faceInfo);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "获取人脸信息成功", faceInfo);

    }
}
