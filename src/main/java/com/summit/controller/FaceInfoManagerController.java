package com.summit.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.FaceInfo;
import com.summit.service.FaceInfoManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2019/8/21.
 */
@Slf4j
@Api(tags = "人脸信息管理接口")
@RestController
@RequestMapping("/faceInfoManager")
public class FaceInfoManagerController {
  @Autowired
  private FaceInfoManagerService faceInfoManagerService;
  @ApiOperation(value = "录入人脸信息",notes = "返回不是-1则为成功")
  @PostMapping(value = "insertFaceInfo")
  public RestfulEntityBySummit<Integer>  insertFaceInfo(@RequestParam(value = "faceName",defaultValue = "")String faceName,
                                                        @RequestParam(value = "gender") Integer gender,
                                                        @RequestParam(value = "province")String province,
                                                        @RequestParam(value = "city")String city,
                                                        @RequestParam(value = "birthday")String birthday,
                                                        @RequestParam(value = "cardType")Integer cardType,
                                                        @RequestParam(value = "cardID")String cardID,
                                                        @RequestParam(value = "faceImage")MultipartFile faceImage) throws ParseException {
        String time=CommonConstants.snapshotTimeFormat.format(new Date());
        String location = new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(CommonConstants.FACE_LIB_ROOT)
                .append(File.separator)
                .append(faceName)
                .append(File.separator)
                .append(time)
                .append(CommonConstants.FACE_Image_SUFFIX)
                .toString();
          String filename = new StringBuilder()
              .append(CommonConstants.FACE_LIB_ROOT)
              .append(CommonConstants.URL_SEPARATOR)
              .append(faceName)
              .append(CommonConstants.URL_SEPARATOR)
              .append(time)
              .append(CommonConstants.FACE_Image_SUFFIX)
              .toString();
          System.out.println(location);
          try {
            FileUtil.writeBytes(faceImage.getBytes(), location);
          } catch (IOException e) {
            //e.printStackTrace();
            log.error("保存人脸图片异常");
          }
          FaceInfo faceInfo=new FaceInfo();
          faceInfo.setFaceName(faceName);
          faceInfo.setGender(gender);
          faceInfo.setProvince(province);
          faceInfo.setCity(city);
          SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd");
          Date birthday1 = sdf.parse(birthday);
          faceInfo.setBirthday(birthday1);
          faceInfo.setCardType(cardType);
          faceInfo.setCardID(cardID);
          faceInfo.setFaceImage(filename);
          try {
            faceInfoManagerService.insertFaceInfo(faceInfo);
          } catch (Exception e) {
            e.printStackTrace();
            log.error("录入人脸信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"录入人脸信息失败", CommonConstants.UPDATE_ERROR);
          }
          return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"录入人脸信息成功", 0);
  }














}
