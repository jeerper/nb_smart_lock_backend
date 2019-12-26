package com.summit.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.summit.MainAction;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ApplicationContextUtil;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.FaceInfoAccCtrlDao;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.entity.FaceRecognitionInfo;
import com.summit.sdk.huawei.model.CameraUploadType;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.sdk.huawei.model.LockProcessType;
import com.summit.util.AccCtrlProcessUtil;
import com.summit.util.CommonUtil;
import com.summit.util.FaceInfoContextHolder;
import com.summit.util.JwtSettings;
import com.summit.utils.BaiduSdkClient;
import com.summit.utils.ClientFaceInfoCallbackImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
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
    private AccCtrlProcessUtil accCtrlProcessUtil;


    @ApiOperation(value = "人脸扫描")
    @PostMapping(value = "/face-scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestfulEntityBySummit<String> faceScan(@ApiParam(value = "人脸图片", required = true)
                                                  @RequestPart("faceImageFile") MultipartFile faceImageFile) {
        try {
            byte[] faceFileByteArray = faceImageFile.getBytes();
            String faceFileStr = Base64.encode(faceFileByteArray);
            String faceId = baiduSdkClient.searchFace(faceFileStr);
            if (StrUtil.isBlank(faceId)) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "没有匹配到人脸", null);
            }
            FaceInfo faceInfo = faceInfoManagerDao.selectById(faceId);
            if (faceInfo == null) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸信息不存在", null);
            }
            if(faceInfo.getIsValidTime()==1){
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸信息已过期", null);
            }

            //生成token
            String token = Jwts.builder()
                    .setId(IdWorker.getIdStr())
                    .setSubject(faceInfo.getUserName())
                    .claim(MainAction.FACE_ID, faceId)
                    .signWith(SignatureAlgorithm.HS512, jwtSettings.getSecretKey())
                    .compact();

            String extName=FileUtil.extName(faceImageFile.getOriginalFilename());
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

            FaceRecognitionInfo faceRecognitionInfo=new FaceRecognitionInfo();
            faceRecognitionInfo.setFaceId(faceId);
            faceRecognitionInfo.setFaceImagePath(filePath);

            genericRedisTemplate.opsForValue().set(MainAction.FACE_AUTH_CACHE_PREFIX + token, faceRecognitionInfo, jwtSettings.getExpireLength(), TimeUnit.MINUTES);

            FileUtil.writeBytes(faceFileByteArray,filePath);
            return ResultBuilder.buildSuccess(token);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸扫描信息上传失败", null);
        }
    }

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
    @GetMapping(value = "/lock-code-password/{lockCode}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lockCode", value = "智能锁编码", paramType = "path", required = true),
    })
    public RestfulEntityBySummit<LockInfo> lockCodePassword(@PathVariable String lockCode) {
        try {
            if (StrUtil.isBlank(lockCode)) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "锁编码为空", null);
            }

            int count =faceInfoAccCtrlDao.selectCountByFaceIdAndLockCode(FaceInfoContextHolder.getFaceRecognitionInfo().getFaceId(),lockCode);
            if(count<1){
                //没有操作权限时需要执行报警操作
                Date date = new Date();
                String snapshotTime = CommonUtil.dateFormat.get().format(date);
                LockProcessResultType processResult = LockProcessResultType.Failure;
                String failReason = "没有操作该门禁锁的权限";
                CameraUploadType type = CameraUploadType.Alarm;

                String fileName=FileUtil.getName(FaceInfoContextHolder.getFaceRecognitionInfo().getFaceImagePath());

                //人脸扫描图片URL
                String facePanoramaUrl = new StringBuilder()
                        .append(MainAction.SnapshotFileName)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(snapshotTime)
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(type.getCode())
                        .append(CommonConstants.URL_SEPARATOR)
                        .append(fileName)
                        .toString();
                String facePanoramaFilePath=SystemUtil.getUserInfo().getCurrentDir()+File.separator+facePanoramaUrl;
                FileUtil.copy(FaceInfoContextHolder.getFaceRecognitionInfo().getFaceImagePath(),facePanoramaFilePath,true);


                FileInfo facePanoramaFile = new FileInfo(snapshotTime + CommonConstants.FACE_PANORAMA_SUFFIX, facePanoramaUrl, "人脸全景图");

                FaceInfo faceInfo=faceInfoManagerDao.selectById(FaceInfoContextHolder.getFaceRecognitionInfo().getFaceId());

                AccCtrlProcess accCtrlProcess = accCtrlProcessUtil.getAccCtrlProcess(lockCode,faceInfo, type, facePanoramaFile, date, processResult,
                        failReason);

                //在事务控制下插入门禁操作记录、门禁实时信息、告警
                ApplicationContextUtil.getBean(ClientFaceInfoCallbackImpl.class).insertData(type, accCtrlProcess);

                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "没有操作该门禁锁的权限", null);
            }

            return ResultBuilder.buildSuccess(lockInfoDao.selectLockPassWordByLockCode(lockCode));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸扫描信息上传失败", null);
        }
    }


    @ApiOperation(value = "人脸扫描")
    @PostMapping(value = "/face-scan/lock-code", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestfulEntityBySummit<String> faceScan(@ApiParam(value = "人脸图片", required = true)
                                                  @RequestPart("faceImageFile") MultipartFile faceImageFile,
                                                  @ApiParam(value = "锁编码", required = true)
                                                  @RequestParam("lockCode") String lockCode) {
        try {
            byte[] faceFileByteArray = faceImageFile.getBytes();
            String faceFileStr = Base64.encode(faceFileByteArray);
            String faceId = baiduSdkClient.searchFace(faceFileStr);
            if (StrUtil.isBlank(faceId)) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "没有匹配到人脸", null);
            }
            FaceInfo faceInfo = faceInfoManagerDao.selectById(faceId);
            if (faceInfo == null) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸信息不存在", null);
            }
            AccessControlInfo accessControlInfo = accessControlDao.selectOne(Wrappers.<AccessControlInfo>lambdaQuery()
                    .eq(AccessControlInfo::getLockCode, lockCode));
            if (accessControlInfo == null) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "没有对应的门禁信息", null);
            }

            IPage<AccCtrlProcess> accCtrlProcessPage = accCtrlProcessDao.selectPage(new Page<>(1, 1), Wrappers.<AccCtrlProcess>lambdaQuery()
                    .eq(AccCtrlProcess::getAccessControlId, accessControlInfo.getAccessControlId())
                    .eq(AccCtrlProcess::getUserName, faceInfo.getUserName())
                    .eq(AccCtrlProcess::getProcessType, LockProcessType.UNLOCK.getCode())
                    .in(AccCtrlProcess::getProcessResult,
                            LockProcessResultType.Success.getCode(),
                            LockProcessResultType.NotResponse.getCode(),
                            LockProcessResultType.WaitSendCommand.getCode(),
                            LockProcessResultType.CommandSuccess.getCode()
                    )
                    .orderByDesc(AccCtrlProcess::getProcessTime)
            );

            List<AccCtrlProcess> accCtrlProcessList = accCtrlProcessPage.getRecords();
            if (accCtrlProcessList.size() != 0) {
                AccCtrlProcess accCtrlProcess = accCtrlProcessList.get(0);
                long differMinute = DateUtil.between(accCtrlProcess.getProcessTime(), new Date(), DateUnit.MINUTE);
                if (differMinute <= 2) {
                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "同一门禁同一人脸不能在2分钟内开锁多次", null);
                }
            }


            int count = faceInfoAccCtrlDao.selectCount(Wrappers.<FaceInfoAccCtrl>lambdaQuery()
                    .eq(FaceInfoAccCtrl::getFaceid, faceId)
                    .eq(FaceInfoAccCtrl::getAccessControlId, accessControlInfo.getAccessControlId()));
            CameraUploadType type = CameraUploadType.Unlock;
            if (count == 0) {
                type = CameraUploadType.Alarm;
//                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, faceInfo.getUserName() + "没有门禁操作权限", null);
            }


            Date date = new Date();
            String snapshotTime = CommonUtil.snapshotTimeFormat.get().format(date);
            String snapshotDate = CommonUtil.dateFormat.get().format(date);

            String picturePathFacePanorama = new StringBuilder()
                    .append(SystemUtil.getUserInfo().getCurrentDir())
                    .append(File.separator)
                    .append(MainAction.SnapshotFileName)
                    .append(File.separator)
                    .append(accessControlInfo.getAccessControlId())
                    .append(File.separator)
                    .append(snapshotTime)
                    .append(File.separator)
                    .append(type.getCode())
                    .append(File.separator)
                    .append(snapshotDate)
                    .append(CommonConstants.FACE_PANORAMA_SUFFIX)
                    .toString();

            String facePanoramaUrl = new StringBuilder()
                    .append(MainAction.SnapshotFileName)
                    .append(CommonConstants.URL_SEPARATOR)
                    .append(accessControlInfo.getAccessControlId())
                    .append(CommonConstants.URL_SEPARATOR)
                    .append(snapshotTime)
                    .append(CommonConstants.URL_SEPARATOR)
                    .append(type.getCode())
                    .append(CommonConstants.URL_SEPARATOR)
                    .append(snapshotDate)
                    .append(CommonConstants.FACE_PANORAMA_SUFFIX)
                    .toString();

            FileInfo facePanoramaFile = new FileInfo(snapshotTime + CommonConstants.FACE_PANORAMA_SUFFIX, facePanoramaUrl, "人脸全景图");


            return ResultBuilder.buildSuccess(faceInfo.getUserName());
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸扫描信息上传失败", null);
        }
    }
//    @ApiOperation(value = "人脸扫描")
//    @PostMapping(value = "/face-scan/lock-code", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public RestfulEntityBySummit<String> faceScan(@ApiParam(value = "人脸图片", required = true)
//                                                  @RequestPart("faceImageFile") MultipartFile faceImageFile,
//                                                  @ApiParam(value = "其他人图片", required = true)
//                                                  @RequestPart("otherPeopleImageFiles") MultipartFile[] otherPeopleImageFiles,
//                                                  @ApiParam(value = "锁编码",required = true)
//                                                  @RequestParam("lockCode") String lockCode) {
//        try {
//            byte[] faceFileByteArray = faceImageFile.getBytes();
//            String faceFileStr = Base64.encode(faceFileByteArray);
//            String faceId = baiduSdkClient.searchFace(faceFileStr);
//            if (StrUtil.isBlank(faceId)) {
//                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "没有匹配到人脸", null);
//            }
//            FaceInfo faceInfo=faceInfoManagerDao.selectById(faceId);
//            log.debug(faceInfo.getUserName());
//            return ResultBuilder.buildSuccess(faceInfo.getUserName());
//        } catch (Exception e) {
//            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸扫描信息上传失败", null);
//        }
//    }


}
