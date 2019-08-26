package com.summit.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.summit.MainAction;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.FaceInfo;
import com.summit.exception.ErrorMsgException;
import com.summit.service.FaceInfoManagerService;
import com.summit.util.SummitTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
  public RestfulEntityBySummit<Integer>  insertFaceInfo(@RequestParam(value = "userName",defaultValue = "")String userName,
                                                        @RequestParam(value = "gender") Integer gender,
                                                        @RequestParam(value = "province")String province,
                                                        @RequestParam(value = "city")String city,
                                                        @RequestParam(value = "birthday")String birthday,
                                                        @RequestParam(value = "cardType")Integer cardType,
                                                        @RequestParam(value = "cardId")String cardId,
                                                        @RequestParam(value = "faceImage")String faceImage) throws ParseException {
        String time=CommonConstants.snapshotTimeFormat.format(new Date());
        String  base64Str=faceImage;
        FaceInfo faceInfo=new FaceInfo();
        if(SummitTools.stringNotNull(base64Str)){
          StringBuffer fileName = new StringBuffer();
          fileName.append(UUID.randomUUID().toString().replaceAll("-",""));
          if(base64Str.indexOf("data:image/png;") != -1){
            base64Str=base64Str.replace("data:image/png;base64,", "");
            fileName.append(".png");
          }else if(base64Str.indexOf("data:image/jpeg;") != -1){
            base64Str.replace("data:image/jpeg;base64,", "");
            fileName.append("jpeg");
          }
          String picId= IdWorker.getIdStr();
          String facePicPath=new StringBuilder()
                  .append(SystemUtil.getUserInfo().getCurrentDir())
                  .append(File.separator)
                  .append(MainAction.SnapshotFileName)
                  .append(File.separator)
                  .append(picId)
                  .append("_Face.jpg")
                  .toString();
          String faceUrl=new StringBuilder()
                  .append(CommonConstants.FACE_LIB_ROOT)
                  .append(CommonConstants.URL_SEPARATOR)
                  .append(userName)
                  .append(CommonConstants.URL_SEPARATOR)
                  .append(time)
                  .append(CommonConstants.FACE_Image_SUFFIX)
                  .toString();
          faceInfo.setFaceImage(faceUrl);
          byte[] bytes = Base64.getDecoder().decode(base64Str);
          try {
            FileUtil.writeBytes(bytes,facePicPath);
          } catch (Exception e) {
            log.error("保存人脸图片异常");
          }
        }
          faceInfo.setUserName(userName);
          faceInfo.setGender(gender);
          faceInfo.setProvince(province);
          faceInfo.setCity(city);
          SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd");
          Date birthday1 = sdf.parse(birthday);
          faceInfo.setBirthday(birthday1);
          faceInfo.setCardType(cardType);
          faceInfo.setCardId(cardId);
          try {
            faceInfoManagerService.insertFaceInfo(faceInfo);
          } catch (Exception e) {
            e.printStackTrace();
            log.error("录入人脸信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"录入人脸信息失败", CommonConstants.UPDATE_ERROR);
          }
          return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"录入人脸信息成功", 0);
  }
  @ApiOperation(value = "删除人脸信息，参数为id数组",notes = "根据人脸id删除人脸信息")
  @DeleteMapping(value = "/delfaceInfoByIdBatch")
  public RestfulEntityBySummit<String> delFaceInfo(@ApiParam(value = "人脸信息的id",required = true)@RequestParam(value = "faceInfoIds",required = false)List<String> faceInfoIds){
    if(faceInfoIds==null || faceInfoIds.isEmpty()){
        log.error("人脸信息id为空");
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸信息id为空",null);
    }
    String msg="删除人脸信息失败";
    try {
      faceInfoManagerService.delFaceInfoByIds(faceInfoIds);
    } catch (Exception e) {
      msg=getErrorMsg(msg,e);
      log.error(msg);
      return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,msg,null);
    }
    return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"删除人脸信息成功",null);
  }




  private String getErrorMsg(String msg,Exception e){
    if(e instanceof ErrorMsgException){
      return  msg=((ErrorMsgException)e).getErrorMsg();
    }
    return  msg;
  }












}
