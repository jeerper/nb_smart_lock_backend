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
import com.summit.service.FaceInfoManagerService;
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
    @Autowired
    private FaceInfoManagerService faceInfoManagerService;
    /**
     * 1、实时查询人脸信息有有效截至时间，一旦超过有效截至时间，内部人员和临时人员都从摄像头中删除，此时开不了锁
     * 2、然后对于超过有效时间半年的临时人员从数据库中删除，同时删除授权关系，是内部人员的话在数据库中不删除，授权关系也不删除
     */
    @Scheduled(cron = "0/35 * * * * ?")
    public  void refreshFaceInfoManager() throws ParseException, IOException {
        QueryWrapper<FaceInfo> wrapper = new QueryWrapper<>();
        List<FaceInfo> faceInfoList = faceInfoManagerDao.selectList(wrapper);
        for (FaceInfo faceInfo : faceInfoList) {
            Date isValidDate = new Date();
            SimpleDateFormat isValidSdf = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            String isValidNowtime = isValidSdf.format(isValidDate);
            long isValidNowDate = isValidSdf.parse(isValidNowtime).getTime();
            Date isValidEndTime = faceInfo.getFaceEndTime();
            long isValidEndDate = isValidEndTime.getTime();
            if (isValidNowDate>isValidEndDate){//说明这个人脸已经过期，需要把人脸过期从0变为1
                faceInfo.setIsValidTime(1);
                faceInfoManagerService.updateFaceInfo(faceInfo);
            }
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
                   // System.out.println("临时人脸关联的门禁："+accessControlInfos);
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
                            /**
                             * 先查询入口临时人脸库
                             */
                            PU_FACE_LIB_GET_S  temporaryEntryFaceLib=new PU_FACE_LIB_GET_S();
                            temporaryEntryFaceLib.ulChannelId=new NativeLong(101);
                            temporaryEntryFaceLib.ulFaceLibNum=new NativeLong(1);
                            String temporaryEntryFaceLibPath=new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.TemporaryEntryFaceLib)
                                    .toString();
                            File entryTemFacelib=new File(temporaryEntryFaceLibPath);
                            if(!entryTemFacelib.exists()){
                                entryTemFacelib.mkdirs();
                            }
                            String entryTemfaceLib=temporaryEntryFaceLibPath+File.separator+"temporaryEntryFaceLib.json";
                            temporaryEntryFaceLib.szFindResultPath= Arrays.copyOf(entryTemfaceLib.getBytes(),128);

                            /**
                             * 再查询出口临时人脸库
                             */
                            PU_FACE_LIB_GET_S  temporaryExitFaceLib=new PU_FACE_LIB_GET_S();
                            temporaryExitFaceLib.ulChannelId=new NativeLong(101);
                            temporaryExitFaceLib.ulFaceLibNum=new NativeLong(1);
                            String temporaryExitFaceLibPath=new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.TemporaryExitFaceLib)
                                    .toString();
                            File exTemFacelib=new File(temporaryExitFaceLibPath);
                            if(!exTemFacelib.exists()){
                                exTemFacelib.mkdirs();
                            }
                            String exitTemfaceLib=temporaryExitFaceLibPath+File.separator+"temporaryExitFaceLib.json";
                            temporaryExitFaceLib.szFindResultPath= Arrays.copyOf(exitTemfaceLib.getBytes(),128);
                            boolean realEntrygetFaceLib;
                            boolean realExitgetFaceLib;
                            if(Platform.isWindows()){
                                realEntrygetFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(entryulIdentifyId, temporaryEntryFaceLib);
                                realExitgetFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(exitulIdentifyId, temporaryExitFaceLib);
                            }else {
                                realEntrygetFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(entryulIdentifyId, temporaryEntryFaceLib);
                                realExitgetFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(exitulIdentifyId, temporaryExitFaceLib);
                            }
                            //出口
                            Integer exitRealLibType=null;//人脸库类型
                            Integer exitRealulFaceLibID=null;//人脸库id
                            String exitRealszLibName=null;//人脸库名称
                            Integer exitRealuiThreshold=null;//布控的阀值
                            //入口
                            Integer entryRealenLibType=null;//人脸库类型
                            Integer entryRealulFaceLibID=null;//人脸库id
                            String entryRealszLibName=null;//人脸库名称
                            Integer entryRealuiThreshold=null;//布控的阀值
                            if (realEntrygetFaceLib || realExitgetFaceLib){
                                if (entrydeviceInfo==null){
                                    entryRealszLibName="facelib";
                                    entryRealenLibType=2;
                                    entryRealuiThreshold=90;
                                    entryRealulFaceLibID=1111;
                                }else if (realEntrygetFaceLib){
                                    System.out.println("实时查询入口临时人脸库成功------------------------");
                                    String temporaryEntryFaceLibPath2 = new String(new File(".").getCanonicalPath() + File.separator +"temporaryEntryFaceLib"+File.separator+"temporaryEntryFaceLib.json");
                                    String temporaryEntryFaceLibPathjson = readFile(temporaryEntryFaceLibPath2);
                                    JSONObject temporaryEntryFaceLibobject=new JSONObject(temporaryEntryFaceLibPathjson);
                                    JSONArray temporaryEntryFaceLibobjectListArry = temporaryEntryFaceLibobject.getJSONArray("FaceListsArry");
                                    System.out.println("人脸库集合："+temporaryEntryFaceLibobjectListArry);
                                    JSONObject temporaryEntryFaceLibInfo=temporaryEntryFaceLibobjectListArry.getJSONObject(0);
                                    entryRealszLibName=temporaryEntryFaceLibInfo.getStr("FaceListName");
                                    entryRealenLibType = Integer.parseInt(temporaryEntryFaceLibInfo.getStr("FaceListType"));
                                    entryRealulFaceLibID = Integer.parseInt(temporaryEntryFaceLibInfo.getStr("ID"));
                                    entryRealuiThreshold=Integer.parseInt(temporaryEntryFaceLibInfo.getStr("Threshold"));
                                }
                                if (exitdeviceInfo==null){
                                    exitRealszLibName="facelib";
                                    exitRealLibType=2;
                                    exitRealuiThreshold=90;
                                    exitRealulFaceLibID=1111;
                                }else if (realExitgetFaceLib){
                                    System.out.println("实时查询出口临时人脸库成功------------------------");
                                    String temporaryExitFaceLibPath2 = new String(new File(".").getCanonicalPath() + File.separator +"temporaryExitFaceLib"+File.separator+"temporaryExitFaceLib.json");
                                    String temporaryExitFaceLibPathjson = readFile(temporaryExitFaceLibPath2);
                                    JSONObject temporaryExitFaceLibobject=new JSONObject(temporaryExitFaceLibPathjson);
                                    JSONArray temporaryExitFaceLibListArry = temporaryExitFaceLibobject.getJSONArray("FaceListsArry");
                                    System.out.println("人脸库集合："+temporaryExitFaceLibListArry);
                                    JSONObject temporaryExitFaceLibInfo=temporaryExitFaceLibListArry.getJSONObject(0);
                                    exitRealszLibName=temporaryExitFaceLibInfo.getStr("FaceListName");
                                    exitRealLibType = Integer.parseInt(temporaryExitFaceLibInfo.getStr("FaceListType"));
                                    exitRealulFaceLibID = Integer.parseInt(temporaryExitFaceLibInfo.getStr("ID"));
                                    exitRealuiThreshold=Integer.parseInt(temporaryExitFaceLibInfo.getStr("Threshold"));
                                }
                            }
                            //查询第一个facelib的入口的人脸信息
                            System.out.println("查询临时入口的人脸信息-------------------------------------------");
                            String temporaryEntryRealFacePath = new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.TemporaryEntryRealFace)
                                    .toString();
                            File temporaryEntryRealFace = new File(temporaryEntryRealFacePath);
                            if (!temporaryEntryRealFace.exists()) {
                                temporaryEntryRealFace.mkdirs();
                            }
                            PU_FACE_INFO_FIND_S temporaryEntryRealFaceFind = new PU_FACE_INFO_FIND_S();
                            temporaryEntryRealFaceFind.ulChannelId=new NativeLong(101);
                            String temporaryentryfaceInfoPath=temporaryEntryRealFacePath+"/temporaryEntryRealFace.json";
                            temporaryEntryRealFaceFind.szFindResultPath= Arrays.copyOf(temporaryentryfaceInfoPath.getBytes(),129);
                            PU_FACE_LIB_S temporaryentryfacelib = new PU_FACE_LIB_S();
                            temporaryentryfacelib.ulFaceLibID=new NativeLong(entryRealulFaceLibID);
                            if(Platform.isWindows()){
                                temporaryentryfacelib.szLibName=Arrays.copyOf(entryRealszLibName.getBytes("gbk"),65);
                            }else {
                                temporaryentryfacelib.szLibName=Arrays.copyOf(entryRealszLibName.getBytes("utf8"),65);
                            }
                            temporaryentryfacelib.enLibType=entryRealenLibType;
                            temporaryentryfacelib.uiThreshold=new NativeLong(entryRealuiThreshold);
                            FACE_FIND_CONDITION entryfaceFindCondition=new FACE_FIND_CONDITION();
                            entryfaceFindCondition.enFeatureStatus=1;
                            entryfaceFindCondition.szName= ByteBuffer.allocate(64).put("".getBytes()).array();
                            entryfaceFindCondition.szProvince=ByteBuffer.allocate(32).put("".getBytes()).array();
                            entryfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                            entryfaceFindCondition.szCardID=ByteBuffer.allocate(32).put("".getBytes()).array();
                            entryfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                            entryfaceFindCondition.enGender=-1;
                            entryfaceFindCondition.enCardType=-1;
                            temporaryEntryRealFaceFind.stCondition=entryfaceFindCondition;
                            temporaryEntryRealFaceFind.stFacelib=temporaryentryfacelib;
                            temporaryEntryRealFaceFind.uStartIndex=0;
                            boolean entrygetFace;
                            if (Platform.isWindows()) {
                                entrygetFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, temporaryEntryRealFaceFind);
                                log.debug("临时人员名字："+faceInfo.getUserName());
                                log.error("临时人员定时任务查询人脸入口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();

                            } else {
                                entrygetFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryulIdentifyId, temporaryEntryRealFaceFind);
                                log.debug("临时人员名字："+faceInfo.getUserName());
                                log.error("临时人员定时任务查询人脸入口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            System.out.println("查询临时出口的人脸信息-------------------------------------------");
                            String temporaryExitRealFacePath = new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.TemporaryExitRealFace)
                                    .toString();
                            File temporaryExitRealFaceFile = new File(temporaryExitRealFacePath);
                            if (!temporaryExitRealFaceFile.exists()) {
                                temporaryExitRealFaceFile.mkdirs();
                            }
                            PU_FACE_INFO_FIND_S temporaryExitRealFaceFind = new PU_FACE_INFO_FIND_S();
                            temporaryExitRealFaceFind.ulChannelId=new NativeLong(101);
                            String temporaryExitRealFace=temporaryExitRealFacePath+"/temporaryExitRealFace.json";
                            temporaryExitRealFaceFind.szFindResultPath= Arrays.copyOf(temporaryExitRealFace.getBytes(),129);
                            PU_FACE_LIB_S temporaryExitfacelib = new PU_FACE_LIB_S();
                            temporaryExitfacelib.ulFaceLibID=new NativeLong(exitRealulFaceLibID);
                            if(Platform.isWindows()){
                                temporaryExitfacelib.szLibName=Arrays.copyOf(exitRealszLibName.getBytes("gbk"),65);
                            }else {
                                temporaryExitfacelib.szLibName=Arrays.copyOf(exitRealszLibName.getBytes("utf8"),65);
                            }
                            temporaryExitfacelib.enLibType=exitRealLibType;
                            temporaryExitfacelib.uiThreshold=new NativeLong(exitRealuiThreshold);
                            FACE_FIND_CONDITION exitfaceFindCondition=new FACE_FIND_CONDITION();
                            exitfaceFindCondition.enFeatureStatus=1;
                            exitfaceFindCondition.szName= ByteBuffer.allocate(64).put("".getBytes()).array();
                            exitfaceFindCondition.szProvince=ByteBuffer.allocate(32).put("".getBytes()).array();
                            exitfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                            exitfaceFindCondition.szCardID=ByteBuffer.allocate(32).put("".getBytes()).array();
                            exitfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                            exitfaceFindCondition.enGender=-1;
                            exitfaceFindCondition.enCardType=-1;
                            temporaryExitRealFaceFind.stCondition=exitfaceFindCondition;
                            temporaryExitRealFaceFind.stFacelib=temporaryExitfacelib;
                            temporaryExitRealFaceFind.uStartIndex=0;
                            boolean exitgetFace;
                            if (Platform.isWindows()) {
                                exitgetFace = HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, temporaryExitRealFaceFind);
                                log.debug("临时人员名字："+faceInfo.getUserName());
                                log.debug("临时人员定时任务查询人脸出口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();
                            } else {
                                exitgetFace = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitulIdentifyId, temporaryExitRealFaceFind);
                                log.debug("临时人员名字："+faceInfo.getUserName());
                                log.debug("临时人员定时任务查询人脸出口信息返回码：");
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            if (entrygetFace || exitgetFace) {
                                log.debug("临时人员定时任务查询人脸信息成功---------------");
                                /**
                                 *  -------查询到临时出口人脸信息json数据
                                 */
                                if (entrygetFace){
                                    String temporaryExitRealFacePath2 = temporaryExitRealFacePath+File.separator+"temporaryExitRealFace.json";
                                    String temporaryExitRealFaceJson = readFile(temporaryExitRealFacePath2);
                                    JSONObject temporaryExitRealFaceObject = new JSONObject(temporaryExitRealFaceJson);
                                    if (temporaryExitRealFaceObject==null || temporaryExitRealFaceObject.isEmpty()){
                                        log.debug("临时人员定时任务查询人脸信息为空");
                                    }
                                    log.debug("临时人员定时任务查询jsonObject:"+temporaryExitRealFaceObject);
                                    JSONArray temporaryExitRealFaceArry = temporaryExitRealFaceObject.getJSONArray("FaceRecordArry");
                                    log.debug("临时人员定时任务查询人脸信息faceRecordArry出口摄像头集合：" + temporaryExitRealFaceArry);
                                    ArrayList<FaceInfo> exithoutaifaceInfos = new ArrayList<>();
                                    for (int i = 0; i < temporaryExitRealFaceArry.size(); i++) {
                                        FaceInfo exithoutaiifaceInfo = new FaceInfo();
                                        JSONObject exitfaceInfojson = temporaryExitRealFaceArry.getJSONObject(i);
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
                                                exitfacelib.ulFaceLibID = new NativeLong(exitRealulFaceLibID);
                                                if (Platform.isWindows()) {
                                                    exitfacelib.szLibName = Arrays.copyOf(exitRealszLibName.getBytes("gbk"), 65);
                                                } else {
                                                    exitfacelib.szLibName = Arrays.copyOf(exitRealszLibName.getBytes("utf8"), 65);
                                                }
                                                exitfacelib.enLibType = exitRealLibType;
                                                exitfacelib.uiThreshold = new NativeLong(exitRealuiThreshold);
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
                                }
                                /**
                                 *  查询到入口人脸信息json数据
                                 */
                                if (exitgetFace){
                                    String temporaryEntryRealFacePath2 = temporaryEntryRealFacePath+File.separator+"temporaryEntryRealFace.json";
                                    log.debug("实时查询文件路径："+temporaryEntryRealFacePath2);
                                    String temporaryEntryRealFaceJson = readFile(temporaryEntryRealFacePath2);
                                    JSONObject temporaryEntryRealFaceObject = new JSONObject(temporaryEntryRealFaceJson);
                                    log.debug("临时人员定时任务查询jsonObject:"+temporaryEntryRealFaceObject);
                                    JSONArray temporaryEntryRealFaceRecordArry = temporaryEntryRealFaceObject.getJSONArray("FaceRecordArry");
                                    log.debug("临时人员定时任务查询人脸信息faceRecordArry出口摄像头集合：" + temporaryEntryRealFaceRecordArry);
                                    ArrayList<FaceInfo> entryhoutaifaceInfos = new ArrayList<>();
                                    for (int i = 0; i < temporaryEntryRealFaceRecordArry.size(); i++) {
                                        FaceInfo entryhoutaiifaceInfo = new FaceInfo();
                                        JSONObject entryfaceInfojson = temporaryEntryRealFaceRecordArry.getJSONObject(i);
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
                                                entryfacelib.ulFaceLibID = new NativeLong(entryRealulFaceLibID);
                                                if (Platform.isWindows()) {
                                                    entryfacelib.szLibName = Arrays.copyOf(entryRealszLibName.getBytes("gbk"), 65);
                                                } else {
                                                    entryfacelib.szLibName = Arrays.copyOf(entryRealszLibName.getBytes("utf8"), 65);
                                                }
                                                entryfacelib.enLibType = entryRealenLibType;
                                                entryfacelib.uiThreshold = new NativeLong(entryRealuiThreshold);
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
                             * 先查询内部出口人脸库
                             */
                            PU_FACE_LIB_GET_S innerExitFaceLib =new PU_FACE_LIB_GET_S();
                            innerExitFaceLib.ulChannelId=new NativeLong(101);
                            innerExitFaceLib.ulFaceLibNum=new NativeLong(1);
                            String innerExitFaceLibPath=new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.InnerExitFaceLib)
                                    .toString();
                            File innerExitFaceLibFile=new File(innerExitFaceLibPath);
                            if(!innerExitFaceLibFile.exists()){
                                innerExitFaceLibFile.mkdirs();
                            }
                            String delInnerExitFaceLib=innerExitFaceLibPath+File.separator+"innerExitFaceLib.json";
                            innerExitFaceLib.szFindResultPath= Arrays.copyOf(delInnerExitFaceLib.getBytes(),128);
                            /**
                             * 先查询内部入口人脸库
                             */
                            PU_FACE_LIB_GET_S innerEntryFaceLib =new PU_FACE_LIB_GET_S();
                            innerEntryFaceLib.ulChannelId=new NativeLong(101);
                            innerEntryFaceLib.ulFaceLibNum=new NativeLong(1);
                            String innerEntryFaceLibPath=new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.InnerEntryFaceLib)
                                    .toString();
                            File innerEntryFaceLibFile=new File(innerEntryFaceLibPath);
                            if(!innerEntryFaceLibFile.exists()){
                                innerEntryFaceLibFile.mkdirs();
                            }
                            String delInnerEntryFaceLib=innerEntryFaceLibPath+File.separator+"innerEntryFaceLib.json";
                            innerEntryFaceLib.szFindResultPath= Arrays.copyOf(delInnerEntryFaceLib.getBytes(),128);
                            boolean delInnerEntrygetFaceLib;
                            boolean delInnerExitgetFaceLib;
                            if(Platform.isWindows()){
                                delInnerEntrygetFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(entryulIdentifyId, innerEntryFaceLib);
                                delInnerExitgetFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(exitulIdentifyId, innerExitFaceLib);
                            }else {
                                delInnerEntrygetFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(entryulIdentifyId, innerEntryFaceLib);
                                delInnerExitgetFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(exitulIdentifyId, innerExitFaceLib);
                            }
                            //出口
                            Integer exitInnerLibType=null;//人脸库类型
                            Integer exitInnerulFaceLibID=null;//人脸库id
                            String exitInnerszLibName=null;//人脸库名称
                            Integer exitInneruiThreshold=null;//布控的阀值
                            //入口
                            Integer entryInnerenLibType=null;//人脸库类型
                            Integer entryInnerulFaceLibID=null;//人脸库id
                            String entryInnerszLibName=null;//人脸库名称
                            Integer entryInneruiThreshold=null;//布控的阀值
                                if (entrydeviceInfo==null){
                                    entryInnerszLibName="facelib";
                                    entryInnerenLibType=2;
                                    entryInneruiThreshold=90;
                                    entryInnerulFaceLibID=1111;
                                }else if (delInnerEntrygetFaceLib){
                                    System.out.println("查询入口库人脸库成功------------------------");
                                    String innerEntryFaceLibPath2 = new String(new File(".").getCanonicalPath() + File.separator +"innerEntryFaceLib"+File.separator+"innerEntryFaceLib.json");
                                    String innerEntryFaceLibPathJson = readFile(innerEntryFaceLibPath2);
                                    JSONObject innerEntryFaceLibObject=new JSONObject(innerEntryFaceLibPathJson);
                                    JSONArray innerEntryFaceLibArry = innerEntryFaceLibObject.getJSONArray("FaceListsArry");
                                    System.out.println("人脸库集合："+innerEntryFaceLibArry);
                                    JSONObject entrynewfacelibinfo=innerEntryFaceLibArry.getJSONObject(0);
                                    entryInnerszLibName=entrynewfacelibinfo.getStr("FaceListName");
                                    entryInnerenLibType = Integer.parseInt(entrynewfacelibinfo.getStr("FaceListType"));
                                    entryInnerulFaceLibID = Integer.parseInt(entrynewfacelibinfo.getStr("ID"));
                                    entryInneruiThreshold=Integer.parseInt(entrynewfacelibinfo.getStr("Threshold"));
                                }
                                if (exitdeviceInfo==null){
                                    exitInnerszLibName="facelib";
                                    exitInnerLibType=2;
                                    exitInneruiThreshold=90;
                                    exitInnerulFaceLibID=1111;
                                }else if (delInnerExitgetFaceLib){
                                    System.out.println("查询出口库人脸库成功------------------------");
                                    String innerExitFaceLibPath2 = new String(new File(".").getCanonicalPath() + File.separator +"innerExitFaceLib"+File.separator+"innerExitFaceLib.json");
                                    String innerExitFaceLibPathJson = readFile(innerExitFaceLibPath2);
                                    JSONObject innerExitFaceLibObject=new JSONObject(innerExitFaceLibPathJson);
                                    JSONArray innerExitFaceLibArry = innerExitFaceLibObject.getJSONArray("FaceListsArry");
                                    System.out.println("人脸库集合："+innerExitFaceLibArry);
                                    JSONObject innerExitFaceLibInfo=innerExitFaceLibArry.getJSONObject(0);
                                    exitInnerszLibName=innerExitFaceLibInfo.getStr("FaceListName");
                                    exitInnerLibType = Integer.parseInt(innerExitFaceLibInfo.getStr("FaceListType"));
                                    exitInnerulFaceLibID = Integer.parseInt(innerExitFaceLibInfo.getStr("ID"));
                                    exitInneruiThreshold=Integer.parseInt(innerExitFaceLibInfo.getStr("Threshold"));
                                }
                            /**
                             * 查询第一个facelib的入口---人脸信息
                             */
                            String innerEntryRealFaceInfoPath = new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.InnerEntryRealFaceInfo)
                                    .toString();
                            File innerEntryRealFaceInfo = new File(innerEntryRealFaceInfoPath);
                            if (!innerEntryRealFaceInfo.exists()) {
                                innerEntryRealFaceInfo.mkdirs();
                            }
                            PU_FACE_INFO_FIND_S entryfaceInfoFindS = new PU_FACE_INFO_FIND_S();
                            entryfaceInfoFindS.ulChannelId = new NativeLong(101);
                            String  entryfaceInfoPath = innerEntryRealFaceInfoPath + "/innerEntryRealFaceInfo.json";
                            entryfaceInfoFindS.szFindResultPath = Arrays.copyOf(entryfaceInfoPath.getBytes(), 129);
                            PU_FACE_LIB_S entryfacelib2 = new PU_FACE_LIB_S();
                            entryfacelib2.ulFaceLibID = new NativeLong(entryInnerulFaceLibID);
                            if (Platform.isWindows()) {
                                entryfacelib2.szLibName = Arrays.copyOf(entryInnerszLibName.getBytes("gbk"), 65);
                            } else {
                                entryfacelib2.szLibName = Arrays.copyOf(entryInnerszLibName.getBytes("utf8"), 65);
                            }
                            entryfacelib2.enLibType = entryInnerenLibType;
                            entryfacelib2.uiThreshold = new NativeLong(entryInneruiThreshold);
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
                            String innerExitRealFaceInfoPath = new StringBuilder()
                                    .append(SystemUtil.getUserInfo().getCurrentDir())
                                    .append(File.separator)
                                    .append(MainAction.InnerExitRealFaceInfo)
                                    .toString();
                            File innerExitRealFaceInfo = new File(innerExitRealFaceInfoPath);
                            if (!innerExitRealFaceInfo.exists()) {
                                innerExitRealFaceInfo.mkdirs();
                            }
                            PU_FACE_INFO_FIND_S exitfaceInfoFindS = new PU_FACE_INFO_FIND_S();
                            exitfaceInfoFindS.ulChannelId = new NativeLong(101);
                            String  exitfaceInfoPath = innerExitRealFaceInfoPath + "/innerExitRealFaceInfo.json";
                            exitfaceInfoFindS.szFindResultPath = Arrays.copyOf(exitfaceInfoPath.getBytes(), 129);
                            PU_FACE_LIB_S exitfacelib2 = new PU_FACE_LIB_S();
                            exitfacelib2.ulFaceLibID = new NativeLong(exitInnerulFaceLibID);
                            if (Platform.isWindows()) {
                                exitfacelib2.szLibName = Arrays.copyOf(exitInnerszLibName.getBytes("gbk"), 65);
                            } else {
                                exitfacelib2.szLibName = Arrays.copyOf(exitInnerszLibName.getBytes("utf8"), 65);
                            }
                            exitfacelib2.enLibType = exitInnerLibType;
                            exitfacelib2.uiThreshold = new NativeLong(exitInneruiThreshold);
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
                            if (exitgetFace || entrygetFace){
                                log.debug("内部人员定时任务查询人脸信息成功");
                                /**
                                 *  -------查询到内部---出口人脸信息json数据
                                 */
                                if (exitgetFace){
                                    String innerExitgetfaceInfoPaths = innerExitRealFaceInfoPath+File.separator+"innerExitRealFaceInfo.json";
                                    log.debug("实时查询文件路径："+innerExitgetfaceInfoPaths);
                                    String innerExitfacejson = readFile(innerExitgetfaceInfoPaths);
                                    log.debug("neibuexitfacejson：---"+innerExitfacejson);
                                    JSONObject neibuExitjsonObject = new JSONObject(innerExitfacejson);
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
                                                neibuExitfacelib.ulFaceLibID = new NativeLong(exitInnerulFaceLibID);
                                                if (Platform.isWindows()) {
                                                    neibuExitfacelib.szLibName = Arrays.copyOf(exitInnerszLibName.getBytes("gbk"), 65);
                                                } else {
                                                    neibuExitfacelib.szLibName = Arrays.copyOf(exitInnerszLibName.getBytes("utf8"), 65);
                                                }
                                                neibuExitfacelib.enLibType = exitInnerLibType;
                                                neibuExitfacelib.uiThreshold = new NativeLong(exitInneruiThreshold);
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
                                }
                                /**
                                 *  -------查询到内部---入口人脸信息json数据
                                 */
                                if (entrygetFace){
                                    String neibuEntrygetfaceInfoPaths = innerEntryRealFaceInfoPath+File.separator+"innerEntryRealFaceInfo.json";
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
                                                neibuEntryfacelib.ulFaceLibID = new NativeLong(entryInnerulFaceLibID);
                                                if (Platform.isWindows()) {
                                                    neibuEntryfacelib.szLibName = Arrays.copyOf(entryInnerszLibName.getBytes("gbk"), 65);
                                                } else {
                                                    neibuEntryfacelib.szLibName = Arrays.copyOf(entryInnerszLibName.getBytes("utf8"), 65);
                                                }
                                                neibuEntryfacelib.enLibType = entryInnerenLibType;
                                                neibuEntryfacelib.uiThreshold = new NativeLong(entryInneruiThreshold);
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