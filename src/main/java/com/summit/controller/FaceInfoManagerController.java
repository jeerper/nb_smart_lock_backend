package com.summit.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.summit.MainAction;
import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.*;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.entity.FaceInfoManagerEntity;
import com.summit.entity.SimpleFaceInfo;
import com.summit.exception.ErrorMsgException;
import com.summit.schedule.FaceInfoManagerSchedule;
import com.summit.sdk.huawei.*;
import com.summit.sdk.huawei.api.HuaWeiSdkApi;
import com.summit.sdk.huawei.model.DeviceInfo;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.service.FaceInfoManagerService;
import com.summit.util.CommonUtil;
import com.summit.util.SummitTools;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.DateFormat;
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
  @Autowired
  private FaceInfoManagerDao faceInfoManagerDao;
  @Autowired
  private  FaceInfoAccCtrlService faceInfoAccCtrlService;
  @ApiOperation(value = "录入人脸信息",notes = "返回不是-1则为成功")
  @PostMapping(value = "insertFaceInfo")
  public RestfulEntityBySummit<Integer>  insertFaceInfo(@RequestBody FaceInfoManagerEntity faceInfoManagerEntity) throws ParseException, IOException {
        String  base64Str=faceInfoManagerEntity.getFaceImage();
        System.out.println("人脸图片："+base64Str);
        if(base64Str==null || base64Str.isEmpty()){
          log.error("人脸图片为空");
          return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸图片为空",null);
        }
        /**
         * 判断人脸修改时图片是否重复，
         */
        if (SummitTools.stringNotNull(base64Str)){
          List<String> faceImagesRelativePaths=new ArrayList<>();//得到所有的图片路径
          List<FaceInfo> allFaceInfo2 = faceInfoManagerService.selectAllFaceInfo(null);
          for (FaceInfo bianlioldFaceInfo:allFaceInfo2){
            if (bianlioldFaceInfo.getFaceImage()!=null){
              faceImagesRelativePaths.add(bianlioldFaceInfo.getFaceImage());
            }
          }
          List<String> imagesBase64=new ArrayList<>();//得到所有的图片base64
          if (!CommonUtil.isEmptyList(faceImagesRelativePaths)){
            for (String faceImagesRelativePath:faceImagesRelativePaths){
              String faceImagesAbsolutePath = new String(new File(".").getCanonicalPath() + faceImagesRelativePath);
              String imageToBase64Str = CommonUtil.imageToBase64Str(faceImagesAbsolutePath);
              imagesBase64.add(imageToBase64Str);
            }
          }
          if (!CommonUtil.isEmptyList(imagesBase64)){
            for (String imageBase64:imagesBase64){
              String newfaceImageBase64 = faceInfoManagerEntity.getFaceImage();
              int i = newfaceImageBase64.indexOf(",");
              String subNewImageBase64= newfaceImageBase64.substring(i+1);
              if (subNewImageBase64.equals(imageBase64)){
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸添加失败,头像重复", null);
              }
            }
          }
        }
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
        faceInfo.setFaceType(faceInfoManagerEntity.getFaceType());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        Date date = new Date();
        String startTime = sdf.format(date);
        Date faceStartTime = sdf.parse(startTime);
        faceInfo.setFaceStartTime(faceStartTime);
        Date faceEndTime = CommonUtil.dateFormat.get().parse(faceInfoManagerEntity.getFaceEndTime());
        faceInfo.setFaceEndTime(faceEndTime);
        faceInfo.setIsValidTime(0);//人脸录入时候这时候人脸肯定未过期
        try {
          int i = faceInfoManagerService.insertFaceInfo(faceInfo);
        } catch (Exception e) {
          e.printStackTrace();
          log.error("人脸信息录入名称已存在");
          return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"人脸信息录入名称已存在", CommonConstants.UPDATE_ERROR);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"录入人脸信息成功",1);
  }
  @ApiOperation(value = "根据所传一个或多个条件组合分页查询人脸信息记录",notes = "各字段都为空则查询全部。分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
  @GetMapping(value = "/selectFaceInfoByPage")
  public RestfulEntityBySummit<Page<FaceInfo>> selectFaceInfoByPage(@ApiParam(value = "姓名")@RequestParam(value = "userName",required = false,defaultValue = "")String userName,
                                                                    @ApiParam(value = "证件号")@RequestParam(value = "cardId",required = false,defaultValue = "")String cardId,
                                                                    @ApiParam(value = "省份")@RequestParam(value = "province",required = false ,defaultValue = "")String province,
                                                                    @ApiParam(value = "城市")@RequestParam(value = "city",required = false,defaultValue ="")String city,
                                                                    @ApiParam(value = "性别，0：男，1：女，2：未知")@RequestParam(value = "gender",required = false)Integer gender,
                                                                    @ApiParam(value = "证件类型，0：身份证，1：护照，2：军官证，3：驾驶证，4：未知")@RequestParam(value = "cardType",required = false)Integer cardType,
                                                                    @ApiParam(value = "人脸类型，0:内部人员，1:临时人员")@RequestParam(value = "faceType",required = false)Integer faceType,
                                                                    @ApiParam(value = "人脸过期，0:没有过期，1:已经过期")@RequestParam(value = "isValidTime",required = false)Integer isValidTime,
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
      faceInfoManagerEntity.setFaceType(faceType);
      faceInfoManagerEntity.setIsValidTime(isValidTime);
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
  public RestfulEntityBySummit<String> updateFaceInfo(@ApiParam(value = "包含人脸信息")@RequestBody FaceInfo faceInfo ) throws IOException, ParseException {
    if(faceInfo==null){
      log.error("人脸信息为空");
      return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸信息为空",null);
    }

    //更新之前找到原来的人脸信息
    String selectoldfaceid = faceInfo.getFaceid();
    FaceInfo oldFaceInfo = faceInfoManagerService.selectFaceInfoByID(selectoldfaceid);
    /**
     * 判断人脸修改时图片是否重复，
     */
    String  base64Str=faceInfo.getFaceImage();
    if (SummitTools.stringNotNull(base64Str)){
      List<String> faceImagesRelativePaths=new ArrayList<>();//得到所有的图片路径
      List<FaceInfo> allFaceInfo2 = faceInfoManagerService.selectAllFaceInfo(null);
      for (FaceInfo bianlioldFaceInfo:allFaceInfo2){
        if (bianlioldFaceInfo.getFaceImage()!=null){
          faceImagesRelativePaths.add(bianlioldFaceInfo.getFaceImage());
        }
      }
      List<String> imagesBase64=new ArrayList<>();//得到所有的图片base64
      if (!CommonUtil.isEmptyList(faceImagesRelativePaths)){
        for (String faceImagesRelativePath:faceImagesRelativePaths){
          String faceImagesAbsolutePath = new String(new File(".").getCanonicalPath() + faceImagesRelativePath);
          String imageToBase64Str = CommonUtil.imageToBase64Str(faceImagesAbsolutePath);
          imagesBase64.add(imageToBase64Str);
        }
      }
      if (!CommonUtil.isEmptyList(imagesBase64)){
        for (String imageBase64:imagesBase64){
          String newfaceImageBase64 = faceInfo.getFaceImage();
          int i = newfaceImageBase64.indexOf(",");
          String subNewImageBase64= newfaceImageBase64.substring(i+1);
          if (subNewImageBase64.equals(imageBase64)){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸更新失败,头像重复", null);
          }
        }
      }
    }
    String msg="更新人脸信息失败";
    String faceUrl=null;
    System.out.println(base64Str+"qqqqq");
    if(SummitTools.stringNotNull(base64Str) && base64Str!=null && base64Str!=""){
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
       faceUrl=new StringBuilder()
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
    /**
     * 编辑人脸信息的时候和摄像头同步,分为两种，一种是人脸有效期过期的修改(不修改摄像头)，一种是没有过期的修改(摄像头也要修改)
     * 1 查询摄像头中的人脸信息，得到人脸信息的id
     * 2 修改人脸信息
     */
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    Date date = new Date();
    String nowtime = df.format(date);
    long nowDate = df.parse(nowtime).getTime();
    Date oldfaceEndTime = oldFaceInfo.getFaceEndTime();
    long oldfaceEndDate = oldfaceEndTime.getTime();
    Date newEndTime = faceInfo.getFaceEndTime();
    long newfaceEndDate = newEndTime.getTime();
      /**
       * 修改的是有效期则需要把过期从1变为0,修改的是自己的数据库
       */
    if (nowDate>oldfaceEndDate && newfaceEndDate>=nowDate){
        faceInfo.setIsValidTime(0);
        faceInfoManagerService.updateFaceInfo(faceInfo);
    }
    String faceid = faceInfo.getFaceid();
    List<AccessControlInfo> accessControlInfos=faceInfoAccCtrlService.seleAccCtrlInfoByFaceID(faceid);//确定授权
    if(!CommonUtil.isEmptyList(accessControlInfos)){//确定已经授权
      List<String> cameraIps=new ArrayList<>();
      for(AccessControlInfo accessControlInfo:accessControlInfos){
        String entryCameraIp = accessControlInfo.getEntryCameraIp();
        String exitCameraIp = accessControlInfo.getExitCameraIp();
        cameraIps.add(entryCameraIp);
        cameraIps.add(exitCameraIp);
      }
     // System.out.println("当前人脸所关联的入口和出口摄像头ip"+cameraIps);
      if(nowDate>oldfaceEndDate && newfaceEndDate>=nowDate){//过期人脸的修改，并且如果修改的是有效期，则需要重新吧这个人脸加入到摄像头里面，如果不是有效期，则需要修改自己的数据库就可以
         Integer printReturnMsg=null;//定义返回码
        /**
         * 修改的是有效期则需要把过期从1变为0,修改的是自己的数据库
         */
         faceInfo.setIsValidTime(0);
         faceInfoManagerService.updateFaceInfo(faceInfo);
        /**
         * 再修改摄像头中的人脸信息
         */
        for(String cameraIp:cameraIps){
           DeviceInfo deviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(cameraIp);
           NativeLong ulIdentifyId;
           if (deviceInfo==null){
             ulIdentifyId=new NativeLong(2222);
           }else {
             ulIdentifyId = deviceInfo.getUlIdentifyId();
           }
           /**
            * 编辑之前先查询人脸库
            */
           PU_FACE_LIB_GET_S updateAddfacelib =new PU_FACE_LIB_GET_S();
           updateAddfacelib.ulChannelId=new NativeLong(101);
           updateAddfacelib.ulFaceLibNum=new NativeLong(1);
           String updateAddfacelibpath=new StringBuilder()
                   .append(SystemUtil.getUserInfo().getCurrentDir())
                   .append(File.separator)
                   .append(MainAction.UpdateAddFaceLib)
                   .toString();
           File updateAddfacef=new File(updateAddfacelibpath);
           if(!updateAddfacef.exists()){
             updateAddfacef.mkdirs();
           }
           String updateAddLib=updateAddfacelibpath+File.separator+"updateAddFaceLib.json";
           updateAddfacelib.szFindResultPath= Arrays.copyOf(updateAddLib.getBytes(),128);
           boolean getAddFaceLib;
           if(Platform.isWindows()){
             getAddFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(ulIdentifyId, updateAddfacelib);
           }else {
             getAddFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(ulIdentifyId, updateAddfacelib);
           }
           //出口人脸库
           Integer updateAddfacelibType=null;//人脸库类型
           Integer updateAddfacelibID=null;//人脸库id
           String  updateAddfacelibName=null;//人脸库名称
           Integer updateAddfacelibThreshold=null;//布控的阀值
           if (getAddFaceLib){
             System.out.println("查询编辑人脸库成功---------");
             String updateAddgetfacelibpath = new String(new File(".").getCanonicalPath() + File.separator +"updateAddFaceLib"+File.separator+"updateAddFaceLib.json");
             String updateAddFaceLibjson = readFile(updateAddgetfacelibpath);
             JSONObject updateAddFaceLiobject=new JSONObject(updateAddFaceLibjson);
             JSONArray updateAddFaceLiobjectArry = updateAddFaceLiobject.getJSONArray("FaceListsArry");
             System.out.println("人脸库集合："+updateAddFaceLiobjectArry);
             JSONObject updateAddfacelibinfo=updateAddFaceLiobjectArry.getJSONObject(0);
             updateAddfacelibName=updateAddfacelibinfo.getStr("FaceListName");
             updateAddfacelibType = Integer.parseInt(updateAddfacelibinfo.getStr("FaceListType"));
             updateAddfacelibID = Integer.parseInt(updateAddfacelibinfo.getStr("ID"));
             updateAddfacelibThreshold=Integer.parseInt(updateAddfacelibinfo.getStr("Threshold"));
           }else {
             updateAddfacelibName = "facelib";
             updateAddfacelibType = 2;
             updateAddfacelibThreshold = 90;
             updateAddfacelibID = 1111;
             log.error("设备还未上线-------------------");
           }
           //设置facelib对象
           PU_FACE_LIB_S stFacelib1=new PU_FACE_LIB_S();
           stFacelib1.enLibType=updateAddfacelibType;
           stFacelib1.isControl=true;
           stFacelib1.ulFaceLibID=new NativeLong(updateAddfacelibID);
           if(Platform.isWindows()){
             stFacelib1.szLibName=Arrays.copyOf(updateAddfacelibName.getBytes("GBK"),65);
           }else {
             stFacelib1.szLibName=Arrays.copyOf(updateAddfacelibName.getBytes("utf8"),65);
           }
           stFacelib1.uiThreshold=new NativeLong(updateAddfacelibThreshold);
           //设置添加人脸信息的对象
           PU_FACE_INFO_ADD_S  puFaceInfoAdd = new PU_FACE_INFO_ADD_S();
           puFaceInfoAdd.stFacelib=stFacelib1;
           puFaceInfoAdd.ulChannelId=new NativeLong(101);
           //设置人脸信息
           PU_FACE_RECORD addface=new PU_FACE_RECORD();
           addface.enCardType=faceInfo.getCardType();
           addface.enGender=faceInfo.getGender();
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
           String birthday = sdf.format(faceInfo.getBirthday());
           addface.szBirthday=Arrays.copyOf(birthday.getBytes(),32);
           addface.szCardID=Arrays.copyOf(faceInfo.getCardId().getBytes(),32);
           if(Platform.isWindows()){
             addface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("GBK"),48);
             addface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("GBK"),64);
             addface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("GBK"),32);
           }else {
             addface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("utf8"),48);
             addface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("utf8"),64);
             addface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("utf8"),32);
           }
           String absolutePath=null;
           if (!SummitTools.stringNotNull(base64Str) && base64Str!=null && base64Str !=""){
              absolutePath = new String(new File(".").getCanonicalPath() + faceUrl);
              log.error(absolutePath+"修改时上传了图片的路径");
           }else {
              absolutePath = new String(new File(".").getCanonicalPath() +oldFaceInfo.getFaceImage());
             log.error(absolutePath+"修改时没有上传图片的路径");
           }
           addface.szPicPath=Arrays.copyOf(absolutePath.getBytes(),128);
           puFaceInfoAdd.stRecord=addface;
           String filename=null;
           if (!SummitTools.stringNotNull(base64Str) && base64Str!=null && base64Str !=""){
             filename = faceInfo.getFaceImage().substring(faceInfo.getFaceImage().lastIndexOf("/")+1);
           }else {
             filename = oldFaceInfo.getFaceImage().substring(oldFaceInfo.getFaceImage().lastIndexOf("/")+1);
           }
           System.out.println(filename);
           boolean addface2;
           if(Platform.isWindows()){
             addface2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(ulIdentifyId, puFaceInfoAdd, filename);
             log.error("编辑修改过期人脸信息添加人脸的返回码：");
             long returnMsg = HuaWeiSdkApi.printReturnMsg();
             if (returnMsg==106){
               printReturnMsg=106;
             }
           }else {
             addface2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(ulIdentifyId, puFaceInfoAdd, filename);
             log.error("编辑修改过期人脸信息添加人脸的返回码：");
             long returnMsg = HuaWeiSdkApi.printReturnMsg();
             if (returnMsg==106){
               printReturnMsg=106;
             }
           }
           if(addface2){
             System.out.println("添加人脸信息成功");
             //接着进行提取特征值
             PU_FACE_FEATURE_EXTRACT_S puFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
             puFaceFeatureExtractS.stFacelib=stFacelib1;
             puFaceFeatureExtractS.ulChannelId=new NativeLong(101);
             boolean getTeZheng;
             if(Platform.isWindows()){
               getTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(ulIdentifyId,puFaceFeatureExtractS);
               HuaWeiSdkApi.printReturnMsg();
             }else {
               getTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(ulIdentifyId,puFaceFeatureExtractS);
               HuaWeiSdkApi.printReturnMsg();
             }
             /**
              * 需要睡眠几秒钟
              */
               try {
                   Thread.sleep(2000);
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
               }
             //接着布控
             PU_FACE_LIB_SET_S puFaceLibSetS2 =new PU_FACE_LIB_SET_S();
             puFaceLibSetS2.enOptType=2;//修改facelib
             PU_FACE_LIB_S  stFacelib2=new PU_FACE_LIB_S();
             if(Platform.isWindows()){
               stFacelib2.szLibName=Arrays.copyOf(updateAddfacelibName.getBytes("gbk"),65);//windows名单库的名称
             }else {
               stFacelib2.szLibName=Arrays.copyOf(updateAddfacelibName.getBytes("utf8"),65);//名单库的名称
             }
             stFacelib2.uiThreshold=new NativeLong(updateAddfacelibThreshold);//布控的阀值
             stFacelib2.enLibType=updateAddfacelibType;//facelib类型2为白名单
             stFacelib2.isControl=true;//修改为布控
             stFacelib2.ulFaceLibID=new NativeLong(updateAddfacelibID);
             puFaceLibSetS2.stFacelib=stFacelib2;
             puFaceLibSetS2.ulChannelId=new NativeLong(101);
             boolean bukong;
             if(Platform.isWindows()){
               bukong = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(ulIdentifyId, puFaceLibSetS2);
               HuaWeiSdkApi.printReturnMsg();
             }else {
               bukong = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(ulIdentifyId, puFaceLibSetS2);
               HuaWeiSdkApi.printReturnMsg();
             }
           }else {
             log.error("添加人脸信息失败--------");
           }
         }
         if (printReturnMsg !=null && printReturnMsg==106){
           return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"更新人脸信息一部分成功,部分设备未上线",null);
         }else {
           return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新人脸信息全部成功",null);
         }
      }else if(nowDate<=oldfaceEndDate){//不是过期人脸的修改，则需要自己数据库和摄像头同时修改,并且如果修改时上传了照片，则需要把原来的人脸头像删掉，重新添加到摄像头里面，如果不是直接修改就行
        Integer printReturnMsg2=null;//定义返回码
        for(String cameraIp:cameraIps){
          DeviceInfo deviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(cameraIp);
          NativeLong ulIdentifyId;
          if (deviceInfo==null){
            ulIdentifyId=new NativeLong(222);
          }else {
            ulIdentifyId = deviceInfo.getUlIdentifyId();
          }
          System.out.println(ulIdentifyId+"222222");
          /**
           * 查询人脸库信息
           */
          PU_FACE_LIB_GET_S updatefacelib =new PU_FACE_LIB_GET_S();
          updatefacelib.ulChannelId=new NativeLong(101);
          updatefacelib.ulFaceLibNum=new NativeLong(1);
          String updatefacelibpath=new StringBuilder()
                  .append(SystemUtil.getUserInfo().getCurrentDir())
                  .append(File.separator)
                  .append(MainAction.UpdateFaceLib)
                  .toString();
          File updatefacef=new File(updatefacelibpath);
          if(!updatefacef.exists()){
            updatefacef.mkdirs();
          }
          String updateLib=updatefacelibpath+File.separator+"updateFaceLib.json";
          updatefacelib.szFindResultPath= Arrays.copyOf(updateLib.getBytes(),128);
          boolean getupdateFaceLib;
          if(Platform.isWindows()){
            getupdateFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(ulIdentifyId, updatefacelib);
          }else {
            getupdateFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(ulIdentifyId, updatefacelib);
          }
          //人脸库
          Integer updatefacelibType=null;//人脸库类型
          Integer updatefacelibID=null;//人脸库id
          String  updatefacelibName=null;//人脸库名称
          Integer updatefacelibThreshold=null;//布控的阀值
          if (getupdateFaceLib){
            System.out.println("查询编辑人脸库成功---------");
            String updategetfacelibpath = new String(new File(".").getCanonicalPath() + File.separator +"updateFaceLib"+File.separator+"updateFaceLib.json");
            String updateFaceLibjson = readFile(updategetfacelibpath);
            JSONObject updateFaceLiobject=new JSONObject(updateFaceLibjson);
            JSONArray updateFaceLiobjectArry = updateFaceLiobject.getJSONArray("FaceListsArry");
            System.out.println("人脸库集合："+updateFaceLiobjectArry);
            JSONObject updatefacelibinfo=updateFaceLiobjectArry.getJSONObject(0);
            updatefacelibName=updatefacelibinfo.getStr("FaceListName");
            updatefacelibType = Integer.parseInt(updatefacelibinfo.getStr("FaceListType"));
            updatefacelibID = Integer.parseInt(updatefacelibinfo.getStr("ID"));
            updatefacelibThreshold=Integer.parseInt(updatefacelibinfo.getStr("Threshold"));
          }else {
            updatefacelibName = "facelib";
            updatefacelibType = 2;
            updatefacelibThreshold = 90;
            updatefacelibID = 1111;
            log.error("设备还未上线-------------------");
          }
          //查询facelib的人脸信息
          String faceinfoPath=new StringBuilder()
                  .append(SystemUtil.getUserInfo().getCurrentDir())
                  .append(File.separator)
                  .append(MainAction.UpdateFaceInfo)
                  .toString();
          File face=new File(faceinfoPath);
          if(!face.exists()){
            face.mkdirs();
          }
          PU_FACE_INFO_FIND_S faceInfoFindS = new PU_FACE_INFO_FIND_S();
          faceInfoFindS.ulChannelId=new NativeLong(101);
          String faceInfoPath=faceinfoPath+"/updatefaceInfo.json";
          faceInfoFindS.szFindResultPath= Arrays.copyOf(faceInfoPath.getBytes(),129);
          PU_FACE_LIB_S facelib2 = new PU_FACE_LIB_S();
          facelib2.ulFaceLibID=new NativeLong(updatefacelibID);
          if(Platform.isWindows()){
            facelib2.szLibName=Arrays.copyOf(updatefacelibName.getBytes("gbk"),65);
          }else {
            facelib2.szLibName=Arrays.copyOf(updatefacelibName.getBytes("utf8"),65);
          }
          facelib2.enLibType=updatefacelibType;
          facelib2.uiThreshold=new NativeLong(updatefacelibThreshold);
          facelib2.isControl=true;
          FACE_FIND_CONDITION faceFindCondition=new FACE_FIND_CONDITION();
          faceFindCondition.enFeatureStatus=4;
          faceFindCondition.szName= ByteBuffer.allocate(64).put("".getBytes()).array();
          faceFindCondition.szProvince=ByteBuffer.allocate(32).put("".getBytes()).array();
          faceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
          faceFindCondition.szCardID=ByteBuffer.allocate(32).put("".getBytes()).array();
          faceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
          faceFindCondition.enGender=-1;
          faceFindCondition.enCardType=-1;
          faceInfoFindS.stCondition=faceFindCondition;
          faceInfoFindS.stFacelib=facelib2;
          faceInfoFindS.uStartIndex=0;
          boolean getFace;
          if(Platform.isWindows()){
            getFace =HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(ulIdentifyId, faceInfoFindS);
            HuaWeiSdkApi.printReturnMsg();
          }else {
            getFace =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(ulIdentifyId, faceInfoFindS);
          }
          String ulFaceId=null;
          if (getFace){
            System.out.println("查询编辑人脸信息成功");
            //查询到人脸信息json数据
            String getfaceInfoPath = new String(new File(".").getCanonicalPath() + File.separator+"updatefaceInfo"+File.separator+"updatefaceInfo.json");
            String facejson = readFile(getfaceInfoPath);
            JSONObject objectface=new JSONObject(facejson);
            JSONArray faceRecordArry = objectface.getJSONArray("FaceRecordArry");
            log.error("编辑不过期时查询到的人脸信息集合faceRecordArry："+faceRecordArry);
            if (faceRecordArry.size()>0){
              ArrayList<FaceInfo> faceInfos=new ArrayList<>();
              for (int i=0; i<faceRecordArry.size();i++){
                FaceInfo updatefaceInfo=new FaceInfo();
                JSONObject faceInfojson=faceRecordArry.getJSONObject(i);
                updatefaceInfo.setFaceid(faceInfojson.getStr("ID"));
                String userName=faceInfojson.getStr("Name");
                System.out.println("用户名："+userName);
                updatefaceInfo.setUserName(userName);
                String gender=faceInfojson.getStr("Gender");
                updatefaceInfo.setGender(Integer.parseInt(gender));
                String birthday=faceInfojson.getStr("Birthday");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date birthday1 = sdf.parse(birthday);
                updatefaceInfo.setBirthday(birthday1);
                String province=faceInfojson.getStr("Province");
                updatefaceInfo.setProvince(province);
                String city = faceInfojson.getStr("City");
                updatefaceInfo.setCity(city);
                String cardType = faceInfojson.getStr("CardType");
                updatefaceInfo.setCardType(Integer.parseInt(cardType));
                String cardID = faceInfojson.getStr("CardID");
                updatefaceInfo.setCardId(cardID);
                faceInfos.add(updatefaceInfo);
              }
              System.out.println("原来的人脸信息"+oldFaceInfo);
              log.error("编辑不过期时从摄像头查询到的人脸集合对象："+faceInfos);
              for (FaceInfo houtaifaceInfo:faceInfos){
                if (houtaifaceInfo.getUserName().equals(oldFaceInfo.getUserName())){
                  ulFaceId=houtaifaceInfo.getFaceid();
                  break;
                }else {
                  ulFaceId="11111";
                  log.debug("更新的当前人脸与摄像头中的人脸名字不对应");
                }
              }
            }else {
              ulFaceId="11111";
              log.debug("更新人脸失败摄像头人脸为空");
            }
          }else {
            ulFaceId="11111";
            log.debug("当前设备未上线,没有查到人脸信息");
          }
          /**
           * 修改时没有上传人脸头像，直接修改信息就行,上传了人脸头像,需要删掉摄像头中的人脸信息，重新添加到摄像头
           */
          if (SummitTools.stringNotNull(base64Str)){//上传了人脸头像,需要删掉摄像头中的人脸信息，重新添加到摄像头
            PU_FACE_INFO_DELETE_S faceInfoDelete=new PU_FACE_INFO_DELETE_S();
            int[] faceID = new int[100];
            faceID[0]=Integer.parseInt(ulFaceId);
            faceInfoDelete.uFaceID= faceID;
            faceInfoDelete.uFaceNum=1;
            faceInfoDelete.ulChannelId=new NativeLong(101);
            PU_FACE_LIB_S facelib = new PU_FACE_LIB_S();
            facelib.ulFaceLibID=new NativeLong(updatefacelibID);
            if(Platform.isWindows()){
              facelib.szLibName=Arrays.copyOf(updatefacelibName.getBytes("gbk"),65);
            }else {
              facelib.szLibName=Arrays.copyOf(updatefacelibName.getBytes("utf8"),65);
            }
            facelib.enLibType=updatefacelibType;
            facelib.uiThreshold=new NativeLong(updatefacelibThreshold);
            faceInfoDelete.stFacelib=facelib;
            boolean delFace;
            if(Platform.isWindows()){
              delFace = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(ulIdentifyId, faceInfoDelete);
              log.debug("上传头像编辑删除人脸信息返回码:");
              HuaWeiSdkApi.printReturnMsg();
              long returnMsg = HuaWeiSdkApi.printReturnMsg();
              if (returnMsg==106){
                printReturnMsg2=106;
              }
            }else {
              delFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(ulIdentifyId, faceInfoDelete);
              log.debug("上传头像编辑删除人脸信息返回码:");
              HuaWeiSdkApi.printReturnMsg();
              long returnMsg = HuaWeiSdkApi.printReturnMsg();
              if (returnMsg==106){
                printReturnMsg2=106;
              }
            }
            if (delFace){//删除成功,在把这个人脸添加到摄像头
              PU_FACE_LIB_S addFaceLib=new PU_FACE_LIB_S();
              addFaceLib.enLibType=updatefacelibType;
              addFaceLib.isControl=true;
              addFaceLib.ulFaceLibID=new NativeLong(updatefacelibID);
              if (Platform.isWindows()){
                addFaceLib.szLibName=Arrays.copyOf(updatefacelibName.getBytes("gbk"),65);
              }else {
                addFaceLib.szLibName=Arrays.copyOf(updatefacelibName.getBytes("utf8"),65);
              }
              addFaceLib.uiThreshold=new NativeLong(updatefacelibThreshold);
              //设置添加人脸信息的对象
              PU_FACE_INFO_ADD_S  puFaceInfoAdd = new PU_FACE_INFO_ADD_S();
              puFaceInfoAdd.stFacelib=addFaceLib;
              puFaceInfoAdd.ulChannelId=new NativeLong(101);
              //设置人脸信息
              PU_FACE_RECORD addfaceInfo=new PU_FACE_RECORD();
              addfaceInfo.enCardType=faceInfo.getCardType();
              addfaceInfo.enGender=faceInfo.getGender();
              SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
              String birthday = sdf.format(faceInfo.getBirthday());
              addfaceInfo.szBirthday=Arrays.copyOf(birthday.getBytes(),32);
              addfaceInfo.szCardID=Arrays.copyOf(faceInfo.getCardId().getBytes(),32);
              if(Platform.isWindows()){
                addfaceInfo.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("gbk"),48);
                addfaceInfo.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("gbk"),64);
                addfaceInfo.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("gbk"),32);
              }else {
                addfaceInfo.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("utf8"),48);
                addfaceInfo.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("utf8"),64);
                addfaceInfo.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("utf8"),32);
              }
              String entryabsolutePath = new String(new File(".").getCanonicalPath() + faceUrl);
              addfaceInfo.szPicPath=Arrays.copyOf(entryabsolutePath.getBytes(),128);
              puFaceInfoAdd.stRecord=addfaceInfo;
              String filename = faceInfo.getFaceImage().substring(faceInfo.getFaceImage().lastIndexOf("/")+1);
              boolean addOneFaceV2;
              if(Platform.isWindows()){
                addOneFaceV2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(ulIdentifyId, puFaceInfoAdd, filename);
                log.debug("上传头像编辑添加人脸信息返回码:");
                HuaWeiSdkApi.printReturnMsg();
                long  entryfaceRepeat = HuaWeiSdkApi.printReturnMsg();
                if (entryfaceRepeat==12108){
                  printReturnMsg2=12108;
                }
              }else {
                addOneFaceV2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(ulIdentifyId, puFaceInfoAdd, filename);
                log.debug("上传头像编辑添加人脸信息返回码:");
                HuaWeiSdkApi.printReturnMsg();
                long  entryfaceRepeat = HuaWeiSdkApi.printReturnMsg();
                if (entryfaceRepeat==12108){
                  printReturnMsg2=12108;
                }
              }
              /**
               * 添加完提取人脸特征值
               */
              if (addOneFaceV2){
                PU_FACE_LIB_S faceLib=new PU_FACE_LIB_S();
                faceLib.enLibType=updatefacelibType;
                faceLib.isControl=true;
                faceLib.ulFaceLibID=new NativeLong(updatefacelibID);
                if (Platform.isWindows()){
                  faceLib.szLibName=Arrays.copyOf(updatefacelibName.getBytes("gbk"),65);
                }else {
                  faceLib.szLibName=Arrays.copyOf(updatefacelibName.getBytes("utf8"),65);
                }
                faceLib.uiThreshold=new NativeLong(updatefacelibThreshold);
                PU_FACE_FEATURE_EXTRACT_S faceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                faceFeatureExtractS.stFacelib=faceLib;
                faceFeatureExtractS.ulChannelId=new NativeLong(101);
                boolean exitgetTeZheng;
                if (Platform.isWindows()){
                  exitgetTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(ulIdentifyId,faceFeatureExtractS);
                  log.debug("编辑提取人脸特征值返回码------");
                  HuaWeiSdkApi.printReturnMsg();
                }else {
                  exitgetTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(ulIdentifyId,faceFeatureExtractS);
                  log.debug("编辑提取人脸特征值返回码------");
                  HuaWeiSdkApi.printReturnMsg();
                }
                if (exitgetTeZheng){
                  log.debug("编辑提取人脸特征值成功------");
                }else {
                  log.error("编辑提取人脸特征值失败,图片不规范");
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                /**
                 * 添加完不管提取是否成功，都接着布控
                 */
                PU_FACE_LIB_SET_S puFaceLibSetS2 =new PU_FACE_LIB_SET_S();
                puFaceLibSetS2.enOptType=2;//修改人脸库
                PU_FACE_LIB_S  entrystFacelib2=new PU_FACE_LIB_S();
                if(Platform.isWindows()){
                  entrystFacelib2.szLibName=Arrays.copyOf("facelib".getBytes("GBK"),65);//windows名单库的名称
                }else {
                  entrystFacelib2.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);//名单库的名称
                }
                entrystFacelib2.uiThreshold=new NativeLong(90);//布控的阀值
                entrystFacelib2.enLibType=2;//人脸库类型2为白名单
                entrystFacelib2.isControl=true;//修改为布控
                entrystFacelib2.ulFaceLibID=new NativeLong(updatefacelibID);
                puFaceLibSetS2.stFacelib=entrystFacelib2;
                puFaceLibSetS2.ulChannelId=new NativeLong(101);
                boolean entrybukong;
                if(Platform.isWindows()){
                  entrybukong = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(ulIdentifyId, puFaceLibSetS2);
                  log.debug("编辑添加之后布控的返回码");
                  HuaWeiSdkApi.printReturnMsg();
                }else {
                  entrybukong = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(ulIdentifyId, puFaceLibSetS2);
                  log.debug("编辑添加之后布控的返回码");
                  HuaWeiSdkApi.printReturnMsg();
                }
              }else {
                log.error("编辑人脸上传头像时，添加人脸失败");
              }
            }else {
              if (printReturnMsg2 !=null && printReturnMsg2==106){
                log.error("上传头像编辑删除人脸失败，错误的用户ID号");
              }else {
                log.error("上传头像编辑删除人脸失败，参数错误");
              }
            }
          }else {//没有上传人脸头像
            PU_FACE_INFO_MODIFY_S  updateFaceInfo=new PU_FACE_INFO_MODIFY_S();
            updateFaceInfo.ulChannelId=new NativeLong(101);
            PU_FACE_LIB_S stFacelib=new PU_FACE_LIB_S();
            stFacelib.ulFaceLibID=new NativeLong(updatefacelibID);
            stFacelib.uiThreshold=new NativeLong(updatefacelibThreshold);
            stFacelib.enLibType=updatefacelibType;
            if (Platform.isWindows()){
              stFacelib.szLibName=Arrays.copyOf(updatefacelibName.getBytes("gbk"),65);//名单库的名称
            }else {
              stFacelib.szLibName=Arrays.copyOf(updatefacelibName.getBytes("utf8"),65);//名单库的名称
            }
            stFacelib.isControl=true;
            updateFaceInfo.stFacelib=stFacelib;
            PU_FACE_RECORD puFaceRecord=new PU_FACE_RECORD();
            if (Platform.isWindows()){
              puFaceRecord.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("gbk"),32);
              puFaceRecord.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("gbk"),64);
              puFaceRecord.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("gbk"),48);
            }else {
              puFaceRecord.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("utf8"),32);
              puFaceRecord.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("utf8"),64);
              puFaceRecord.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("utf8"),48);
            }
            if (SummitTools.stringNotNull(base64Str) && base64Str!=null && base64Str !=""){
              String absolutePath = new String(new File(".").getCanonicalPath() + faceUrl);
              System.out.println(absolutePath+"图片路径");
              puFaceRecord.szPicPath=Arrays.copyOf("".getBytes(),128);
            }else {
              String absolutePath = new String(new File(".").getCanonicalPath() +oldFaceInfo.getFaceImage());
              System.out.println(absolutePath+"图片路径");
              puFaceRecord.szPicPath=Arrays.copyOf(absolutePath.getBytes(),128);
            }
            puFaceRecord.szCardID=Arrays.copyOf(faceInfo.getCardId().getBytes(),32);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String birthday = sdf.format(faceInfo.getBirthday());
            System.out.println(birthday);
            puFaceRecord.szBirthday=Arrays.copyOf(birthday.getBytes(),32);
            puFaceRecord.enGender=faceInfo.getGender();
            puFaceRecord.enCardType=faceInfo.getCardType();
            System.out.println("id:"+ulFaceId);
            puFaceRecord.ulFaceId=new NativeLong(Integer.parseInt(ulFaceId));
            updateFaceInfo.stRecord=puFaceRecord;
            boolean update;
            if(Platform.isWindows()){
              update = HWPuSDKLibrary.INSTANCE.IVS_PU_FaceInfoModify(ulIdentifyId, updateFaceInfo);
              long returnMsg = HuaWeiSdkApi.printReturnMsg();
              if (returnMsg==106){
                printReturnMsg2=106;
              }
            }else {
              update = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FaceInfoModify(ulIdentifyId, updateFaceInfo);
              long returnMsg = HuaWeiSdkApi.printReturnMsg();
              if (returnMsg==106){
                printReturnMsg2=106;
              }
            }
            if(update){
              System.out.println("更新摄像头人脸信息成功");
              //不管是否更新成功都提取特征值
              //提取特征值
              PU_FACE_FEATURE_EXTRACT_S puFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
              puFaceFeatureExtractS.stFacelib=stFacelib;
              puFaceFeatureExtractS.ulChannelId=new NativeLong(101);
              //puFaceFeatureExtractS.taskID=new NativeLong(1);
              boolean getTeZheng;
              if(Platform.isWindows()){
                getTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(ulIdentifyId,puFaceFeatureExtractS);
              }else {
                getTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(ulIdentifyId,puFaceFeatureExtractS);
              }
              //接着布控
              PU_FACE_LIB_SET_S puFaceLibSetS2 =new PU_FACE_LIB_SET_S();
              puFaceLibSetS2.enOptType=2;//修改facelib
              PU_FACE_LIB_S  stFacelib2=new PU_FACE_LIB_S();
              if(Platform.isWindows()){
                stFacelib2.szLibName=Arrays.copyOf(updatefacelibName.getBytes("gbk"),65);//windows名单库的名称
              }else {
                stFacelib2.szLibName=Arrays.copyOf(updatefacelibName.getBytes("utf8"),65);//名单库的名称
              }
              stFacelib2.uiThreshold=new NativeLong(updatefacelibThreshold);//布控的阀值
              stFacelib2.enLibType=updatefacelibType;//facelib类型2为白名单
              stFacelib2.isControl=true;//修改为布控
              stFacelib2.ulFaceLibID=new NativeLong(updatefacelibID);
              puFaceLibSetS2.stFacelib=stFacelib2;
              puFaceLibSetS2.ulChannelId=new NativeLong(101);
              boolean bukong;
              if(Platform.isWindows()){
                bukong = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(ulIdentifyId, puFaceLibSetS2);
              }else {
                bukong = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(ulIdentifyId, puFaceLibSetS2);
              }
            }else{
              if (printReturnMsg2 !=null && printReturnMsg2==106){
                log.error("更新摄像头人脸信息失败,设备未上线");
              }else {
                log.error("更新摄像头人脸信息失败,参数错误");
              }
            }
          }
        }
        if (printReturnMsg2 !=null && printReturnMsg2==106){
          return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"更新人脸信息一部分成功,部分设备未上线",null);
        }else if (printReturnMsg2 !=null && printReturnMsg2==12108){
          return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"更新人脸信息一部分成功,人脸头像重复",null);
        }else {
          return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新人脸信息,同时更新摄像头全部成功",null);
        }
      }
    }
    return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新人脸信息成功",null);

  }



  @ApiOperation(value = "删除人脸信息，参数为id数组",notes = "根据人脸id删除人脸信息")
  @DeleteMapping(value = "/delfaceInfoByIdBatch")
  public RestfulEntityBySummit<String> delFaceInfo(@ApiParam(value = "人脸信息的id",required = true)@RequestParam(value = "faceInfoIds",required = false)List<String> faceInfoIds) throws ParseException, IOException {
    if(faceInfoIds==null || faceInfoIds.isEmpty()){
        log.error("人脸信息id为空");
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸信息id为空",null);
    }
    /**
     *1  删除人脸时候，如果这个人脸已经授权，并且和摄像头挂钩，分两种情况，一种是这个人脸已经过期，这时候只是删除数据库的人脸，
     * 如果没有过期，这时候数据库和摄像头同时删除
     * 2 如果这个人脸没有授权，删除只是单纯的在自己数据库中删除
     */
    String msg="删除人脸信息失败";
    List<String> notAuthorityfaceids=new ArrayList<>();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    Date date = new Date();
    String nowtime = df.format(date);
    long nowDate = df.parse(nowtime).getTime();
    Integer printReturnMsg=null;//返回码
    for(String faceInfoId:faceInfoIds){
      List<AccessControlInfo> accessControlInfos = faceInfoAccCtrlService.seleAccCtrlInfoByFaceID(faceInfoId);
      if(!CommonUtil.isEmptyList(accessControlInfos)){//确定已经授权
        FaceInfo faceInfo = faceInfoManagerService.selectFaceInfoByID(faceInfoId);
        Date faceEndTime = faceInfo.getFaceEndTime();
        long faceEndDate = faceEndTime.getTime();
        if (nowDate>faceEndDate){//说明已经过期
          notAuthorityfaceids.add(faceInfoId);
        }else {//说明没有过期和摄像头一起删除,同时删除授权关系
          notAuthorityfaceids.add(faceInfoId);
          int i1 = faceInfoAccCtrlService.deleteFaceAccCtrlByFaceId(faceInfoId);//删除授权关系
          for (AccessControlInfo accessControlInfo : accessControlInfos) {
            String entryCameraIp = accessControlInfo.getEntryCameraIp();
            String exitCameraIp = accessControlInfo.getExitCameraIp();
            DeviceInfo entrydeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(entryCameraIp);
            DeviceInfo exitdeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(exitCameraIp);
            NativeLong entryulIdentifyId;
            NativeLong exitulIdentifyId;
            if (entrydeviceInfo == null ) {
              entryulIdentifyId = new NativeLong(111);
            }else {
              entryulIdentifyId = entrydeviceInfo.getUlIdentifyId();
            }
            if (exitdeviceInfo == null){
              exitulIdentifyId = new NativeLong(112);
            }else {
              exitulIdentifyId = exitdeviceInfo.getUlIdentifyId();
            }
            //出口
            Integer exitenLibType=null;//人脸库类型
            Integer exitulFaceLibID=null;//人脸库id
            String exitszLibName=null;//人脸库名称
            Integer exituiThreshold=null;//布控的阀值
            //入口
            Integer entryenLibType=null;//人脸库类型
            Integer entryulFaceLibID=null;//人脸库id
            String entryszLibName=null;//人脸库名称
            Integer entrytuiThreshold=null;//布控的阀值
            /**
             * 先查询出口人脸库
             */
            PU_FACE_LIB_GET_S delExitfaceLibGetS =new PU_FACE_LIB_GET_S();
            delExitfaceLibGetS.ulChannelId=new NativeLong(101);
            delExitfaceLibGetS.ulFaceLibNum=new NativeLong(1);
            String delExitfacelibPath=new StringBuilder()
                    .append(SystemUtil.getUserInfo().getCurrentDir())
                    .append(File.separator)
                    .append(MainAction.DelExitFaceLib)
                    .toString();
            File delExitf=new File(delExitfacelibPath);
            if(!delExitf.exists()){
              delExitf.mkdirs();
            }
            String delExitfaceLib=delExitfacelibPath+File.separator+"delExitFaceLib.json";
            delExitfaceLibGetS.szFindResultPath= Arrays.copyOf(delExitfaceLib.getBytes(),128);
            /**
             * 再查询入口人脸库
             */
            PU_FACE_LIB_GET_S delEntryfaceLibGetS =new PU_FACE_LIB_GET_S();
            delEntryfaceLibGetS.ulChannelId=new NativeLong(101);
            delEntryfaceLibGetS.ulFaceLibNum=new NativeLong(1);
            String delEntryfacelibPath=new StringBuilder()
                    .append(SystemUtil.getUserInfo().getCurrentDir())
                    .append(File.separator)
                    .append(MainAction.DelEntryFaceLib)
                    .toString();
            File delEntrytf=new File(delEntryfacelibPath);
            if(!delEntrytf.exists()){
              delEntrytf.mkdirs();
            }
            String delEntryfaceLib=delEntryfacelibPath+File.separator+"delEntryFaceLib.json";
            delEntryfaceLibGetS.szFindResultPath= Arrays.copyOf(delEntryfaceLib.getBytes(),128);
            boolean delEntrygetFaceLib;
            boolean delExitgetFaceLib;
            if(Platform.isWindows()){
              delEntrygetFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(entryulIdentifyId, delEntryfaceLibGetS);
              delExitgetFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(exitulIdentifyId, delExitfaceLibGetS);
            }else {
              delEntrygetFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(entryulIdentifyId, delEntryfaceLibGetS);
              delExitgetFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(exitulIdentifyId, delExitfaceLibGetS);
            }
              if (entrydeviceInfo==null){
                entryszLibName="facelib";
                entryenLibType=2;
                entrytuiThreshold=90;
                entryulFaceLibID=1111;
              }else if (delEntrygetFaceLib){
                System.out.println("查询入口库人脸库成功------------------------");
                String entrygetfacelibpath1 = new String(new File(".").getCanonicalPath() + File.separator +"delEntryFaceLib"+File.separator+"delEntryFaceLib.json");
                String entryjson = readFile(entrygetfacelibpath1);
                JSONObject entryobject=new JSONObject(entryjson);
                JSONArray entryfaceListsArry = entryobject.getJSONArray("FaceListsArry");
                System.out.println("人脸库集合："+entryfaceListsArry);
                JSONObject entrynewfacelibinfo=entryfaceListsArry.getJSONObject(0);
                entryszLibName=entrynewfacelibinfo.getStr("FaceListName");
                entryenLibType = Integer.parseInt(entrynewfacelibinfo.getStr("FaceListType"));
                entryulFaceLibID = Integer.parseInt(entrynewfacelibinfo.getStr("ID"));
                entrytuiThreshold=Integer.parseInt(entrynewfacelibinfo.getStr("Threshold"));
              }
              if (exitdeviceInfo==null){
                exitszLibName="facelib";
                exitenLibType=2;
                exituiThreshold=90;
                exitulFaceLibID=1111;
              }else if (delExitgetFaceLib){
                System.out.println("查询新建出口的人脸库成功---------------");
                String exitgetfacelibpath = new String(new File(".").getCanonicalPath() + File.separator +"delExitFaceLib"+File.separator+"delExitFaceLib.json");
                String exitnewFaceLibjson = readFile(exitgetfacelibpath);
                JSONObject exitnewFaceLiobject=new JSONObject(exitnewFaceLibjson);
                JSONArray exitnewFaceLiobjectArry = exitnewFaceLiobject.getJSONArray("FaceListsArry");
                System.out.println("人脸库集合："+exitnewFaceLiobjectArry);
                JSONObject exitnewfacelibinfo=exitnewFaceLiobjectArry.getJSONObject(0);
                exitszLibName=exitnewfacelibinfo.getStr("FaceListName");
                exitenLibType = Integer.parseInt(exitnewfacelibinfo.getStr("FaceListType"));
                exitulFaceLibID = Integer.parseInt(exitnewfacelibinfo.getStr("ID"));
                exituiThreshold=Integer.parseInt(exitnewfacelibinfo.getStr("Threshold"));
              }

            //查询第一个facelib的人脸信息
            System.out.println("查询出口的人脸信息-------------------------------------------");
            String delexitfaceInfoPath = new StringBuilder()
                    .append(SystemUtil.getUserInfo().getCurrentDir())
                    .append(File.separator)
                    .append(MainAction.DelExitFaceInfo)
                    .toString();
            File delexitfaceInfo = new File(delexitfaceInfoPath);
            if (!delexitfaceInfo.exists()) {
              delexitfaceInfo.mkdirs();
            }
            PU_FACE_INFO_FIND_S delExitfaceInfoFindS = new PU_FACE_INFO_FIND_S();
            delExitfaceInfoFindS.ulChannelId = new NativeLong(101);
            String delExitfaceInfoPath = delexitfaceInfoPath + "/delExitfaceInfo.json";
            delExitfaceInfoFindS.szFindResultPath = Arrays.copyOf(delExitfaceInfoPath.getBytes(), 129);
            PU_FACE_LIB_S delExitfacelib = new PU_FACE_LIB_S();
            delExitfacelib.ulFaceLibID = new NativeLong(exitulFaceLibID);
            if (Platform.isWindows()) {
              delExitfacelib.szLibName = Arrays.copyOf(exitszLibName.getBytes("gbk"), 65);
            } else {
              delExitfacelib.szLibName = Arrays.copyOf(exitszLibName.getBytes("utf8"), 65);
            }
            delExitfacelib.enLibType = exitenLibType;
            delExitfacelib.uiThreshold = new NativeLong(exituiThreshold);
            delExitfacelib.isControl=true;
            FACE_FIND_CONDITION delExitfaceFindCondition = new FACE_FIND_CONDITION();
            delExitfaceFindCondition.enFeatureStatus = 1;
            delExitfaceFindCondition.szName = ByteBuffer.allocate(64).put("".getBytes()).array();
            delExitfaceFindCondition.szProvince = ByteBuffer.allocate(32).put("".getBytes()).array();
            delExitfaceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
            delExitfaceFindCondition.szCardID = ByteBuffer.allocate(32).put("".getBytes()).array();
            delExitfaceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
            delExitfaceFindCondition.enGender = -1;
            delExitfaceFindCondition.enCardType = -1;
            delExitfaceInfoFindS.stCondition = delExitfaceFindCondition;
            delExitfaceInfoFindS.stFacelib = delExitfacelib;
            delExitfaceInfoFindS.uStartIndex = 0;
            boolean exitdelFace;
            if (Platform.isWindows()) {
              exitdelFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, delExitfaceInfoFindS);
            } else {
              exitdelFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, delExitfaceInfoFindS);
              log.error("删除时查询出口的人脸信息返回码：");
              HuaWeiSdkApi.printReturnMsg();
            }
            /**
             *  --------查询到查询入口的人脸信息json数据
             */
            System.out.println("查询入口的人脸信息-------------------------------------------");
            String delentryfaceInfoPath = new StringBuilder()
                    .append(SystemUtil.getUserInfo().getCurrentDir())
                    .append(File.separator)
                    .append(MainAction.DelEntryFaceInfo)
                    .toString();
            File delentryfaceInfo = new File(delentryfaceInfoPath);
            if (!delentryfaceInfo.exists()) {
              delentryfaceInfo.mkdirs();
            }
            PU_FACE_INFO_FIND_S delEntryfaceInfoFindS = new PU_FACE_INFO_FIND_S();
            delEntryfaceInfoFindS.ulChannelId = new NativeLong(101);
            String delEntryfaceInfoPath = delentryfaceInfoPath + "/delEntryfaceInfo.json";
            delEntryfaceInfoFindS.szFindResultPath = Arrays.copyOf(delEntryfaceInfoPath.getBytes(), 129);
            PU_FACE_LIB_S delEntryfacelib = new PU_FACE_LIB_S();
            delEntryfacelib.ulFaceLibID = new NativeLong(entryulFaceLibID);
            if (Platform.isWindows()) {
              delEntryfacelib.szLibName = Arrays.copyOf(entryszLibName.getBytes("gbk"), 65);
            } else {
              delEntryfacelib.szLibName = Arrays.copyOf(entryszLibName.getBytes("utf8"), 65);
            }
            delEntryfacelib.enLibType = entryenLibType;
            delEntryfacelib.uiThreshold = new NativeLong(entrytuiThreshold);
            delEntryfacelib.isControl=true;
            FACE_FIND_CONDITION delEntryfaceFindCondition = new FACE_FIND_CONDITION();
            delEntryfaceFindCondition.enFeatureStatus = 1;
            delEntryfaceFindCondition.szName = ByteBuffer.allocate(64).put("".getBytes()).array();
            delEntryfaceFindCondition.szProvince = ByteBuffer.allocate(32).put("".getBytes()).array();
            delEntryfaceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
            delEntryfaceFindCondition.szCardID = ByteBuffer.allocate(32).put("".getBytes()).array();
            delEntryfaceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
            delEntryfaceFindCondition.enGender = -1;
            delEntryfaceFindCondition.enCardType = -1;
            delEntryfaceInfoFindS.stCondition = delEntryfaceFindCondition;
            delEntryfaceInfoFindS.stFacelib = delEntryfacelib;
            delEntryfaceInfoFindS.uStartIndex = 0;
            boolean entrytdelFace;
            if (Platform.isWindows()) {
              entrytdelFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, delEntryfaceInfoFindS);
            } else {
              entrytdelFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, delEntryfaceInfoFindS);
              log.error("删除时查询入口的人脸信息返回码：");
              HuaWeiSdkApi.printReturnMsg();
            }
            if (entrytdelFace ||  exitdelFace) {
              log.debug("需要删除的人脸信息查询人脸信息成功");
              /**
               *  -------查询到出口人脸信息json数据
               */
              if (exitdelFace){
                String delExitfaceInfo = delexitfaceInfoPath+File.separator+"delExitfaceInfo.json";
                String delExitfacejson =readFile(delExitfaceInfo);
                JSONObject delExitobjectface = new JSONObject(delExitfacejson);
                JSONArray delExitfaceRecordArry = delExitobjectface.getJSONArray("FaceRecordArry");
                log.debug("需要删除的人脸信息delExitfaceRecordArry集合：" + delExitfaceRecordArry);
                ArrayList<FaceInfo> delExitHouTaiFaceInfos = new ArrayList<>();
                for (int i = 0; i < delExitfaceRecordArry.size(); i++) {
                  FaceInfo delExitFaceInfo = new FaceInfo();
                  JSONObject exitfaceInfojson = delExitfaceRecordArry.getJSONObject(i);
                  delExitFaceInfo.setFaceid(exitfaceInfojson.getStr("ID"));
                  String userName = exitfaceInfojson.getStr("Name");
                  delExitFaceInfo.setUserName(userName);
                  String gender = exitfaceInfojson.getStr("Gender");
                  delExitFaceInfo.setGender(Integer.parseInt(gender));
                  String exitbirthday = exitfaceInfojson.getStr("Birthday");
                  SimpleDateFormat exitsdf = new SimpleDateFormat("yyyy-MM-dd");
                  Date exitbirthdayDate = exitsdf.parse(exitbirthday);
                  delExitFaceInfo.setBirthday(exitbirthdayDate);
                  String province = exitfaceInfojson.getStr("Province");
                  delExitFaceInfo.setProvince(province);
                  String city = exitfaceInfojson.getStr("City");
                  delExitFaceInfo.setCity(city);
                  String cardType = exitfaceInfojson.getStr("CardType");
                  delExitFaceInfo.setCardType(Integer.parseInt(cardType));
                  String cardID = exitfaceInfojson.getStr("CardID");
                  delExitFaceInfo.setCardId(cardID);
                  delExitHouTaiFaceInfos.add(delExitFaceInfo);
                }
                System.out.println("需要删除的出口---------人脸信息合对象：" + delExitHouTaiFaceInfos);
                String exitfaceID=null;
                if (!CommonUtil.isEmptyList(delExitHouTaiFaceInfos)){
                  for (FaceInfo delExitHouTaiFaceInfo : delExitHouTaiFaceInfos) {
                    if (faceInfo.getUserName().equals(delExitHouTaiFaceInfo.getUserName())) {
                      exitfaceID = delExitHouTaiFaceInfo.getFaceid();
                      PU_FACE_INFO_DELETE_S exitpuFaceInfoDeleteS = new PU_FACE_INFO_DELETE_S();
                      int[] exituFaceID = new int[100];
                      exituFaceID[0] = Integer.parseInt(exitfaceID);
                      exitpuFaceInfoDeleteS.uFaceID = exituFaceID;
                      exitpuFaceInfoDeleteS.uFaceNum = 1;
                      exitpuFaceInfoDeleteS.ulChannelId = new NativeLong(101);
                      PU_FACE_LIB_S exitfacelib = new PU_FACE_LIB_S();
                      exitfacelib.ulFaceLibID = new NativeLong(exitulFaceLibID);
                      if (Platform.isWindows()) {
                        exitfacelib.szLibName = Arrays.copyOf(exitszLibName.getBytes("gbk"), 65);
                      } else {
                        exitfacelib.szLibName = Arrays.copyOf(exitszLibName.getBytes("utf8"), 65);
                      }
                      exitfacelib.enLibType = exitenLibType;
                      exitfacelib.uiThreshold = new NativeLong(exituiThreshold);
                      exitpuFaceInfoDeleteS.stFacelib = exitfacelib;
                      boolean exitdel;
                      if (Platform.isWindows()) {
                        exitdel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, exitpuFaceInfoDeleteS);
                        long returnMsg = HuaWeiSdkApi.printReturnMsg();
                        if (returnMsg==106){
                          printReturnMsg=106;
                        }
                      } else {
                        exitdel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, exitpuFaceInfoDeleteS);
                        long returnMsg = HuaWeiSdkApi.printReturnMsg();
                        if (returnMsg==106){
                          printReturnMsg=106;
                        }
                      }
                      if (exitdel) {
                        log.debug("需要删除的出口人脸信息删除成功");
                      } else {
                        log.error("需要删除的出口人脸信息删除失败");
                      }
                    }
                  }
                }
              }
              /**
               *  -------查询到入口人脸信息json数据
               */
              if (entrytdelFace){
                String delEntryfaceInfo = delentryfaceInfoPath+File.separator+"delEntryfaceInfo.json";
                String delEntryfacejson =readFile(delEntryfaceInfo);
                JSONObject delEntryobjectface = new JSONObject(delEntryfacejson);
                JSONArray delEntryfaceRecordArry = delEntryobjectface.getJSONArray("FaceRecordArry");
                log.debug("需要删除的人脸信息delEntryfaceRecordArry集合：" + delEntryfaceRecordArry);
                ArrayList<FaceInfo> delEntryHouTaiFaceInfos = new ArrayList<>();
                for (int i = 0; i < delEntryfaceRecordArry.size(); i++) {
                  FaceInfo delEntryFaceInfo = new FaceInfo();
                  JSONObject entryfaceInfojson = delEntryfaceRecordArry.getJSONObject(i);
                  delEntryFaceInfo.setFaceid(entryfaceInfojson.getStr("ID"));
                  String userName = entryfaceInfojson.getStr("Name");
                  delEntryFaceInfo.setUserName(userName);
                  String gender = entryfaceInfojson.getStr("Gender");
                  delEntryFaceInfo.setGender(Integer.parseInt(gender));
                  String entrybirthday = entryfaceInfojson.getStr("Birthday");
                  SimpleDateFormat entrysdf = new SimpleDateFormat("yyyy-MM-dd");
                  Date entrybirthdayDate = entrysdf.parse(entrybirthday);
                  delEntryFaceInfo.setBirthday(entrybirthdayDate);
                  String province = entryfaceInfojson.getStr("Province");
                  delEntryFaceInfo.setProvince(province);
                  String city = entryfaceInfojson.getStr("City");
                  delEntryFaceInfo.setCity(city);
                  String cardType = entryfaceInfojson.getStr("CardType");
                  delEntryFaceInfo.setCardType(Integer.parseInt(cardType));
                  String cardID = entryfaceInfojson.getStr("CardID");
                  delEntryFaceInfo.setCardId(cardID);
                  delEntryHouTaiFaceInfos.add(delEntryFaceInfo);
                }
                System.out.println("需要删除的入口---------人脸信息合对象：" + delEntryHouTaiFaceInfos);
                String entryfaceID=null;
                if (!CommonUtil.isEmptyList(delEntryHouTaiFaceInfos)){
                  for (FaceInfo delEntryHouTaiFaceInfo : delEntryHouTaiFaceInfos) {
                    if (faceInfo.getUserName().equals(delEntryHouTaiFaceInfo.getUserName())) {
                      entryfaceID = delEntryHouTaiFaceInfo.getFaceid();
                      PU_FACE_INFO_DELETE_S entrypuFaceInfoDeleteS = new PU_FACE_INFO_DELETE_S();
                      int[] entryuFaceID = new int[100];
                      entryuFaceID[0] = Integer.parseInt(entryfaceID);
                      entrypuFaceInfoDeleteS.uFaceID = entryuFaceID;
                      entrypuFaceInfoDeleteS.uFaceNum = 1;
                      entrypuFaceInfoDeleteS.ulChannelId = new NativeLong(101);
                      PU_FACE_LIB_S entryfacelib = new PU_FACE_LIB_S();
                      entryfacelib.ulFaceLibID = new NativeLong(entryulFaceLibID);
                      if (Platform.isWindows()) {
                        entryfacelib.szLibName = Arrays.copyOf(entryszLibName.getBytes("gbk"), 65);
                      } else {
                        entryfacelib.szLibName = Arrays.copyOf(entryszLibName.getBytes("utf8"), 65);
                      }
                      entryfacelib.enLibType = entryenLibType;
                      entryfacelib.uiThreshold = new NativeLong(entrytuiThreshold);
                      entrypuFaceInfoDeleteS.stFacelib = entryfacelib;
                      boolean entrytdel;
                      if (Platform.isWindows()) {
                        entrytdel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, entrypuFaceInfoDeleteS);
                        long returnMsg = HuaWeiSdkApi.printReturnMsg();
                        if (returnMsg==106){
                          printReturnMsg=106;
                        }
                      } else {
                        entrytdel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, entrypuFaceInfoDeleteS);
                        long returnMsg = HuaWeiSdkApi.printReturnMsg();
                        if (returnMsg==106){
                          printReturnMsg=106;
                        }
                      }
                      if (entrytdel) {
                        log.debug("需要删除的入口人脸信息删除成功");
                      } else {
                        log.error("需要删除的入口人脸信息删除失败");
                      }
                    }
                  }
                }
              }
            }else {
              log.error("查询全部人脸信息失败");
            }
          }
        }
      }else {//没有授权
        notAuthorityfaceids.add(faceInfoId);
      }
    }
    if (!CommonUtil.isEmptyList(notAuthorityfaceids)){
      try {
        faceInfoManagerService.delFaceInfoByIds(notAuthorityfaceids);
      } catch (Exception e) {
        msg=getErrorMsg(msg,e);
        log.error(msg);
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,msg,null);
      }
    }
    if (printReturnMsg !=null && printReturnMsg==106){
      return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"部分摄像头删除人脸信息成功",null);
    }else {
      return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"删除人脸信息成功",null);
    }
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



   @ApiOperation(value = "查询全部人脸信息，包括人脸信息的id和name以及人脸图片,人脸是否过期",notes = "无论有无门禁权限都全部查询")
   @GetMapping(value = "/selectAllFaceInfo")
   public RestfulEntityBySummit<List<SimpleFaceInfo>> selectAllFaceInfo(){
     List<SimpleFaceInfo> simpleFaceInfos=new ArrayList<>();
     try {
       List<FaceInfo> faceInfos=faceInfoManagerService.selectAllFaceInfo(null);
       //System.out.println(faceInfos+"TestPath");
       if(faceInfos !=null){
         for(FaceInfo faceInfo:faceInfos){
           simpleFaceInfos.add(new SimpleFaceInfo(faceInfo.getFaceid(),faceInfo.getUserName(),faceInfo.getFaceImage(),faceInfo.getIsValidTime()));
         }
       }
     } catch (Exception e) {
       e.printStackTrace();
        log.error("查询全部的人脸信息失败");
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询全部人脸信息失败",simpleFaceInfos);
     }
     return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询全部人脸信息成功",simpleFaceInfos);
   }

   @ApiOperation(value = "查询全部的省份")
   @GetMapping(value = "/selectProvince")
   public RestfulEntityBySummit<List<Province>> selectProvince(){
     List<Province> provinces=null;
     try {
        provinces = faceInfoManagerService.selectProvince(null);
     } catch (Exception e) {
       e.printStackTrace();
       log.error("查询全部的省份失败");
       return  ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询全部的省份失败",provinces);
     }
     return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询全部的省份成功",provinces);
   }

   @ApiOperation(value = "根据省份的编号查询省份所对应的所有的城市")
   @GetMapping(value = "/selectCityByProvinceId")
   public RestfulEntityBySummit<List<String>> selectCityByProvinceId(@ApiParam(value = "省份编号")@RequestParam(value = "provinceId",required = false)String provinceId){
     if(provinceId==null){
       log.error("省份编号为空");
       return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"省份编号为空",null);
     }
     List<String> cityNames=new ArrayList<>();
     try {
       List<City> cities= faceInfoManagerService.selectCityByProvinceId(provinceId);
       if(cities!=null){
         for(City city:cities){
           cityNames.add(city.getCityName());
         }
       }
     } catch (Exception e) {
       log.error("查询城市信息列表失败");
       return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询城市信息列表失败",cityNames);
     }
     return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询城市信息列表成功",cityNames);
   }

   private String getErrorMsg(String msg,Exception e){
     if(e instanceof ErrorMsgException){
      return  msg=((ErrorMsgException)e).getErrorMsg();
     }
     return  msg;
   }

  public String readFile(String path){
        BufferedReader  breader=null;
        StringBuffer sbf = new StringBuffer();
        FileInputStream fileInputStream=null;
        InputStreamReader inputStreamReader =null;
        try {
            fileInputStream = new FileInputStream(path);
            inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            breader=new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = breader.readLine()) != null){
                sbf.append(tempString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (breader !=null){
                    breader.close();
                }
                if (inputStreamReader !=null){
                    inputStreamReader.close();
                }
                if (fileInputStream !=null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sbf.toString();
  }





}
