package com.summit.schedule;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.summit.MainAction;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.repository.AccCtrlRealTimeDao;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.sdk.huawei.*;
import com.summit.sdk.huawei.api.HuaWeiSdkApi;
import com.summit.sdk.huawei.model.DeviceInfo;
import com.summit.service.AccCtrlRealTimeService;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.util.CommonUtil;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2019/9/18.
 */
@Slf4j
@Component
public class FaceInfoManagerSchedule {

    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;
    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;
    @Autowired
    private AccCtrlRealTimeService accCtrlRealTimeService;

    /**
     * 实时查询人脸信息有有效截至时间，一旦超过有效截至时间，先删除摄像头中的人脸信息，然后超过有效时间半年再删除临时人脸信息
     */
    @Scheduled(cron = "0/2 0 0-23 1/1 * ?")
    public void refreshFaceInfoManager() throws ParseException, IOException {
        QueryWrapper<FaceInfo> wrapper=new QueryWrapper<>();
        List<FaceInfo> faceInfoList = faceInfoManagerDao.selectList(wrapper);
        for(FaceInfo faceInfo:faceInfoList){
            if (faceInfo.getFaceType()==1){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                Date faceEndTime = faceInfo.getFaceEndTime();
                Calendar c=Calendar.getInstance();
                c.setTime(faceEndTime);
                c.add(Calendar.MONTH,6);
                String banNianTime = df.format(c.getTime());
                Date banNianDate = df.parse(banNianTime);
                long banNianDateTime = banNianDate.getTime();
                Date date=new Date();
                String nowtime = df.format(date);
                long nowDate = df.parse(nowtime).getTime();
                if (nowDate>banNianDateTime){//已经超过了截至时间,从数据库删掉当前人脸信息
                    int i = faceInfoManagerDao.deleteById(faceInfo.getFaceid());
                }
            }
        }
    }

