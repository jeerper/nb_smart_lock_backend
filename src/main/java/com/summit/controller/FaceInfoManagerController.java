package com.summit.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.summit.MainAction;
import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.entity.FaceInfoManagerEntity;
import com.summit.entity.SimpleFaceInfo;
import com.summit.exception.ErrorMsgException;
import com.summit.service.FaceInfoManagerService;
import com.summit.util.CommonUtil;
import com.summit.util.SummitTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
  public RestfulEntityBySummit<Integer>  insertFaceInfo(@RequestBody FaceInfoManagerEntity faceInfoManagerEntity) throws ParseException {
       //System.out.println(faceInfoManagerEntity+"ddd");
       String  base64Str=faceInfoManagerEntity.getFaceImage();
        FaceInfo faceInfo=new FaceInfo();
        if(SummitTools.stringNotNull(base64Str)){
          StringBuffer fileName = new StringBuffer();
          fileName.append(UUID.randomUUID().toString().replaceAll("-", ""));
          if (base64Str.indexOf("data:image/png;") != -1) {
            base64Str = base64Str.replace("data:image/png;base64,", "");
            fileName.append(".png");
          } else if (base64Str.indexOf("data:image/jpeg;") != -1) {
            base64Str = base64Str.replace("data:image/jpeg;base64,", "");
            fileName.append(".jpeg");
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
                  .append("/")
                  .append(MainAction.SnapshotFileName)
                  .append("/")
                  .append(picId)
                  .append("_Face.jpg")
                  .toString();
          faceInfo.setFaceImage(faceUrl);
          byte[] bytes = Base64.getDecoder().decode(base64Str);
          try {
            FileUtil.writeBytes(bytes,facePicPath);
          } catch (Exception e) {
            log.error("保存人脸图片异常");
          }
        }
          faceInfo.setUserName(faceInfoManagerEntity.getUserName());
          faceInfo.setGender(faceInfoManagerEntity.getGender());
          faceInfo.setProvince(faceInfoManagerEntity.getProvince());
          faceInfo.setCity(faceInfoManagerEntity.getCity());
          Date birth = CommonUtil.dateFormat.get().parse(faceInfoManagerEntity.getBirthday());
          faceInfo.setBirthday(birth);
          faceInfo.setCardType(faceInfoManagerEntity.getCardType());
          faceInfo.setCardId(faceInfoManagerEntity.getCardId());
          try {
            faceInfoManagerService.insertFaceInfo(faceInfo);
          } catch (Exception e) {
            e.printStackTrace();
            log.error("录入人脸信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"录入人脸信息失败", CommonConstants.UPDATE_ERROR);
          }
          return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"录入人脸信息成功", 0);
  }
  @ApiOperation(value = "根据所传一个或多个条件组合分页查询人脸信息记录",notes = "各字段都为空则查询全部。分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
  @GetMapping(value = "/selectFaceInfoByPage")
  public RestfulEntityBySummit<Page<FaceInfo>> selectFaceInfoByPage(@ApiParam(value = "姓名")@RequestParam(value = "userName",required = false,defaultValue = "")String userName,
                                                                    @ApiParam(value = "证件号")@RequestParam(value = "cardId",required = false,defaultValue = "")String cardId,
                                                                    @ApiParam(value = "省份")@RequestParam(value = "province",required = false ,defaultValue = "")String province,
                                                                    @ApiParam(value = "城市")@RequestParam(value = "city",required = false,defaultValue ="")String city,
                                                                    @ApiParam(value = "性别，0：男，1：女，2：未知")@RequestParam(value = "gender",required = false)Integer gender,
                                                                    @ApiParam(value = "证件类型，0：身份证，1：护照，2：军官证，3：驾驶证，4：未知")@RequestParam(value = "cardType",required = false)Integer cardType,
                                                                    @ApiParam(value = "当前页，大于等于1")@RequestParam(value = "current",required = false)Integer current,
                                                                    @ApiParam(value = "每页条数，大于等于0")@RequestParam(value = "pageSize",required = false)Integer pageSize){
    Page<FaceInfo> faceInfoPage=null;

    try {
      FaceInfoManagerEntity faceInfoManagerEntity = new FaceInfoManagerEntity();
      faceInfoManagerEntity.setUserName(userName);
      faceInfoManagerEntity.setCardId(cardId);
      faceInfoManagerEntity.setProvince(province);
      faceInfoManagerEntity.setCity(city);
      faceInfoManagerEntity.setGender(gender);
      faceInfoManagerEntity.setCardType(cardType);
      System.out.println(faceInfoManagerEntity+"ggg");
      faceInfoPage=faceInfoManagerService.selectFaceInfoByPage(faceInfoManagerEntity,new SimplePage(current,pageSize));
    } catch (Exception e) {
      e.printStackTrace();
      log.error("分页全部查询人脸信息失败");
      return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"分页全部查询人脸信息失败",faceInfoPage);
    }
    return  ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"分页全部查询人脸信息成功",faceInfoPage);
  }

  @ApiOperation(value = "更新人脸信息")
  @PutMapping(value = "/updateFaceInfo")
  public RestfulEntityBySummit<String> updateFaceInfo(@ApiParam(value = "包含人脸信息")@RequestBody FaceInfo faceInfo ){
    if(faceInfo==null){
      log.error("人脸信息为空");
      return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸信息为空",null);
    }
    String msg="更新人脸信息失败";
    String  base64Str=faceInfo.getFaceImage();
    if(SummitTools.stringNotNull(base64Str)){
      StringBuffer fileName = new StringBuffer();
      fileName.append(UUID.randomUUID().toString().replaceAll("-", ""));
      if (base64Str.indexOf("data:image/png;") != -1) {
        base64Str = base64Str.replace("data:image/png;base64,", "");
        fileName.append(".png");
      } else if (base64Str.indexOf("data:image/jpeg;") != -1) {
        base64Str = base64Str.replace("data:image/jpeg;base64,", "");
        fileName.append(".jpeg");
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
              .append("/")
              .append(MainAction.SnapshotFileName)
              .append("/")
              .append(picId)
              .append("_Face.jpg")
              .toString();
      faceInfo.setFaceImage(faceUrl);
      byte[] bytes = Base64.getDecoder().decode(base64Str);
      try {
        FileUtil.writeBytes(bytes,facePicPath);
      } catch (Exception e) {
        log.error("保存人脸图片异常");
      }
    }
    try {
      faceInfoManagerService.updateFaceInfo(faceInfo);
    } catch (Exception e) {
      e.printStackTrace();
      msg=getErrorMsg(msg,e);
      log.error(msg);
      return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,msg,null);
    }
    return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新人脸信息成功",null);
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
  @ApiOperation(value = "根据id查询人脸信息",notes = "faceid不能为空，查询唯一一条人脸信息")
  @GetMapping(value = "/queryFaceInfoById")
  public RestfulEntityBySummit<FaceInfo> queryFaceInfoById(@ApiParam(value = "人脸信息id",required = true)@RequestParam(value = "faceid")String faceid){
    if(faceid==null){
      log.error("人脸信息id为空");
      return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸信息id为空",null);
    }
    FaceInfo faceInfo=null;
    try {
      faceInfo=faceInfoManagerService.selectFaceInfoByID(faceid);
    } catch (Exception e) {
      log.error("查询人脸信息失败");
      return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"根据id查询人脸信息失败",faceInfo);
    }
    return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"根据id查询人脸信息成功",faceInfo);
  }
   @ApiOperation(value = "查询全部人脸信息，包括人脸信息的id和name以及人脸图片",notes = "无论有无门禁权限都全部查询")
   @GetMapping(value = "/selectAllFaceInfo")
   public RestfulEntityBySummit<List<SimpleFaceInfo>> selectAllFaceInfo(){
     List<SimpleFaceInfo> simpleFaceInfos=new ArrayList<>();
     try {
       List<FaceInfo> faceInfos=faceInfoManagerService.selectAllFaceInfo(null);
       System.out.println(faceInfos+"aaa");
       if(faceInfos !=null){
         for(FaceInfo faceInfo:faceInfos){
           simpleFaceInfos.add(new SimpleFaceInfo(faceInfo.getFaceid(),faceInfo.getUserName(),faceInfo.getFaceImage()));
         }
       }
     } catch (Exception e) {
        log.error("查询全部的人脸信息失败");
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询全部人脸信息失败",simpleFaceInfos);
     }
     return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询全部人脸信息成功",simpleFaceInfos);
   }


  private String getErrorMsg(String msg,Exception e){
    if(e instanceof ErrorMsgException){
      return  msg=((ErrorMsgException)e).getErrorMsg();
    }
    return  msg;
  }












}
