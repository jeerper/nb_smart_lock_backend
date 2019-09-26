package com.summit.schedule;

import cn.hutool.core.io.FileUtil;
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
    private  FaceInfoManagerDao faceInfoManagerDao;
    @Autowired
    private  FaceInfoAccCtrlService faceInfoAccCtrlService;
    @Autowired
    private AccCtrlRealTimeService accCtrlRealTimeService;

    /**
     * 1、实时查询人脸信息有有效截至时间，一旦超过有效截至时间，内部人员和临时人员都从摄像头中删除，此时开不了锁
     * 2、然后对于超过有效时间半年的临时人员从数据库中删除，同时删除授权关系，是内部人员的话在数据库中不删除，授权关系也不删除
     */
    @Scheduled(cron = "0/35 * * * * ?")
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
                if (nowDate > faceEndDate && nowDate<=banNianDateTime) {//超过截至日期，从摄像头删除
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
                                entryulIdentifyId = new NativeLong(111);
                            }else {
                                entryulIdentifyId = entrydeviceInfo.getUlIdentifyId();
                            }
                            if (exitdeviceInfo == null){
                                exitulIdentifyId = new NativeLong(112);
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
                            String faceInfoPath=realfaceinfoPath+"/realFace.json";
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
                                //String getfaceInfoPathss = new String(new File(".").getCanonicalPath() + File.separator + "realface" + File.separator + "realface.json");
                                String getfaceInfoPathss = realfaceinfoPath+File.separator+"realFace.json";
                                log.debug("实时查询文件路径："+getfaceInfoPathss);
                                String facejson = readFile(getfaceInfoPathss);
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
                                                log.debug("临时过期人脸入口删除返回码：");
                                                HuaWeiSdkApi.printReturnMsg();
                                                exitdel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, puFaceInfoDeleteS);
                                                log.debug("临时过期人脸出口删除返回码：");
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
                                entryulIdentifyId = new NativeLong(222);
                            }else {
                                entryulIdentifyId = entrydeviceInfo.getUlIdentifyId();

                            }
                            if (exitdeviceInfo == null){
                                exitulIdentifyId = new NativeLong(223);
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
                                facelib2.szLibName = Arrays.copyOf("facelib".getBytes("gbk"), 65);
                            } else {
                                facelib2.szLibName = Arrays.copyOf("facelib".getBytes("utf8"), 65);
                            }
                            facelib2.enLibType = 2;
                            facelib2.uiThreshold = new NativeLong(90);
                            facelib2.isControl=true;
                            FACE_FIND_CONDITION faceFindCondition = new FACE_FIND_CONDITION();
                            faceFindCondition.enFeatureStatus = 1;
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
                               // String getfaceInfoPath = new String(new File(".").getCanonicalPath() + File.separator + "realfaceInfo" + File.separator + "realfaceInfo.json");
                                String getfaceInfoPath = realfaceinfoPath+File.separator+"realFaceInfo.json";
                                String facejson =readFile(getfaceInfoPath);
                                JSONObject objectface = new JSONObject(facejson);
                                JSONArray faceRecordArry = objectface.getJSONArray("FaceRecordArry");
                                log.debug("内部人员定时任务查询人脸信息json集合：" + faceRecordArry);
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
    public  String readFile(String path){
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
}