  public  void refresh() throws ParseException, IOException {
      QueryWrapper<FaceInfo> wrapper=new QueryWrapper<>();
      List<FaceInfo> faceInfoList = faceInfoManagerDao.selectList(wrapper);
      for (FaceInfo faceInfo:faceInfoList){
          if (faceInfo.getFaceType()==1){//确定是临时人员
              List<AccessControlInfo> accessControlInfos = faceInfoAccCtrlService.seleAccCtrlInfoByFaceID(faceInfo.getFaceid());
              if (!CommonUtil.isEmptyList(accessControlInfos)){//而且确定他和摄像头挂钩
                  Date faceStartTime = faceInfo.getFaceStartTime();
                  Date faceEndTime = faceInfo.getFaceEndTime();
                  if (!CommonUtil.isInTime(faceStartTime,faceEndTime)){//再判断这个人有没有超过有时间限制，超过时间限制直接删除在摄像头中
                      for(AccessControlInfo accessControlInfo:accessControlInfos){
                          String entryCameraIp = accessControlInfo.getEntryCameraIp();
                          DeviceInfo entrydeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(entryCameraIp);
                          NativeLong entryulIdentifyId = entrydeviceInfo.getUlIdentifyId();
                          String exitCameraIp = accessControlInfo.getExitCameraIp();
                          DeviceInfo exitdeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(exitCameraIp);
                          NativeLong exitulIdentifyId = exitdeviceInfo.getUlIdentifyId();
                          //查询第一个人脸库的人脸信息
                          String realfaceinfoPath=new StringBuilder()
                                  .append(SystemUtil.getUserInfo().getCurrentDir())
                                  .append(File.separator)
                                  .append(MainAction.RealFaceInfo)
                                  .toString();
                          File face=new File(realfaceinfoPath);
                          if(!face.exists()){
                              face.mkdirs();
                          }
                          PU_FACE_INFO_FIND_S faceInfoFindS = new PU_FACE_INFO_FIND_S();
                          faceInfoFindS.ulChannelId=new NativeLong(101);
                          String faceInfoPath=realfaceinfoPath+"/realFaceInfo.json";
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
                          if(Platform.isWindows()){
                              getFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, faceInfoFindS);
                              exitgetFace =HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, faceInfoFindS);
                          }else {
                              getFace =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, faceInfoFindS);
                              exitgetFace =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, faceInfoFindS);
                          }
                          if (getFace || exitgetFace){
                              System.out.println("查询人脸信息成功");
                              String getfaceInfoPath = new String(new File(".").getCanonicalPath() + File.separator+"realfaceInfo"+File.separator+"realfaceInfo.json");
                              String facejson =CommonUtil.readFile(getfaceInfoPath);
                              JSONObject objectface=new JSONObject(facejson);
                              JSONArray faceRecordArry = objectface.getJSONArray("FaceRecordArry");
                              System.out.println("人脸信息集合："+faceRecordArry);
                              ArrayList<FaceInfo> houtaifaceInfos=new ArrayList<>();
                              for (int i=0; i<faceRecordArry.size();i++){
                                  FaceInfo qiantaifaceInfo=new FaceInfo();
                                  JSONObject faceInfojson=faceRecordArry.getJSONObject(i);
                                  qiantaifaceInfo.setFaceid(faceInfojson.getStr("ID"));
                                  String userName=faceInfojson.getStr("Name");
                                  System.out.println("用户名："+userName);
                                  qiantaifaceInfo.setUserName(userName);
                                  String gender=faceInfojson.getStr("Gender");
                                  qiantaifaceInfo.setGender(Integer.parseInt(gender));
                                  String birthday=faceInfojson.getStr("Birthday");
                                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                  Date birthday1 = sdf.parse(birthday);
                                  qiantaifaceInfo.setBirthday(birthday1);
                                  String province=faceInfojson.getStr("Province");
                                  qiantaifaceInfo.setProvince(province);
                                  String city = faceInfojson.getStr("City");
                                  qiantaifaceInfo.setCity(city);
                                  String cardType = faceInfojson.getStr("CardType");
                                  qiantaifaceInfo.setCardType(Integer.parseInt(cardType));
                                  String cardID = faceInfojson.getStr("CardID");
                                  qiantaifaceInfo.setCardId(cardID);
                                  houtaifaceInfos.add(faceInfo);
                              }
                              System.out.println("从摄像头查询到的人脸集合对象："+houtaifaceInfos);
                              for(FaceInfo houtaiFaceInfo:houtaifaceInfos){
                                  if(faceInfo.getUserName().equals(houtaiFaceInfo.getUserName())){
                                      PU_FACE_INFO_DELETE_S puFaceInfoDeleteS=new PU_FACE_INFO_DELETE_S();
                                      int[] uFaceID = new int[100];
                                      uFaceID[0]=Integer.parseInt(houtaiFaceInfo.getFaceid());
                                      puFaceInfoDeleteS.uFaceID= uFaceID;
                                      puFaceInfoDeleteS.uFaceNum=1;
                                      puFaceInfoDeleteS.ulChannelId=new NativeLong(101);
                                      PU_FACE_LIB_S facelib = new PU_FACE_LIB_S();
                                      facelib.ulFaceLibID=new NativeLong(1);
                                      if(Platform.isWindows()){
                                          facelib.szLibName=Arrays.copyOf("人脸库".getBytes("gbk"),65);
                                      }else {
                                          facelib.szLibName=Arrays.copyOf("人脸库".getBytes("utf8"),65);
                                      }
                                      facelib.enLibType=2;
                                      facelib.uiThreshold=new NativeLong(101);
                                      puFaceInfoDeleteS.stFacelib=facelib;
                                      boolean del;
                                      boolean exitdel;
                                      if(Platform.isWindows()){
                                          del = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, puFaceInfoDeleteS);
                                          exitdel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, puFaceInfoDeleteS);
                                      }else {
                                          del = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, puFaceInfoDeleteS);
                                          exitdel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, puFaceInfoDeleteS);
                                      }
                                      HuaWeiSdkApi.printReturnMsg();
                                      if(del || exitdel){
                                          System.out.println("删除人脸信息成功");
                                      }else {
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