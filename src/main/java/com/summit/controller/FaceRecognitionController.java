package com.summit.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.utils.BaiduSdkClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    private BaiduSdkClient baiduSdkClient;
    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;


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
            FaceInfo faceInfo=faceInfoManagerDao.selectById(faceId);
            log.debug(faceInfo.getUserName());
            return ResultBuilder.buildSuccess(faceInfo.getUserName());
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸扫描信息上传失败", null);
        }
    }


}
