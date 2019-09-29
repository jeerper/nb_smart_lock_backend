package com.summit.schedule;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.summit.MainAction;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.util.ResultBuilder;
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
                            //查询第一个facelib的入口的人脸信息
                            System.out.println("查询入口的人脸信息-------------------------------------------");
                            String entryrealfaceinfoPath = new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.EntryRealFace)
                                    .toString();
                            File entryface = new File(entryrealfaceinfoPath);
                            if (!entryface.exists()) {
                                entryface.mkdirs();
                            }
                            PU_FACE_INFO_FIND_S entryfaceInfoFindS = new PU_FACE_INFO_FIND_S();
                            entryfaceInfoFindS.ulChannelId=new NativeLong(101);
                            String entryfaceInfoPath=entryrealfaceinfoPath+"/entryRealface.json";
                            entryfaceInfoFindS.szFindResultPath= Arrays.copyOf(entryfaceInfoPath.getBytes(),129);
                            PU_FACE_LIB_S entryfacelib2 = new PU_FACE_LIB_S();
                            entryfacelib2.ulFaceLibID=new NativeLong(1);
                            if(Platform.isWindows()){
                                entryfacelib2.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);
                            }else {
                                entryfacelib2.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);
                            }
                            entryfacelib2.enLibType=2;
                            entryfacelib2.uiThreshold=new NativeLong(90);
                            FACE_FIND_CONDITION entryfaceFindCondition=new FACE_FIND_CONDITION();
                            entryfaceFindCondition.enFeatureStatus=1;
                            entryfaceFindCondition.szName= ByteBuffer.allocate(64).put("".getBytes()).array();
                            entryfaceFindCondition.szProvince=ByteBuffer.allocate(32).put("".getBytes()).array();
                            entryfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                            entryfaceFindCondition.szCardID=ByteBuffer.allocate(32).put("".getBytes()).array();
                            entryfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                            entryfaceFindCondition.enGender=-1;
                            entryfaceFindCondition.enCardType=-1;
                            entryfaceInfoFindS.stCondition=entryfaceFindCondition;
                            entryfaceInfoFindS.stFacelib=entryfacelib2;
                            entryfaceInfoFindS.uStartIndex=0;
                            boolean entrygetFace;
                            if (Platform.isWindows()) {
                                entrygetFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, entryfaceInfoFindS);
                                log.debug("临时人员名字："+faceInfo.getUserName());
                                log.error("临时人员定时任务查询人脸入口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();

                            } else {
                                entrygetFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, entryfaceInfoFindS);
                                log.debug("临时人员名字："+faceInfo.getUserName());
                                log.error("临时人员定时任务查询人脸入口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            System.out.println("查询第一个人脸库的出口人脸信息-------------------------------------");
                            String exitrealfaceinfoPath = new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.ExitRealFace)
                                    .toString();
                            File exitface = new File(exitrealfaceinfoPath);
                            if (!exitface.exists()) {
                                exitface.mkdirs();
                            }
                            PU_FACE_INFO_FIND_S exitfaceInfoFindS = new PU_FACE_INFO_FIND_S();
                            exitfaceInfoFindS.ulChannelId=new NativeLong(101);
                            String exitfaceInfoPath=exitrealfaceinfoPath+"/exitRealface.json";
                            entryfaceInfoFindS.szFindResultPath= Arrays.copyOf(exitfaceInfoPath.getBytes(),129);
                            PU_FACE_LIB_S exitfacelib2 = new PU_FACE_LIB_S();
                            exitfacelib2.ulFaceLibID=new NativeLong(1);
                            if(Platform.isWindows()){
                                exitfacelib2.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);
                            }else {
                                exitfacelib2.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);
                            }
                            exitfacelib2.enLibType=2;
                            exitfacelib2.uiThreshold=new NativeLong(90);
                            FACE_FIND_CONDITION exitfaceFindCondition=new FACE_FIND_CONDITION();
                            exitfaceFindCondition.enFeatureStatus=1;
                            exitfaceFindCondition.szName= ByteBuffer.allocate(64).put("".getBytes()).array();
                            exitfaceFindCondition.szProvince=ByteBuffer.allocate(32).put("".getBytes()).array();
                            exitfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                            exitfaceFindCondition.szCardID=ByteBuffer.allocate(32).put("".getBytes()).array();
                            exitfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                            exitfaceFindCondition.enGender=-1;
                            exitfaceFindCondition.enCardType=-1;
                            exitfaceInfoFindS.stCondition=exitfaceFindCondition;
                            exitfaceInfoFindS.stFacelib=exitfacelib2;
                            exitfaceInfoFindS.uStartIndex=0;
                            boolean exitgetFace;
                            if (Platform.isWindows()) {
                                exitgetFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, exitfaceInfoFindS);
                                log.debug("临时人员名字："+faceInfo.getUserName());
                                log.debug("临时人员定时任务查询人脸出口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();
                            } else {
                                exitgetFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, exitfaceInfoFindS);
                                log.debug("临时人员名字："+faceInfo.getUserName());
                                log.debug("临时人员定时任务查询人脸出口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            if (entrygetFace && exitgetFace) {
                                log.debug("临时人员定时任务查询人脸信息成功");
                                /**
                                 *  -------查询到出口人脸信息json数据
                                 */
                                String exitgetfaceInfoPathss = exitrealfaceinfoPath+File.separator+"exitRealface.json";
                                log.debug("实时查询文件路径："+exitgetfaceInfoPathss);
                                String exitfacejson = readFile(exitgetfaceInfoPathss);
                                log.debug("exitfacejson：---"+exitfacejson);
                                int j = exitfacejson.indexOf("{");
                                int g = exitfacejson.lastIndexOf("}");
                                log.debug("开始下标：---"+j);
                                log.debug("结束下标：---"+g);
                                exitfacejson = exitfacejson.substring(j);
                                JSONObject exitjsonObject = new JSONObject(exitfacejson);
                                if (exitjsonObject==null || exitjsonObject.isEmpty()){
                                    log.debug("临时人员定时任务查询人脸信息为空");
                                }
                                log.debug("临时人员定时任务查询jsonObject:"+exitjsonObject);
                                JSONArray exitfaceRecordArry = exitjsonObject.getJSONArray("FaceRecordArry");
                                log.debug("临时人员定时任务查询人脸信息faceRecordArry出口摄像头集合：" + exitfaceRecordArry);
                                ArrayList<FaceInfo> exithoutaifaceInfos = new ArrayList<>();
                                for (int i = 0; i < exitfaceRecordArry.size(); i++) {
                                    FaceInfo exithoutaiifaceInfo = new FaceInfo();
                                    JSONObject exitfaceInfojson = exitfaceRecordArry.getJSONObject(i);
                                    exithoutaiifaceInfo.setFaceid(exitfaceInfojson.getStr("ID"));
                                    String userName = exitfaceInfojson.getStr("Name");
                                    System.out.println("用户名：" + userName);
                                    exithoutaiifaceInfo.setUserName(userName);
                                    String gender = exitfaceInfojson.getStr("Gender");
                                    exithoutaiifaceInfo.setGender(Integer.parseInt(gender));
                                    String exitbirthday = exitfaceInfojson.getStr("Birthday");
                                    SimpleDateFormat exitsdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date exitbirthday1 = exitsdf.parse(exitbirthday);
                                    exithoutaiifaceInfo.setBirthday(exitbirthday1);
                                    String province = exitfaceInfojson.getStr("Province");
                                    exithoutaiifaceInfo.setProvince(province);
                                    String city = exitfaceInfojson.getStr("City");
                                    exithoutaiifaceInfo.setCity(city);
                                    String cardType = exitfaceInfojson.getStr("CardType");
                                    exithoutaiifaceInfo.setCardType(Integer.parseInt(cardType));
                                    String cardID = exitfaceInfojson.getStr("CardID");
                                    exithoutaiifaceInfo.setCardId(cardID);
                                    exithoutaifaceInfos.add(exithoutaiifaceInfo);
                                }
                                System.out.println("临时人员定时任务查询从摄像头查询到的出口人脸集合对象：" + exithoutaifaceInfos);
                                String exitfaceID = null;
                                if (!CommonUtil.isEmptyList(exithoutaifaceInfos)){
                                    for (FaceInfo houtaiFaceInfo : exithoutaifaceInfos) {
                                        if (faceInfo.getUserName().equals(houtaiFaceInfo.getUserName())) {
                                            exitfaceID = houtaiFaceInfo.getFaceid();
                                            PU_FACE_INFO_DELETE_S exitpuFaceInfoDeleteS = new PU_FACE_INFO_DELETE_S();
                                            int[] exituFaceID = new int[100];
                                            exituFaceID[0] = Integer.parseInt(exitfaceID);
                                            exitpuFaceInfoDeleteS.uFaceID = exituFaceID;
                                            exitpuFaceInfoDeleteS.uFaceNum = 1;
                                            exitpuFaceInfoDeleteS.ulChannelId = new NativeLong(101);
                                            PU_FACE_LIB_S exitfacelib = new PU_FACE_LIB_S();
                                            exitfacelib.ulFaceLibID = new NativeLong(1);
                                            if (Platform.isWindows()) {
                                                exitfacelib.szLibName = Arrays.copyOf("facelib".getBytes("gbk"), 65);
                                            } else {
                                                exitfacelib.szLibName = Arrays.copyOf("facelib".getBytes("utf8"), 65);
                                            }
                                            exitfacelib.enLibType = 2;
                                            exitfacelib.uiThreshold = new NativeLong(101);
                                            exitpuFaceInfoDeleteS.stFacelib = exitfacelib;
                                            boolean exitdel;
                                            if (Platform.isWindows()) {
                                                exitdel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, exitpuFaceInfoDeleteS);
                                                log.debug("临时过期人脸出口删除返回码：");
                                                HuaWeiSdkApi.printReturnMsg();
                                            } else {
                                                exitdel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, exitpuFaceInfoDeleteS);
                                                log.debug("临时过期人脸出口删除返回码：");
                                                HuaWeiSdkApi.printReturnMsg();
                                            }
                                            if (exitdel) {
                                               log.debug("临时过期人脸出口删除人脸信息成功");
                                            } else {
                                                log.error("临时过期人脸出口删除人脸信息成功");
                                            }
                                        }
                                    }
                                }
                                /**
                                 *  查询到入口人脸信息json数据
                                 */
                                String entrygetfaceInfoPathss = entryrealfaceinfoPath+File.separator+"entryRealface.json";
                                log.debug("实时查询文件路径："+entrygetfaceInfoPathss);
                                String entryfacejson = readFile(entrygetfaceInfoPathss);
                                log.debug("exitfacejson：---"+entryfacejson);
                                int entryj = entryfacejson.indexOf("{");
                                int entryg = entryfacejson.lastIndexOf("}");
                                log.debug("入口开始下标：---"+entryj);
                                log.debug("出口结束下标：---"+entryg);
                                entryfacejson = entryfacejson.substring(j);
                                JSONObject entryjsonObject = new JSONObject(entryfacejson);
                                log.debug("临时人员定时任务查询jsonObject:"+entryjsonObject);
                                JSONArray entryfaceRecordArry = entryjsonObject.getJSONArray("FaceRecordArry");
                                log.debug("临时人员定时任务查询人脸信息faceRecordArry出口摄像头集合：" + entryfaceRecordArry);
                                ArrayList<FaceInfo> entryhoutaifaceInfos = new ArrayList<>();
                                for (int i = 0; i < entryfaceRecordArry.size(); i++) {
                                    FaceInfo entryhoutaiifaceInfo = new FaceInfo();
                                    JSONObject entryfaceInfojson = entryfaceRecordArry.getJSONObject(i);
                                    entryhoutaiifaceInfo.setFaceid(entryfaceInfojson.getStr("ID"));
                                    String userName = entryfaceInfojson.getStr("Name");
                                    entryhoutaiifaceInfo.setUserName(userName);
                                    String gender = entryfaceInfojson.getStr("Gender");
                                    entryhoutaiifaceInfo.setGender(Integer.parseInt(gender));
                                    String entrybirthday = entryfaceInfojson.getStr("Birthday");
                                    SimpleDateFormat exitsdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date entrybirthday1 = exitsdf.parse(entrybirthday);
                                    entryhoutaiifaceInfo.setBirthday(entrybirthday1);
                                    String province = entryfaceInfojson.getStr("Province");
                                    entryhoutaiifaceInfo.setProvince(province);
                                    String city = entryfaceInfojson.getStr("City");
                                    entryhoutaiifaceInfo.setCity(city);
                                    String cardType = entryfaceInfojson.getStr("CardType");
                                    entryhoutaiifaceInfo.setCardType(Integer.parseInt(cardType));
                                    String cardID = entryfaceInfojson.getStr("CardID");
                                    entryhoutaiifaceInfo.setCardId(cardID);
                                    entryhoutaifaceInfos.add(entryhoutaiifaceInfo);
                                }
                                log.debug("临时人员定时任务查询从摄像头查询到的出口人脸集合对象：" + entryhoutaifaceInfos);
                                String entryfaceID = null;
                                if (!CommonUtil.isEmptyList(entryhoutaifaceInfos)){
                                    for (FaceInfo houtaiFaceInfo : entryhoutaifaceInfos) {
                                        if (faceInfo.getUserName().equals(houtaiFaceInfo.getUserName())) {
                                            entryfaceID = houtaiFaceInfo.getFaceid();
                                            PU_FACE_INFO_DELETE_S entrypuFaceInfoDeleteS = new PU_FACE_INFO_DELETE_S();
                                            int[] entryuFaceID = new int[100];
                                            entryuFaceID[0] = Integer.parseInt(entryfaceID);
                                            entrypuFaceInfoDeleteS.uFaceID = entryuFaceID;
                                            entrypuFaceInfoDeleteS.uFaceNum = 1;
                                            entrypuFaceInfoDeleteS.ulChannelId = new NativeLong(101);
                                            PU_FACE_LIB_S entryfacelib = new PU_FACE_LIB_S();
                                            entryfacelib.ulFaceLibID = new NativeLong(1);
                                            if (Platform.isWindows()) {
                                                entryfacelib.szLibName = Arrays.copyOf("facelib".getBytes("gbk"), 65);
                                            } else {
                                                entryfacelib.szLibName = Arrays.copyOf("facelib".getBytes("utf8"), 65);
                                            }
                                            entryfacelib.enLibType = 2;
                                            entryfacelib.uiThreshold = new NativeLong(101);
                                            entrypuFaceInfoDeleteS.stFacelib = entryfacelib;
                                            boolean entrydel;
                                            if (Platform.isWindows()) {
                                                entrydel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, entrypuFaceInfoDeleteS);
                                                log.debug("临时过期人脸入口删除返回码：");
                                                HuaWeiSdkApi.printReturnMsg();
                                            } else {
                                                entrydel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, entrypuFaceInfoDeleteS);
                                                log.debug("临时过期人脸入口删除返回码：");
                                                HuaWeiSdkApi.printReturnMsg();
                                            }
                                            if (entrydel) {
                                                log.debug("临时过期人脸出口删除人脸信息成功");
                                            } else {
                                                log.error("临时过期人脸出口删除人脸信息失败");
                                            }
                                        }
                                    }
                                }
                            }else {
                                log.error("查询临时人脸库人脸信息失败");
                            }
                        }
                    }

                } else if (nowDate > banNianDateTime) {//已经超过了截至时间,从数据库删掉当前人脸信息,同时删除人脸门禁授权关系
                    int i = faceInfoManagerDao.deleteById(faceInfo.getFaceid());
                    int j= faceInfoAccCtrlService.deleteFaceAccCtrlByFaceId(faceInfo.getFaceid());
                }
            }else if(faceInfo.getFaceType() == 0){//内部人员，超过有限日期从摄像头中删除，facelib中不删除
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
                            /**
                             * 查询第一个facelib的入口---人脸信息
                             */
                            String neiBuEntryRealFaceInfoPath = new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.NeiBuEntryRealFaceInfo)
                                    .toString();
                            File neiBuEntryRealFaceInfo = new File(neiBuEntryRealFaceInfoPath);
                            if (!neiBuEntryRealFaceInfo.exists()) {
                                neiBuEntryRealFaceInfo.mkdirs();
                            }
                            PU_FACE_INFO_FIND_S entryfaceInfoFindS = new PU_FACE_INFO_FIND_S();
                            entryfaceInfoFindS.ulChannelId = new NativeLong(101);
                            String  entryfaceInfoPath = neiBuEntryRealFaceInfoPath + "/neiBuEntryRealFaceInfo.json";
                            entryfaceInfoFindS.szFindResultPath = Arrays.copyOf(entryfaceInfoPath.getBytes(), 129);
                            PU_FACE_LIB_S entryfacelib2 = new PU_FACE_LIB_S();
                            entryfacelib2.ulFaceLibID = new NativeLong(1);
                            if (Platform.isWindows()) {
                                entryfacelib2.szLibName = Arrays.copyOf("facelib".getBytes("gbk"), 65);
                            } else {
                                entryfacelib2.szLibName = Arrays.copyOf("facelib".getBytes("utf8"), 65);
                            }
                            entryfacelib2.enLibType = 2;
                            entryfacelib2.uiThreshold = new NativeLong(90);
                            entryfacelib2.isControl=true;
                            FACE_FIND_CONDITION entryfaceFindCondition = new FACE_FIND_CONDITION();
                            entryfaceFindCondition.enFeatureStatus = 1;
                            entryfaceFindCondition.szName = ByteBuffer.allocate(64).put("".getBytes()).array();
                            entryfaceFindCondition.szProvince = ByteBuffer.allocate(32).put("".getBytes()).array();
                            entryfaceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
                            entryfaceFindCondition.szCardID = ByteBuffer.allocate(32).put("".getBytes()).array();
                            entryfaceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
                            entryfaceFindCondition.enGender = -1;
                            entryfaceFindCondition.enCardType = -1;
                            entryfaceInfoFindS.stCondition = entryfaceFindCondition;
                            entryfaceInfoFindS.stFacelib = entryfacelib2;
                            entryfaceInfoFindS.uStartIndex = 0;
                            boolean entrygetFace;
                            if (Platform.isWindows()) {
                                entrygetFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, entryfaceInfoFindS);
                            } else {
                                entrygetFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, entryfaceInfoFindS);
                                log.error("内部人员定时任务查询人脸入口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            /**
                             * 查询第一个facelib的出口---人脸信息
                             */
                            String neiBuExitRealFaceInfoPath = new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.NeiBuExitRealFaceInfo)
                                    .toString();
                            File neiBuExitRealFaceInfo = new File(neiBuExitRealFaceInfoPath);
                            if (!neiBuExitRealFaceInfo.exists()) {
                                neiBuExitRealFaceInfo.mkdirs();
                            }
                            PU_FACE_INFO_FIND_S exitfaceInfoFindS = new PU_FACE_INFO_FIND_S();
                            exitfaceInfoFindS.ulChannelId = new NativeLong(101);
                            String  exitfaceInfoPath = neiBuExitRealFaceInfoPath + "/neiBuExitRealFaceInfo.json";
                            exitfaceInfoFindS.szFindResultPath = Arrays.copyOf(exitfaceInfoPath.getBytes(), 129);
                            PU_FACE_LIB_S exitfacelib2 = new PU_FACE_LIB_S();
                            exitfacelib2.ulFaceLibID = new NativeLong(1);
                            if (Platform.isWindows()) {
                                exitfacelib2.szLibName = Arrays.copyOf("facelib".getBytes("gbk"), 65);
                            } else {
                                exitfacelib2.szLibName = Arrays.copyOf("facelib".getBytes("utf8"), 65);
                            }
                            exitfacelib2.enLibType = 2;
                            exitfacelib2.uiThreshold = new NativeLong(90);
                            exitfacelib2.isControl=true;
                            FACE_FIND_CONDITION exitfaceFindCondition = new FACE_FIND_CONDITION();
                            exitfaceFindCondition.enFeatureStatus = 1;
                            exitfaceFindCondition.szName = ByteBuffer.allocate(64).put("".getBytes()).array();
                            exitfaceFindCondition.szProvince = ByteBuffer.allocate(32).put("".getBytes()).array();
                            exitfaceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
                            exitfaceFindCondition.szCardID = ByteBuffer.allocate(32).put("".getBytes()).array();
                            exitfaceFindCondition.szCity = ByteBuffer.allocate(48).put("".getBytes()).array();
                            exitfaceFindCondition.enGender = -1;
                            exitfaceFindCondition.enCardType = -1;
                            exitfaceInfoFindS.stCondition = exitfaceFindCondition;
                            exitfaceInfoFindS.stFacelib = exitfacelib2;
                            exitfaceInfoFindS.uStartIndex = 0;
                            boolean exitgetFace;
                            if (Platform.isWindows()) {
                                exitgetFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, exitfaceInfoFindS);
                            } else {
                                exitgetFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, exitfaceInfoFindS);
                                log.error("内部人员定时任务查询人脸出口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            if (exitgetFace && entrygetFace){
                                log.debug("内部人员定时任务查询人脸信息成功");
                                /**
                                 *  -------查询到内部---出口人脸信息json数据
                                 */
                                String neibuExitgetfaceInfoPaths = neiBuExitRealFaceInfoPath+File.separator+"neiBuExitRealFaceInfo.json";
                                log.debug("实时查询文件路径："+neibuExitgetfaceInfoPaths);
                                String neibuexitfacejson = readFile(neibuExitgetfaceInfoPaths);
                                log.debug("neibuexitfacejson：---"+neibuexitfacejson);
                                JSONObject neibuExitjsonObject = new JSONObject(neibuexitfacejson);
                                if (neibuExitjsonObject==null || neibuExitjsonObject.isEmpty()){
                                    log.debug("临时人员定时任务查询人脸信息为空");
                                }
                                log.debug("临时人员定时任务查询jsonObject:"+neibuExitjsonObject);
                                JSONArray neibuExitfaceRecordArry = neibuExitjsonObject.getJSONArray("FaceRecordArry");
                                log.debug("临时人员定时任务查询人脸信息faceRecordArry出口摄像头集合：" + neibuExitfaceRecordArry);
                                ArrayList<FaceInfo> neibuExithoutaifaceInfos = new ArrayList<>();
                                for (int i = 0; i < neibuExitfaceRecordArry.size(); i++) {
                                    FaceInfo neibuExithoutaiifaceInfo = new FaceInfo();
                                    JSONObject neibuExitfaceInfojson = neibuExitfaceRecordArry.getJSONObject(i);
                                    neibuExithoutaiifaceInfo.setFaceid(neibuExitfaceInfojson.getStr("ID"));
                                    String userName = neibuExitfaceInfojson.getStr("Name");
                                    System.out.println("用户名：" + userName);
                                    neibuExithoutaiifaceInfo.setUserName(userName);
                                    String gender = neibuExitfaceInfojson.getStr("Gender");
                                    neibuExithoutaiifaceInfo.setGender(Integer.parseInt(gender));
                                    String exitbirthday = neibuExitfaceInfojson.getStr("Birthday");
                                    SimpleDateFormat exitsdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date exitbirthday1 = exitsdf.parse(exitbirthday);
                                    neibuExithoutaiifaceInfo.setBirthday(exitbirthday1);
                                    String province = neibuExitfaceInfojson.getStr("Province");
                                    neibuExithoutaiifaceInfo.setProvince(province);
                                    String city = neibuExitfaceInfojson.getStr("City");
                                    neibuExithoutaiifaceInfo.setCity(city);
                                    String cardType = neibuExitfaceInfojson.getStr("CardType");
                                    neibuExithoutaiifaceInfo.setCardType(Integer.parseInt(cardType));
                                    String cardID = neibuExitfaceInfojson.getStr("CardID");
                                    neibuExithoutaiifaceInfo.setCardId(cardID);
                                    neibuExithoutaifaceInfos.add(neibuExithoutaiifaceInfo);
                                }
                                System.out.println("内部人员定时任务查询从摄像头查询到的出口人脸集合对象：" + neibuExithoutaifaceInfos);
                                String neibuExitfaceID = null;
                                if (!CommonUtil.isEmptyList(neibuExithoutaifaceInfos)){
                                    for (FaceInfo houtaiFaceInfo : neibuExithoutaifaceInfos) {
                                        if (faceInfo.getUserName().equals(houtaiFaceInfo.getUserName())) {
                                            neibuExitfaceID = houtaiFaceInfo.getFaceid();
                                            PU_FACE_INFO_DELETE_S neibuExitpuFaceInfoDeleteS = new PU_FACE_INFO_DELETE_S();
                                            int[] neibuExituFaceID = new int[100];
                                            neibuExituFaceID[0] = Integer.parseInt(neibuExitfaceID);
                                            neibuExitpuFaceInfoDeleteS.uFaceID = neibuExituFaceID;
                                            neibuExitpuFaceInfoDeleteS.uFaceNum = 1;
                                            neibuExitpuFaceInfoDeleteS.ulChannelId = new NativeLong(101);
                                            PU_FACE_LIB_S neibuExitfacelib = new PU_FACE_LIB_S();
                                            neibuExitfacelib.ulFaceLibID = new NativeLong(1);
                                            if (Platform.isWindows()) {
                                                neibuExitfacelib.szLibName = Arrays.copyOf("facelib".getBytes("gbk"), 65);
                                            } else {
                                                neibuExitfacelib.szLibName = Arrays.copyOf("facelib".getBytes("utf8"), 65);
                                            }
                                            neibuExitfacelib.enLibType = 2;
                                            neibuExitfacelib.uiThreshold = new NativeLong(101);
                                            neibuExitpuFaceInfoDeleteS.stFacelib = neibuExitfacelib;
                                            boolean neibuExitdel;
                                            if (Platform.isWindows()) {
                                                neibuExitdel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, neibuExitpuFaceInfoDeleteS);
                                                log.debug("内部过期人脸出口删除返回码：");
                                                HuaWeiSdkApi.printReturnMsg();
                                            } else {
                                                neibuExitdel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitulIdentifyId, neibuExitpuFaceInfoDeleteS);
                                                log.debug("内部过期人脸出口删除返回码：");
                                                HuaWeiSdkApi.printReturnMsg();
                                            }
                                            if (neibuExitdel) {
                                                log.debug("内部过期人脸出口删除人脸信息成功");
                                            } else {
                                                log.error("内部过期人脸出口删除人脸信息失败");
                                            }
                                        }
                                    }
                                }
                                /**
                                 *  -------查询到内部---入口人脸信息json数据
                                 */
                                String neibuEntrygetfaceInfoPaths = neiBuEntryRealFaceInfoPath+File.separator+"neiBuEntryRealFaceInfo.json";
                                log.debug("实时查询文件路径："+neibuEntrygetfaceInfoPaths);
                                String neibuEntryfacejson = readFile(neibuEntrygetfaceInfoPaths);
                                log.debug("neibuEntryfacejson：---"+neibuEntryfacejson);
                                JSONObject neibuEntryjsonObject = new JSONObject(neibuEntryfacejson);
                                log.debug("临时人员定时任务查询neibuEntryjsonObject:"+neibuEntryjsonObject);
                                JSONArray neibuEntryfaceRecordArry = neibuEntryjsonObject.getJSONArray("FaceRecordArry");
                                log.debug("临时人员定时任务查询人脸信息neibuEntryfaceRecordArry出口摄像头集合：" + neibuEntryfaceRecordArry);
                                ArrayList<FaceInfo> neibuEntryhoutaifaceInfos = new ArrayList<>();
                                for (int i = 0; i < neibuEntryfaceRecordArry.size(); i++) {
                                    FaceInfo neibuEntryhoutaifaceInfo = new FaceInfo();
                                    JSONObject neibuEntryfaceInfojson = neibuEntryfaceRecordArry.getJSONObject(i);
                                    neibuEntryhoutaifaceInfo.setFaceid(neibuEntryfaceInfojson.getStr("ID"));
                                    String userName = neibuEntryfaceInfojson.getStr("Name");
                                    neibuEntryhoutaifaceInfo.setUserName(userName);
                                    String gender = neibuEntryfaceInfojson.getStr("Gender");
                                    neibuEntryhoutaifaceInfo.setGender(Integer.parseInt(gender));
                                    String entrybirthday = neibuEntryfaceInfojson.getStr("Birthday");
                                    SimpleDateFormat entrysdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date entrybirthday1 = entrysdf.parse(entrybirthday);
                                    neibuEntryhoutaifaceInfo.setBirthday(entrybirthday1);
                                    String province = neibuEntryfaceInfojson.getStr("Province");
                                    neibuEntryhoutaifaceInfo.setProvince(province);
                                    String city = neibuEntryfaceInfojson.getStr("City");
                                    neibuEntryhoutaifaceInfo.setCity(city);
                                    String cardType = neibuEntryfaceInfojson.getStr("CardType");
                                    neibuEntryhoutaifaceInfo.setCardType(Integer.parseInt(cardType));
                                    String cardID = neibuEntryfaceInfojson.getStr("CardID");
                                    neibuEntryhoutaifaceInfo.setCardId(cardID);
                                    neibuEntryhoutaifaceInfos.add(neibuEntryhoutaifaceInfo);
                                }
                                System.out.println("内部人员定时任务查询从摄像头查询到的入口人脸集合对象：" + neibuEntryhoutaifaceInfos);
                                String neibuEntryfaceID = null;
                                if (!CommonUtil.isEmptyList(neibuEntryhoutaifaceInfos)){
                                    for (FaceInfo entryhoutaiFaceInfo : neibuEntryhoutaifaceInfos) {
                                        if (faceInfo.getUserName().equals(entryhoutaiFaceInfo.getUserName())) {
                                            neibuEntryfaceID = entryhoutaiFaceInfo.getFaceid();
                                            PU_FACE_INFO_DELETE_S neibuEntrypuFaceInfoDeleteS = new PU_FACE_INFO_DELETE_S();
                                            int[] neibuEntryuFaceID = new int[100];
                                            neibuEntryuFaceID[0] = Integer.parseInt(neibuEntryfaceID);
                                            neibuEntrypuFaceInfoDeleteS.uFaceID = neibuEntryuFaceID;
                                            neibuEntrypuFaceInfoDeleteS.uFaceNum = 1;
                                            neibuEntrypuFaceInfoDeleteS.ulChannelId = new NativeLong(101);
                                            PU_FACE_LIB_S neibuEntryfacelib = new PU_FACE_LIB_S();
                                            neibuEntryfacelib.ulFaceLibID = new NativeLong(1);
                                            if (Platform.isWindows()) {
                                                neibuEntryfacelib.szLibName = Arrays.copyOf("facelib".getBytes("gbk"), 65);
                                            } else {
                                                neibuEntryfacelib.szLibName = Arrays.copyOf("facelib".getBytes("utf8"), 65);
                                            }
                                            neibuEntryfacelib.enLibType = 2;
                                            neibuEntryfacelib.uiThreshold = new NativeLong(101);
                                            neibuEntrypuFaceInfoDeleteS.stFacelib = neibuEntryfacelib;
                                            boolean neibuEntrydel;
                                            if (Platform.isWindows()) {
                                                neibuEntrydel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, neibuEntrypuFaceInfoDeleteS);
                                                log.debug("内部过期人脸入口删除返回码：");
                                                HuaWeiSdkApi.printReturnMsg();
                                            } else {
                                                neibuEntrydel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryulIdentifyId, neibuEntrypuFaceInfoDeleteS);
                                                log.debug("内部过期人脸入口删除返回码：");
                                                HuaWeiSdkApi.printReturnMsg();
                                            }
                                            if (neibuEntrydel) {
                                                log.debug("内部过期人脸入口删除人脸信息成功");
                                            } else {
                                                log.error("内部过期人脸入口删除人脸信息失败");
                                            }
                                        }
                                    }
                                }
                            }else {
                                log.error("查询内部人脸信息失败");
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