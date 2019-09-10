package com.summit.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.netflix.discovery.converters.Auto;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.FaceInfoEntity;
import com.summit.dao.entity.FileInfo;
import com.summit.service.FaceInfoService;
import com.summit.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Api(tags = "人脸信息接口")
@RestController
@RequestMapping("/faceInfo")
public class FaceInfoController {

    @Autowired
    private FaceInfoService faceInfoService;

    @ApiOperation(value = "录入人脸信息", notes = "返回不为-1则为成功")
    @PostMapping(value = "/insertFaceInfo")
    public RestfulEntityBySummit<Integer> insertFaceInfo(@ApiParam(value = "人脸信息参数") @RequestBody FaceInfoEntity faceInfoEntity,
                                                         @ApiParam(value = "人脸全景图") MultipartFile facePanorama,
                                                         @ApiParam(value = "人脸识别抠图") MultipartFile facePic,
                                                         @ApiParam(value = "人脸识别和人脸库中匹配的图片") MultipartFile faceMatch) {
        if(faceInfoEntity == null){
            log.error("人脸信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸信息为空", CommonConstants.UPDATE_ERROR);
        }
        if(facePanorama == null){
            log.error("人脸全景图为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸全景图为空", CommonConstants.UPDATE_ERROR);
        }
        if(facePic == null){
            log.error("人脸识别抠图为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸识别抠图为空", CommonConstants.UPDATE_ERROR);
        }
        String name = faceInfoEntity.getUserName();
        String time = CommonUtil.snapshotTimeFormat.get().format(new Date());

        String picturePathFacePanorama = new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(CommonConstants.FACE_LIB_ROOT)
                .append(File.separator)
                .append(name)
                .append(File.separator)
                .append(time)
                .append(CommonConstants.FACE_PANORAMA_SUFFIX)
                .toString();

        String picturePathFacePic = new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(CommonConstants.FACE_LIB_ROOT)
                .append(File.separator)
                .append(name)
                .append(File.separator)
                .append(time)
                .append(CommonConstants.FACE_PIC_SUFFIX)
                .toString();

        String facePanoramaUrl = new StringBuilder()
                .append(CommonConstants.FACE_LIB_ROOT)
                .append(CommonConstants.URL_SEPARATOR)
                .append(name)
                .append(CommonConstants.URL_SEPARATOR)
                .append(time)
                .append(CommonConstants.FACE_PANORAMA_SUFFIX)
                .toString();

        String facePicUrl = new StringBuilder()
                .append(CommonConstants.FACE_LIB_ROOT)
                .append(CommonConstants.URL_SEPARATOR)
                .append(name)
                .append(CommonConstants.URL_SEPARATOR)
                .append(time)
                .append(CommonConstants.FACE_PIC_SUFFIX)
                .toString();
        String panoramaName = facePanorama.getName();
        String facePicName = facePic.getName();

        try {
            FileUtil.writeBytes(facePanorama.getBytes(), picturePathFacePanorama);
            FileUtil.writeBytes(facePic.getBytes(), picturePathFacePic);
        } catch (IOException e) {
            log.error("保存人脸图片异常");
        }

        FileInfo facePanoramaFile = new FileInfo(panoramaName, facePanoramaUrl, "人脸全景图");
        FileInfo facePicFile = new FileInfo(facePicName, facePicUrl, "人脸识别抠图");

        faceInfoEntity.setFacePanorama(facePanoramaFile);
        faceInfoEntity.setFacePic(facePicFile);

        try {
            faceInfoService.insertFaceInfo(faceInfoEntity);
        } catch (Exception e) {
            log.error("录入人脸信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"录入人脸信息失败", CommonConstants.UPDATE_ERROR);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"录入人脸信息成功", 0);
    }



}
