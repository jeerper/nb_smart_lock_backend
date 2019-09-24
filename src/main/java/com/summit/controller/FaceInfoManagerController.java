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
  public RestfulEntityBySummit<Integer>  insertFaceInfo(@RequestBody FaceInfoManagerEntity faceInfoManagerEntity) throws ParseException {
       //System.out.println(faceInfoManagerEntity+"ddd");
        String  base64Str=faceInfoManagerEntity.getFaceImage();
        System.out.println("人脸图片："+base64Str);
        if(base64Str==null || base64Str.isEmpty()){
          log.error("人脸图片为空");
          return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸图片为空",null);
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
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
          Date date = new Date();
          String startTime = df.format(date);
          Date faceStartTime = CommonUtil.dateFormat.get().parse(startTime);
          faceInfo.setFaceStartTime(faceStartTime);
          Date faceEndTime = CommonUtil.dateFormat.get().parse(faceInfoManagerEntity.getFaceEndTime());
          faceInfo.setFaceEndTime(faceEndTime);

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
      refreshFaceInfoManager();
    //更新之前找到原来的人脸信息
    String selectoldfaceid = faceInfo.getFaceid();
    FaceInfo oldFaceInfo = faceInfoManagerService.selectFaceInfoByID(selectoldfaceid);
    String msg="更新人脸信息失败";
    String  base64Str=faceInfo.getFaceImage();
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
    String faceid = faceInfo.getFaceid();
    List<AccessControlInfo> accessControlInfos=faceInfoAccCtrlService.seleAccCtrlInfoByFaceID(faceid);//确定授权
    System.out.println("门禁信息"+accessControlInfos);
    if(!CommonUtil.isEmptyList(accessControlInfos)){//确定已经授权
      List<String> cameraIps=new ArrayList<>();
      for(AccessControlInfo accessControlInfo:accessControlInfos){
        String entryCameraIp = accessControlInfo.getEntryCameraIp();
        String exitCameraIp = accessControlInfo.getExitCameraIp();
        cameraIps.add(entryCameraIp);
        cameraIps.add(exitCameraIp);
      }
      System.out.println("当前人脸所关联的入口和出口摄像头ip"+cameraIps);
      if(nowDate>oldfaceEndDate && newfaceEndDate>=nowDate){//过期人脸的修改，并且如果修改的是有效期，则需要重新吧这个人脸加入到摄像头里面，如果不是有效期，则需要修改自己的数据库就可以
         for(String cameraIp:cameraIps){
           DeviceInfo deviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(cameraIp);
           NativeLong ulIdentifyId;
           if (deviceInfo==null){
             ulIdentifyId=new NativeLong(222);
           }else {
             ulIdentifyId = deviceInfo.getUlIdentifyId();
           }
           //设置人脸库对象
           PU_FACE_LIB_S stFacelib1=new PU_FACE_LIB_S();
           stFacelib1.enLibType=2;
           stFacelib1.isControl=true;
           stFacelib1.ulFaceLibID=new NativeLong(1);
           if(Platform.isWindows()){
             stFacelib1.szLibName=Arrays.copyOf("人脸库".getBytes("GBK"),65);
           }else {
             stFacelib1.szLibName=Arrays.copyOf("人脸库".getBytes("utf8"),65);
           }
           stFacelib1.uiThreshold=new NativeLong(101);
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
             HuaWeiSdkApi.printReturnMsg();
           }else {
             addface2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(ulIdentifyId, puFaceInfoAdd, filename);
           }
           if(addface2){
             System.out.println("添加人脸信息成功");
             //接着进行布控
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
             //接着布控
             PU_FACE_LIB_SET_S puFaceLibSetS2 =new PU_FACE_LIB_SET_S();
             puFaceLibSetS2.enOptType=2;//修改人脸库
             PU_FACE_LIB_S  stFacelib2=new PU_FACE_LIB_S();
             if(Platform.isWindows()){
               stFacelib2.szLibName=Arrays.copyOf("人脸库".getBytes("gbk"),65);//windows名单库的名称
             }else {
               stFacelib2.szLibName=Arrays.copyOf("人脸库".getBytes("utf8"),65);//名单库的名称
             }
             stFacelib2.uiThreshold=new NativeLong(90);//布控的阀值
             stFacelib2.enLibType=2;//人脸库类型2为白名单
             stFacelib2.isControl=true;//修改为布控
             stFacelib2.ulFaceLibID=new NativeLong(1);
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
             System.out.println("添加人脸信息失败");
             return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新人脸信息-部分成功",null);
           }
         }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新人脸信息全部成功",null);
      }else if(nowDate<oldfaceEndDate){//不是过期人脸的修改，则需要自己数据库和摄像头同时修改
        /**
         * 插入。。。
         */
        refreshFaceInfoManager();
        for(String cameraIp:cameraIps){
          DeviceInfo deviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(cameraIp);
          NativeLong ulIdentifyId;
          if (deviceInfo==null){
            ulIdentifyId=new NativeLong(222);
          }else {
            ulIdentifyId = deviceInfo.getUlIdentifyId();
          }
          System.out.println(ulIdentifyId+"222222");
          //查询人脸库的人脸信息
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
          facelib2.ulFaceLibID=new NativeLong(1);
          if(Platform.isWindows()){
            facelib2.szLibName=Arrays.copyOf("人脸库".getBytes("gbk"),65);
          }else {
            facelib2.szLibName=Arrays.copyOf("人脸库".getBytes("utf8"),65);
          }
          facelib2.enLibType=2;
          facelib2.uiThreshold=new NativeLong(90);
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
          if (getFace){
            System.out.println("查询编辑人脸信息成功");
          }
          String ulFaceId=null;
            //查询到人脸信息json数据
          String getfaceInfoPath = new String(new File(".").getCanonicalPath() + File.separator+"updatefaceInfo"+File.separator+"updatefaceInfo.json");
          String facejson = readFile(getfaceInfoPath);
          log.debug("编辑人脸信息文件路径---："+getfaceInfoPath);
          JSONObject objectface=new JSONObject(facejson);
          JSONArray faceRecordArry = objectface.getJSONArray("FaceRecordArry");
          log.error("编辑不过期时查询到的人脸信息集合faceRecordArry："+faceRecordArry);
          if(faceRecordArry.size()>0){
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
               }
             }
          }else {
              ulFaceId="11111";
              log.debug("更新人脸失败摄像头人脸为空");
          }
          PU_FACE_INFO_MODIFY_S  updateFaceInfo=new PU_FACE_INFO_MODIFY_S();
          updateFaceInfo.ulChannelId=new NativeLong(101);
          PU_FACE_LIB_S stFacelib=new PU_FACE_LIB_S();
          stFacelib.ulFaceLibID=new NativeLong(1);
          stFacelib.uiThreshold=new NativeLong(90);
          stFacelib.enLibType=2;
          if (Platform.isWindows()){
            stFacelib.szLibName=Arrays.copyOf("人脸库".getBytes("gbk"),65);//名单库的名称
          }else {
            stFacelib.szLibName=Arrays.copyOf("人脸库".getBytes("utf8"),65);//名单库的名称
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
          }else {
            update = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FaceInfoModify(ulIdentifyId, updateFaceInfo);
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
            puFaceLibSetS2.enOptType=2;//修改人脸库
            PU_FACE_LIB_S  stFacelib2=new PU_FACE_LIB_S();
            if(Platform.isWindows()){
              stFacelib2.szLibName=Arrays.copyOf("人脸库".getBytes("gbk"),65);//windows名单库的名称
            }else {
              stFacelib2.szLibName=Arrays.copyOf("人脸库".getBytes("utf8"),65);//名单库的名称
            }
            stFacelib2.uiThreshold=new NativeLong(90);//布控的阀值
            stFacelib2.enLibType=2;//人脸库类型2为白名单
            stFacelib2.isControl=true;//修改为布控
            stFacelib2.ulFaceLibID=new NativeLong(1);
            puFaceLibSetS2.stFacelib=stFacelib2;
            puFaceLibSetS2.ulChannelId=new NativeLong(101);
            boolean bukong;
            if(Platform.isWindows()){
              bukong = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(ulIdentifyId, puFaceLibSetS2);
            }else {
              bukong = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(ulIdentifyId, puFaceLibSetS2);
            }
          }else{
            System.out.println("更新摄像头人脸信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新人脸信息-部分成功",null);
          }
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"更新人脸信息全部成功",null);
      }
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
       //System.out.println(faceInfos+"TestPath");
       if(faceInfos !=null){
         for(FaceInfo faceInfo:faceInfos){
           simpleFaceInfos.add(new SimpleFaceInfo(faceInfo.getFaceid(),faceInfo.getUserName(),faceInfo.getFaceImage()));
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
    BufferedReader reader=null;
    String lastStr="";
    try {
      FileInputStream fileInputStream = new FileInputStream(path);
      InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
      reader=new BufferedReader(inputStreamReader);
      String tempString=null;
      while ((tempString = reader.readLine()) != null){
        lastStr+=tempString;
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lastStr;
  }
  public  void refreshFaceInfoManager() throws ParseException, IOException {
    QueryWrapper<FaceInfo> wrapper = new QueryWrapper<>();
    List<FaceInfo> faceInfoList = faceInfoManagerDao.selectList(wrapper);
    for (FaceInfo faceInfo : faceInfoList) {
      if (faceInfo.getFaceType() == 1) {//临时人员
        Date faceEndTime = faceInfo.getFaceEndTime();
        long faceEndDate = faceEndTime.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Calendar c = Calendar.getInstance();
        c.setTime(faceEndTime);
        c.add(Calendar.MONTH, 6);
        String banNianTime = df.format(c.getTime());
        Date banNianDate = df.parse(banNianTime);
        long banNianDateTime = banNianDate.getTime();
        Date date = new Date();
        String nowtime = df.format(date);
        long nowDate = df.parse(nowtime).getTime();
        if (nowDate > faceEndDate) {//超过截至日期，从摄像头删除
          List<AccessControlInfo> accessControlInfos = faceInfoAccCtrlService.seleAccCtrlInfoByFaceID(faceInfo.getFaceid());
          System.out.println("临时人脸关联的门禁："+accessControlInfos);
          if (!CommonUtil.isEmptyList(accessControlInfos)) {//而且确定他和摄像头挂钩
            for (AccessControlInfo accessControlInfo : accessControlInfos) {
              //找出所挂钩的入口摄像头
              String entryCameraIp = accessControlInfo.getEntryCameraIp();
              String exitCameraIp = accessControlInfo.getExitCameraIp();
              DeviceInfo entrydeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(entryCameraIp);
              DeviceInfo exitdeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(exitCameraIp);
              NativeLong entryulIdentifyId;
              NativeLong exitulIdentifyId;
              if (entrydeviceInfo == null ) {
                entryulIdentifyId = new NativeLong(1);
              }else {
                entryulIdentifyId = entrydeviceInfo.getUlIdentifyId();
              }
              if (exitdeviceInfo == null){
                exitulIdentifyId = new NativeLong(22);
              }else {
                exitulIdentifyId = exitdeviceInfo.getUlIdentifyId();
              }
              //查询第一个人脸库的人脸信息
              String realfaceinfoPath = new StringBuilder()
                      .append(SystemUtil.getUserInfo().getCurrentDir())
                      .append(File.separator)
                      .append(MainAction.RealFace)
                      .toString();
              File face = new File(realfaceinfoPath);
              if (!face.exists()) {
                face.mkdirs();
              }
              PU_FACE_INFO_FIND_S faceInfoFindS = new PU_FACE_INFO_FIND_S();
              faceInfoFindS.ulChannelId=new NativeLong(101);
              String faceInfoPath=realfaceinfoPath+File.separator+"realFace.json";
              faceInfoFindS.szFindResultPath= Arrays.copyOf(faceInfoPath.getBytes(),129);
              PU_FACE_LIB_S facelib2 = new PU_FACE_LIB_S();
              facelib2.ulFaceLibID=new NativeLong(1);
              if(Platform.isWindows()){
                facelib2.szLibName=Arrays.copyOf("人脸库".getBytes("gbk"),65);
              }else {
                facelib2.szLibName=Arrays.copyOf("人脸库".getBytes("utf8"),65);
              }
              facelib2.enLibType=2;
              facelib2.uiThreshold=new NativeLong(90);
              FACE_FIND_CONDITION faceFindCondition=new FACE_FIND_CONDITION();
              faceFindCondition.enFeatureStatus=1;
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
              boolean exitgetFace;
              if (Platform.isWindows()) {
                getFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, faceInfoFindS);
                log.debug("临时人员名字："+faceInfo.getUserName());
                log.error("临时人员定时任务查询人脸入口信息返回码：");
                HuaWeiSdkApi.printReturnMsg();
                exitgetFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, faceInfoFindS);
                log.error("临时人员定时任务查询人脸出口信息返回码：");
                HuaWeiSdkApi.printReturnMsg();
              } else {
                getFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, faceInfoFindS);
                log.error("临时人员定时任务查询人脸入口信息返回码：");
                HuaWeiSdkApi.printReturnMsg();
                exitgetFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, faceInfoFindS);
                log.error("临时人员定时任务查询人脸出口信息返回码：");
                HuaWeiSdkApi.printReturnMsg();
              }
              if (getFace || exitgetFace) {
                log.debug("临时人员定时任务查询人脸信息成功");
               // String getfaceInfoPath = new String(new File(".").getCanonicalPath() +"/realface"+"/realface.json");
                String getfaceInfoPath = realfaceinfoPath+File.separator+"realFace.json";
                log.debug("实时查询文件路径："+getfaceInfoPath);
                String facejson = readFile(getfaceInfoPath);
                log.debug("facejson：---"+facejson);
                int j = facejson.indexOf("{");
                int g = facejson.lastIndexOf("}");
                log.debug("开始下标：---"+j);
                log.debug("结束下标：---"+g);
                facejson = facejson.substring(j);
                JSONObject jsonObject = new JSONObject(facejson);
                if (jsonObject==null || jsonObject.isEmpty()){
                  log.debug("临时人员定时任务查询人脸信息为空");
                }
                log.debug("临时人员定时任务查询jsonObject:"+jsonObject);
                JSONArray faceRecordArry = jsonObject.getJSONArray("FaceRecordArry");
                log.debug("临时人员定时任务查询人脸信息faceRecordArry集合：" + faceRecordArry);
                ArrayList<FaceInfo> houtaifaceInfos = new ArrayList<>();
                for (int i = 0; i < faceRecordArry.size(); i++) {
                  FaceInfo qiantaifaceInfo = new FaceInfo();
                  JSONObject faceInfojson = faceRecordArry.getJSONObject(i);
                  qiantaifaceInfo.setFaceid(faceInfojson.getStr("ID"));
                  String userName = faceInfojson.getStr("Name");
                  System.out.println("用户名：" + userName);
                  qiantaifaceInfo.setUserName(userName);
                  String gender = faceInfojson.getStr("Gender");
                  qiantaifaceInfo.setGender(Integer.parseInt(gender));
                  String birthday = faceInfojson.getStr("Birthday");
                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                  Date birthday1 = sdf.parse(birthday);
                  qiantaifaceInfo.setBirthday(birthday1);
                  String province = faceInfojson.getStr("Province");
                  qiantaifaceInfo.setProvince(province);
                  String city = faceInfojson.getStr("City");
                  qiantaifaceInfo.setCity(city);
                  String cardType = faceInfojson.getStr("CardType");
                  qiantaifaceInfo.setCardType(Integer.parseInt(cardType));
                  String cardID = faceInfojson.getStr("CardID");
                  qiantaifaceInfo.setCardId(cardID);
                  houtaifaceInfos.add(qiantaifaceInfo);
                }
                System.out.println("临时人员定时任务查询从摄像头查询到的人脸集合对象：" + houtaifaceInfos);
                String faceID = null;
                if (!CommonUtil.isEmptyList(houtaifaceInfos)){
                  for (FaceInfo houtaiFaceInfo : houtaifaceInfos) {
                    if (faceInfo.getUserName().equals(houtaiFaceInfo.getUserName())) {
                      faceID = houtaiFaceInfo.getFaceid();
                      PU_FACE_INFO_DELETE_S puFaceInfoDeleteS = new PU_FACE_INFO_DELETE_S();
                      int[] uFaceID = new int[100];
                      uFaceID[0] = Integer.parseInt(faceID);
                      puFaceInfoDeleteS.uFaceID = uFaceID;
                      puFaceInfoDeleteS.uFaceNum = 1;
                      puFaceInfoDeleteS.ulChannelId = new NativeLong(101);
                      PU_FACE_LIB_S facelib = new PU_FACE_LIB_S();
                      facelib.ulFaceLibID = new NativeLong(1);
                      if (Platform.isWindows()) {
                        facelib.szLibName = Arrays.copyOf("人脸库".getBytes("gbk"), 65);
                      } else {
                        facelib.szLibName = Arrays.copyOf("人脸库".getBytes("utf8"), 65);
                      }
                      facelib.enLibType = 2;
                      facelib.uiThreshold = new NativeLong(101);
                      puFaceInfoDeleteS.stFacelib = facelib;
                      boolean del;
                      boolean exitdel;
                      if (Platform.isWindows()) {
                        del = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, puFaceInfoDeleteS);
                        log.debug("临时过期人脸入口删除返回码："+faceInfo.getUserName());
                        HuaWeiSdkApi.printReturnMsg();
                        exitdel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, puFaceInfoDeleteS);
                        log.debug("临时过期人脸出口删除返回码："+faceInfo.getUserName());
                        HuaWeiSdkApi.printReturnMsg();
                      } else {
                        del = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, puFaceInfoDeleteS);
                        exitdel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, puFaceInfoDeleteS);
                      }
                      if (del || exitdel) {
                        System.out.println("删除人脸信息成功");
                      } else {
                        System.out.println("删除人脸信息失败");
                      }
                    }
                  }
                }
              }
            }
          }

        } else if (nowDate > banNianDateTime) {//已经超过了截至时间,从数据库删掉当前人脸信息,同时删除人脸门禁授权关系
          int i = faceInfoManagerDao.deleteById(faceInfo.getFaceid());
          int j= faceInfoAccCtrlService.deleteFaceAccCtrlByFaceId(faceInfo.getFaceid());
        }
      }else if(faceInfo.getFaceType() == 0){//内部人员，超过有限日期从摄像头中删除，人脸库中不删除
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String nowtime = df.format(date);
        long nowDate = df.parse(nowtime).getTime();
        Date faceEndTime = faceInfo.getFaceEndTime();
        long faceEndDate = faceEndTime.getTime();
        if (nowDate > faceEndDate) {
          List<AccessControlInfo> accessControlInfos = faceInfoAccCtrlService.seleAccCtrlInfoByFaceID(faceInfo.getFaceid());
          if (!CommonUtil.isEmptyList(accessControlInfos)) {//而且确定他和摄像头挂钩
            for (AccessControlInfo accessControlInfo : accessControlInfos) {
              //找出所挂钩的入口摄像头
              String entryCameraIp = accessControlInfo.getEntryCameraIp();
              String exitCameraIp = accessControlInfo.getExitCameraIp();
              DeviceInfo entrydeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(entryCameraIp);
              DeviceInfo exitdeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(exitCameraIp);
              NativeLong entryulIdentifyId;
              NativeLong exitulIdentifyId;
              if (entrydeviceInfo == null ) {
                entryulIdentifyId = new NativeLong(1);
              }else {
                entryulIdentifyId = entrydeviceInfo.getUlIdentifyId();
              }
              if (exitdeviceInfo == null){
                exitulIdentifyId = new NativeLong(122);
              }else {
                exitulIdentifyId = exitdeviceInfo.getUlIdentifyId();
              }
              //查询第一个人脸库的人脸信息
              String realfaceinfoPath = new StringBuilder()
                      .append(SystemUtil.getUserInfo().getCurrentDir())
                      .append(File.separator)
                      .append(MainAction.RealFaceInfo)
                      .toString();
              File face = new File(realfaceinfoPath);
              if (!face.exists()) {
                face.mkdirs();
              }
              PU_FACE_INFO_FIND_S faceInfoFindS = new PU_FACE_INFO_FIND_S();
              faceInfoFindS.ulChannelId = new NativeLong(101);
              String faceInfoPath = realfaceinfoPath + "/realFaceInfo.json";
              faceInfoFindS.szFindResultPath = Arrays.copyOf(faceInfoPath.getBytes(), 129);
              PU_FACE_LIB_S facelib2 = new PU_FACE_LIB_S();
              facelib2.ulFaceLibID = new NativeLong(1);
              if (Platform.isWindows()) {
                facelib2.szLibName = Arrays.copyOf("人脸库".getBytes("gbk"), 65);
              } else {
                facelib2.szLibName = Arrays.copyOf("人脸库".getBytes("utf8"), 65);
              }
              facelib2.enLibType = 2;
              facelib2.uiThreshold = new NativeLong(90);
              facelib2.isControl=true;
              FACE_FIND_CONDITION faceFindCondition = new FACE_FIND_CONDITION();
              faceFindCondition.enFeatureStatus = 4;
              faceFindCondition.szName = ByteBuffer.allocate(64).put("".getBytes()).array();
              faceFindCondition.szProvince = ByteBuffer.allocate(32).put("".getBytes()).array();
              faceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
              faceFindCondition.szCardID = ByteBuffer.allocate(32).put("".getBytes()).array();
              faceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
              faceFindCondition.enGender = -1;
              faceFindCondition.enCardType = -1;
              faceInfoFindS.stCondition = faceFindCondition;
              faceInfoFindS.stFacelib = facelib2;
              faceInfoFindS.uStartIndex = 0;
              boolean getFace;
              boolean exitgetFace;
              if (Platform.isWindows()) {
                getFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, faceInfoFindS);
                exitgetFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, faceInfoFindS);
              } else {
                getFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, faceInfoFindS);
                log.error("内部人员定时任务查询人脸入口信息返回码：");
                HuaWeiSdkApi.printReturnMsg();
                exitgetFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, faceInfoFindS);
                log.error("内部人员定时任务查询人脸出口信息返回码：");
                HuaWeiSdkApi.printReturnMsg();
              }
              if (getFace || exitgetFace) {
                System.out.println("查询人脸信息成功");
                String getfaceInfoPath = new String(new File(".").getCanonicalPath() + File.separator + "realfaceInfo" + File.separator + "realfaceInfo.json");
                String facejson =readFile(getfaceInfoPath);
                JSONObject objectface = new JSONObject(facejson);
                JSONArray faceRecordArry = objectface.getJSONArray("FaceRecordArry");
                System.out.println("内部人员定时任务查询人脸信息json集合：" + faceRecordArry);
                ArrayList<FaceInfo> houtaifaceInfos = new ArrayList<>();
                for (int i = 0; i < faceRecordArry.size(); i++) {
                  FaceInfo qiantaifaceInfo = new FaceInfo();
                  JSONObject faceInfojson = faceRecordArry.getJSONObject(i);
                  qiantaifaceInfo.setFaceid(faceInfojson.getStr("ID"));
                  String userName = faceInfojson.getStr("Name");
                  System.out.println("用户名：" + userName);
                  qiantaifaceInfo.setUserName(userName);
                  String gender = faceInfojson.getStr("Gender");
                  qiantaifaceInfo.setGender(Integer.parseInt(gender));
                  String birthday = faceInfojson.getStr("Birthday");
                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                  Date birthday1 = sdf.parse(birthday);
                  qiantaifaceInfo.setBirthday(birthday1);
                  String province = faceInfojson.getStr("Province");
                  qiantaifaceInfo.setProvince(province);
                  String city = faceInfojson.getStr("City");
                  qiantaifaceInfo.setCity(city);
                  String cardType = faceInfojson.getStr("CardType");
                  qiantaifaceInfo.setCardType(Integer.parseInt(cardType));
                  String cardID = faceInfojson.getStr("CardID");
                  qiantaifaceInfo.setCardId(cardID);
                  houtaifaceInfos.add(qiantaifaceInfo);
                }
                System.out.println("内部人员定时任务查询从摄像头查询到的人脸集合对象：" + houtaifaceInfos);
                String faceID=null;
                if (!CommonUtil.isEmptyList(houtaifaceInfos)){
                  for (FaceInfo houtaiFaceInfo : houtaifaceInfos) {
                    if (faceInfo.getUserName().equals(houtaiFaceInfo.getUserName())) {
                      faceID = houtaiFaceInfo.getFaceid();
                      PU_FACE_INFO_DELETE_S puFaceInfoDeleteS = new PU_FACE_INFO_DELETE_S();
                      int[] uFaceID = new int[100];
                      uFaceID[0] = Integer.parseInt(faceID);
                      puFaceInfoDeleteS.uFaceID = uFaceID;
                      puFaceInfoDeleteS.uFaceNum = 1;
                      puFaceInfoDeleteS.ulChannelId = new NativeLong(101);
                      PU_FACE_LIB_S facelib = new PU_FACE_LIB_S();
                      facelib.ulFaceLibID = new NativeLong(1);
                      if (Platform.isWindows()) {
                        facelib.szLibName = Arrays.copyOf("人脸库".getBytes("gbk"), 65);
                      } else {
                        facelib.szLibName = Arrays.copyOf("人脸库".getBytes("utf8"), 65);
                      }
                      facelib.enLibType = 2;
                      facelib.uiThreshold = new NativeLong(101);
                      puFaceInfoDeleteS.stFacelib = facelib;
                      boolean del;
                      boolean exitdel;
                      if (Platform.isWindows()) {
                        del = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, puFaceInfoDeleteS);
                        HuaWeiSdkApi.printReturnMsg();
                        exitdel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, puFaceInfoDeleteS);
                        HuaWeiSdkApi.printReturnMsg();
                      } else {
                        del = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, puFaceInfoDeleteS);
                        exitdel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, puFaceInfoDeleteS);
                      }
                      if (del || exitdel) {
                        System.out.println("删除人脸信息成功");
                      } else {
                        System.out.println("删除人脸信息失败");
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }










}
