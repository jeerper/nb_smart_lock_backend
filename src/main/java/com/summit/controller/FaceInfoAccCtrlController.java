package com.summit.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.system.SystemUtil;
import com.summit.MainAction;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;
import com.summit.sdk.huawei.*;
import com.summit.sdk.huawei.api.HuaWeiSdkApi;
import com.summit.sdk.huawei.model.DeviceInfo;
import com.summit.service.AccessControlService;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.service.FaceInfoManagerService;
import com.sun.jna.IntegerType;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/8/29.
 */
@Slf4j
@Api(tags = "人脸门禁授权管理接口")
@RestController
@RequestMapping("/faceInfoAccCtrl")
public class FaceInfoAccCtrlController {

    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    FaceInfoManagerService faceInfoManagerService;

    @ApiOperation(value = "批量刷新指定人脸关联的门禁",notes = "为指定的人脸信息更新门禁权限，所传的人脸信息之前没有关联某门禁且所传列表中有添加，之前已关联过门禁而所传列表中有则不添加，之前已关联过门禁而所传列表中没有则删除与摄像头同步")
    @PostMapping("/authorityFaceInfoAccCtrl")
    public RestfulEntityBySummit<String> authorityFaceInfoAccCtrl(@ApiParam(value = "门禁id",required = true) @RequestParam(value = "accessControlId")String accessControlId,
                                                                  @ApiParam(value = "人脸id列表",required = true) @RequestParam(value = "faceids") List<String> faceids) throws IOException, ParseException {
        if(faceids==null){
            log.error("人脸信息id列表为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸id列表为空",null);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date date = new Date();
        String nowtime = df.format(date);
        long nowDate = df.parse(nowtime).getTime();
        for(String faceid:faceids){
            FaceInfo faceInfo = faceInfoManagerService.selectFaceInfoByID(faceid);
            Date faceEndTime = faceInfo.getFaceEndTime();
            long faceEndDate = faceEndTime.getTime();
            if (nowDate>faceEndDate){
                log.error("授权人脸中含有有效期过期人脸");
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"授权人脸中含有有效期过期人脸",null);
            }
        }
        if(accessControlId==null){
            log.error("门禁信息id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁信息id为空",null);
        }
        int result=faceInfoAccCtrlService.authorityFaceInfoAccCtrl(accessControlId,faceids);
        if(result== CommonConstants.UPDATE_ERROR){
            log.error("人脸门禁授权失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"人脸门禁授权失败",null);
        }
        log.error("人脸门禁授权成功");
        AccessControlInfo accessControlInfo = accessControlService.selectAccCtrlByIdBeyondAuthority(accessControlId);
        String entryCameraIp = accessControlInfo.getEntryCameraIp();
        String exitCameraIp = accessControlInfo.getExitCameraIp();
        System.out.println(entryCameraIp+"入口摄像头ip");
        System.out.println(exitCameraIp+"出口摄像头ip");
        List<FaceInfo> faceInfoList=new ArrayList<>();
        for(String faceid: faceids){
            FaceInfo faceInfo = faceInfoManagerService.selectFaceInfoByID(faceid);
            faceInfoList.add(faceInfo);
        }
        System.out.println(faceInfoList+"授权的人脸对象");
        //把人脸信息集合加入到对应摄像头的ip中

        //把人脸信息集合先加入到入口摄像头中
        DeviceInfo entrydeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(entryCameraIp);
        NativeLong entryIdentifyId;
        if (entrydeviceInfo==null){
            entryIdentifyId=new NativeLong(100);
        }else {
            entryIdentifyId= entrydeviceInfo.getUlIdentifyId();
        }
        System.out.println(entryIdentifyId+"：entryIdentifyId");
        //把人脸信息再加入到出口摄像头中
        DeviceInfo exitdeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(exitCameraIp);
        NativeLong exitIdentifyId;
        if (exitdeviceInfo==null){
             exitIdentifyId = new NativeLong(222);
        }else {
             exitIdentifyId = exitdeviceInfo.getUlIdentifyId();
        }
        System.out.println(exitIdentifyId+"：exitIdentifyId");
        /**1 先查询人脸库，如果有人脸库，再查询人脸信息，如果没有则新建人脸库，直接添加人脸信息，如果有人脸库，没有人脸信息，直接添加人脸信息
         * 2 如果有人脸库再查询人脸信息，
         * 3 若传入集合列表为空，则直接删除人脸
         * 4、先删除数据库在所传入列表找不到的人脸
         * 5  再添加传入列表在数据库中找不到的人脸
         */
        //1 先查询人脸库
        PU_FACE_LIB_GET_S faceLibGetS =new PU_FACE_LIB_GET_S();
        faceLibGetS.ulChannelId=new NativeLong(101);
        faceLibGetS.ulFaceLibNum=new NativeLong(1);
        String facelibPath=new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(MainAction.FaceLib)
                .toString();
        File f=new File(facelibPath);
        if(!f.exists()){
            f.mkdirs();
        }
        String faceLib=facelibPath+File.separator+"faceLib.json";
        faceLibGetS.szFindResultPath= Arrays.copyOf(faceLib.getBytes(),128);
        boolean getFaceLib;
        boolean exitgetFaceLib;
        if(Platform.isWindows()){
             getFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(entryIdentifyId, faceLibGetS);
             exitgetFaceLib =HWPuSDKLibrary.INSTANCE.IVS_PU_GetFaceLib(exitIdentifyId, faceLibGetS);
        }else {
             getFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(entryIdentifyId, faceLibGetS);
             exitgetFaceLib =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetFaceLib(exitIdentifyId, faceLibGetS);
        }
        Integer enLibType=null;//人脸库类型
        Integer ulFaceLibID=null;//人脸库id
        String szLibName=null;//人脸库名称
        Integer uiThreshold=null;//布控的阀值
        HuaWeiSdkApi.printReturnMsg();
        if(getFaceLib && exitgetFaceLib){
            System.out.println("查询人脸库成功");
            String getfacelibpath1 = new String(new File(".").getCanonicalPath() + File.separator +"facelib"+File.separator+"faceLib.json");
            String json = readFile(getfacelibpath1);
            JSONObject object=new JSONObject(json);
            JSONArray faceListsArry = object.getJSONArray("FaceListsArry");
            System.out.println("人脸库集合："+faceListsArry);
            //2 判断有没有人脸库，没有人脸库则直接添加人脸库，接着添加人脸信息
            if(faceListsArry==null || faceListsArry.size()==0){ //说明没有人脸库则需要添加人脸库,接着添加人脸信息
                PU_FACE_LIB_SET_S enteypuFaceLibSetS =new PU_FACE_LIB_SET_S();
                enteypuFaceLibSetS.enOptType=1;//新增人脸库
                PU_FACE_LIB_S  enteystFacelib=new PU_FACE_LIB_S();
                if(Platform.isWindows()){
                    enteystFacelib.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);//windows名单库的名称
                }else {
                    enteystFacelib.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);//名单库的名称
                }
                enteystFacelib.uiThreshold=new NativeLong(90);//布控的阀值
                enteystFacelib.enLibType=2;//人脸库类型
                enteystFacelib.isControl=true;
                enteypuFaceLibSetS.stFacelib=enteystFacelib;
                enteypuFaceLibSetS.ulChannelId=new NativeLong(101);
                boolean entryaddFaceLib;
                boolean exitaddFaceLib2;
                if(Platform.isWindows()){
                     entryaddFaceLib = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, enteypuFaceLibSetS);
                     System.out.println("添加入口人脸库返回码");
                     HuaWeiSdkApi.printReturnMsg();
                }else {
                    entryaddFaceLib  = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, enteypuFaceLibSetS);
                    System.out.println("添加入口人脸库返回码");
                    HuaWeiSdkApi.printReturnMsg();
                }
                System.out.println("添加出口库--------------------------------------------");
                PU_FACE_LIB_SET_S exitpuFaceLibSetS =new PU_FACE_LIB_SET_S();
                exitpuFaceLibSetS.enOptType=1;//新增人脸库
                PU_FACE_LIB_S  exitstFacelib=new PU_FACE_LIB_S();
                if(Platform.isWindows()){
                    exitstFacelib.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);//windows名单库的名称
                }else {
                    exitstFacelib.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);//名单库的名称
                }
                exitstFacelib.uiThreshold=new NativeLong(90);//布控的阀值
                exitstFacelib.enLibType=2;//人脸库类型
                exitstFacelib.isControl=true;
                exitpuFaceLibSetS.stFacelib=exitstFacelib;
                exitpuFaceLibSetS.ulChannelId=new NativeLong(101);
                if (Platform.isWindows()){
                     exitaddFaceLib2= HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, exitpuFaceLibSetS);
                     System.out.println("添加出口人脸库返回码");
                    HuaWeiSdkApi.printReturnMsg();
                }else {
                    exitaddFaceLib2  = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, exitpuFaceLibSetS);
                    System.out.println("添加入口人脸库返回码");
                    HuaWeiSdkApi.printReturnMsg();
                }
                if (entryaddFaceLib && exitaddFaceLib2){//人脸库添加成功，接着添加人脸信息，循环添加
                    System.out.println("人脸库添加成功");
                    //新建完再次人脸库查询人脸库取人脸库信息,并且给人脸库信息赋值
                    szLibName="facelib";
                    enLibType = 2;
                    ulFaceLibID = 1;
                    uiThreshold=90;
                    //添加人脸信息，循环添加
                    for(FaceInfo faceInfo:faceInfoList){
                        //入口设置人脸库对象
                        System.out.println("添加入口人脸信息-------------------------------------------------------------");
                        PU_FACE_LIB_S stFacelib1=new PU_FACE_LIB_S();
                        stFacelib1.enLibType=enLibType;
                        stFacelib1.isControl=true;
                        stFacelib1.ulFaceLibID=new NativeLong(ulFaceLibID);
                        if(Platform.isWindows()){
                            stFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("GBK"),65);
                        }else {
                            stFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                        }
                        stFacelib1.uiThreshold=new NativeLong(uiThreshold);
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
                        String absolutePath = new String(new File(".").getCanonicalPath() + faceInfo.getFaceImage());
                        addface.szPicPath=Arrays.copyOf(absolutePath.getBytes(),128);
                        puFaceInfoAdd.stRecord=addface;
                        String filename = faceInfo.getFaceImage().substring(faceInfo.getFaceImage().lastIndexOf("/")+1);
                        boolean entryaddface2;
                        if (Platform.isWindows()) {
                            entryaddface2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, puFaceInfoAdd, filename);
                        }else {
                            entryaddface2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, puFaceInfoAdd, filename);
                        }
                        System.out.println("添加人脸入口摄像头返回码------");
                        HuaWeiSdkApi.printReturnMsg();
                        /**
                         * 提取人脸特征
                         */
                        //添加完人脸信息之后需要提取特征值
                        if (entryaddface2){//添加入口人脸成功
                            PU_FACE_LIB_S entrystFacelib1=new PU_FACE_LIB_S();
                            entrystFacelib1.enLibType=2;
                            entrystFacelib1.isControl=true;
                            entrystFacelib1.ulFaceLibID=new NativeLong(1);
                            if (Platform.isWindows()){
                                entrystFacelib1.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);
                            }else {
                                entrystFacelib1.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);
                            }
                            entrystFacelib1.uiThreshold=new NativeLong(90);
                            PU_FACE_FEATURE_EXTRACT_S entrypuFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                            entrypuFaceFeatureExtractS.stFacelib=entrystFacelib1;
                            entrypuFaceFeatureExtractS.ulChannelId=new NativeLong(101);
                            boolean getTeZheng;
                            if (Platform.isWindows()){
                                 getTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,entrypuFaceFeatureExtractS);
                                System.out.println("提取人脸入口特征值返回码------");
                                HuaWeiSdkApi.printReturnMsg();
                            }else {
                                 getTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,entrypuFaceFeatureExtractS);
                                System.out.println("提取人脸入口特征值返回码------");
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            if (getTeZheng){
                                log.debug("提取入口人脸特征值成功------");
                            }else {
                                log.debug("提取入口人脸特征值失败------");
                                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"提取入口人脸特征值失败,图片不规范",null);
                            }
                        }else {
                            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"添加入口人脸失败",null);
                        }

                        System.out.println("添加出口人脸信息-------------------------------------------------------------");
                        PU_FACE_LIB_S exitstFacelib2=new PU_FACE_LIB_S();
                        exitstFacelib2.enLibType=enLibType;
                        exitstFacelib2.isControl=true;
                        exitstFacelib2.ulFaceLibID=new NativeLong(ulFaceLibID);
                        if(Platform.isWindows()){
                            exitstFacelib2.szLibName=Arrays.copyOf(szLibName.getBytes("GBK"),65);
                        }else {
                            exitstFacelib2.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                        }
                        exitstFacelib2.uiThreshold=new NativeLong(uiThreshold);
                        //设置添加人脸信息的对象
                        PU_FACE_INFO_ADD_S  exitpuFaceInfoAdd = new PU_FACE_INFO_ADD_S();
                        exitpuFaceInfoAdd.stFacelib=exitstFacelib2;
                        exitpuFaceInfoAdd.ulChannelId=new NativeLong(101);
                        //设置人脸信息
                        PU_FACE_RECORD exitaddface=new PU_FACE_RECORD();
                        exitaddface.enCardType=faceInfo.getCardType();
                        exitaddface.enGender=faceInfo.getGender();
                        SimpleDateFormat exitsdf = new SimpleDateFormat("yyyy-MM-dd");
                        String exitbirthday = exitsdf.format(faceInfo.getBirthday());
                        exitaddface.szBirthday=Arrays.copyOf(exitbirthday.getBytes(),32);
                        exitaddface.szCardID=Arrays.copyOf(faceInfo.getCardId().getBytes(),32);
                        if(Platform.isWindows()){
                            exitaddface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("GBK"),48);
                            exitaddface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("GBK"),64);
                            exitaddface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("GBK"),32);
                        }else {
                            exitaddface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("utf8"),48);
                            exitaddface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("utf8"),64);
                            exitaddface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("utf8"),32);
                        }
                        String exitabsolutePath = new String(new File(".").getCanonicalPath() + faceInfo.getFaceImage());
                        exitaddface.szPicPath=Arrays.copyOf(exitabsolutePath.getBytes(),128);
                        exitpuFaceInfoAdd.stRecord=exitaddface;
                        String exitfilename = faceInfo.getFaceImage().substring(faceInfo.getFaceImage().lastIndexOf("/")+1);
                        boolean  exitaddface2;
                        if (Platform.isWindows()) {
                            exitaddface2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, exitpuFaceInfoAdd, exitfilename);
                        }else {
                            exitaddface2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, exitpuFaceInfoAdd, exitfilename);
                        }
                        System.out.println("添加人脸入口摄像头返回码---");
                        HuaWeiSdkApi.printReturnMsg();
                        if (exitaddface2){//添加入口人脸成功
                            PU_FACE_LIB_S exitstFacelib1=new PU_FACE_LIB_S();
                            exitstFacelib1.enLibType=2;
                            exitstFacelib1.isControl=true;
                            exitstFacelib1.ulFaceLibID=new NativeLong(1);
                            if (Platform.isWindows()){
                                exitstFacelib1.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);
                            }else {
                                exitstFacelib1.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);
                            }
                            exitstFacelib1.uiThreshold=new NativeLong(90);
                            PU_FACE_FEATURE_EXTRACT_S exitpuFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                            exitpuFaceFeatureExtractS.stFacelib=exitstFacelib1;
                            exitpuFaceFeatureExtractS.ulChannelId=new NativeLong(101);
                            boolean exitgetTeZheng;
                            if (Platform.isWindows()){
                                exitgetTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,exitpuFaceFeatureExtractS);
                                System.out.println("提取出口人脸特征值返回码------");
                                HuaWeiSdkApi.printReturnMsg();
                            }else {
                                exitgetTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,exitpuFaceFeatureExtractS);
                                System.out.println("提取出口人脸特征值返回码------");
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            if (exitgetTeZheng){
                                log.debug("提取出口人脸特征值成功------");
                            }else {
                                log.debug("提取出口人脸特征值失败------");
                                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"提取出口人脸特征值失败,图片不规范",null);
                            }
                        }else {
                            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"添加出口人脸失败",null);
                        }
                    }
                }else {
                    System.out.println("人脸库添加失败");
                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"人脸库添加失败",null);
                }
            }else { //说明有人脸库取第一个人脸库
                String getfacelibpath2 = new String(new File(".").getCanonicalPath() +File.separator +"facelib"+File.separator+"faceLib.json");
                String jason2 = readFile(getfacelibpath2);
                JSONObject object1=new JSONObject(jason2);
                JSONArray faceListsArry1 = object1.getJSONArray("FaceListsArry");
                System.out.println("人脸库集合："+faceListsArry1);
                JSONObject facelibinfo=faceListsArry1.getJSONObject(0);
                szLibName=facelibinfo.getStr("FaceListName");
                enLibType = Integer.parseInt(facelibinfo.getStr("FaceListType"));
                ulFaceLibID = Integer.parseInt(facelibinfo.getStr("ID"));
                uiThreshold=Integer.parseInt(facelibinfo.getStr("Threshold"));
                System.out.println("查询第一个人脸库的出口人脸信息-------------------------------------");
                String faceinfoPath=new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.FaceInfo)
                        .toString();
                File face=new File(faceinfoPath);
                if(!face.exists()){
                    face.mkdirs();
                }
                PU_FACE_INFO_FIND_S exitfaceInfoFindS = new PU_FACE_INFO_FIND_S();
                exitfaceInfoFindS.ulChannelId=new NativeLong(101);
                String faceInfoPath=faceinfoPath+"/faceInfo.json";
                exitfaceInfoFindS.szFindResultPath= Arrays.copyOf(faceInfoPath.getBytes(),129);
                PU_FACE_LIB_S exitfacelib2 = new PU_FACE_LIB_S();
                exitfacelib2.ulFaceLibID=new NativeLong(ulFaceLibID);
                if(Platform.isWindows()){
                    exitfacelib2.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                }else {
                    exitfacelib2.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                }
                exitfacelib2.enLibType=enLibType;
                exitfacelib2.uiThreshold=new NativeLong(uiThreshold);
                FACE_FIND_CONDITION exitfaceFindCondition=new FACE_FIND_CONDITION();
                exitfaceFindCondition.enFeatureStatus=1;
                exitfaceFindCondition.szName=ByteBuffer.allocate(64).put("".getBytes()).array();
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
                if(Platform.isWindows()){
                    exitgetFace =HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitIdentifyId, exitfaceInfoFindS);
                }else {
                    exitgetFace =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitIdentifyId, exitfaceInfoFindS);
                }
                System.out.println("查询入口的人脸信息-------------------------------------------");
                PU_FACE_INFO_FIND_S entryfaceInfoFindS = new PU_FACE_INFO_FIND_S();
                entryfaceInfoFindS.ulChannelId=new NativeLong(101);
                String entryfaceInfoPath=faceinfoPath+"/faceInfo.json";
                entryfaceInfoFindS.szFindResultPath= Arrays.copyOf(entryfaceInfoPath.getBytes(),129);
                PU_FACE_LIB_S entryfacelib2 = new PU_FACE_LIB_S();
                entryfacelib2.ulFaceLibID=new NativeLong(ulFaceLibID);
                if(Platform.isWindows()){
                    entryfacelib2.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                }else {
                    entryfacelib2.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                }
                entryfacelib2.enLibType=enLibType;
                entryfacelib2.uiThreshold=new NativeLong(uiThreshold);
                FACE_FIND_CONDITION entryfaceFindCondition=new FACE_FIND_CONDITION();
                entryfaceFindCondition.enFeatureStatus=1;
                entryfaceFindCondition.szName=ByteBuffer.allocate(64).put("".getBytes()).array();
                entryfaceFindCondition.szProvince=ByteBuffer.allocate(32).put("".getBytes()).array();
                entryfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                entryfaceFindCondition.szCardID=ByteBuffer.allocate(32).put("".getBytes()).array();
                entryfaceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                entryfaceFindCondition.enGender=-1;
                entryfaceFindCondition.enCardType=-1;
                entryfaceInfoFindS.stCondition=entryfaceFindCondition;
                entryfaceInfoFindS.stFacelib=exitfacelib2;
                entryfaceInfoFindS.uStartIndex=0;
                boolean entrygetFace;
                if(Platform.isWindows()){
                    entrygetFace =HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryIdentifyId, entryfaceInfoFindS);
                }else {
                    entrygetFace =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryIdentifyId, entryfaceInfoFindS);
                }
                if (exitgetFace && entrygetFace){
                    System.out.println("查询人脸信息成功");
                    //查询到人脸信息json数据
                    String getfaceInfoPath = new String(new File(".").getCanonicalPath() + File.separator+"faceInfo"+File.separator+"faceInfo.json");
                    String facejson = readFile(getfaceInfoPath);
                    JSONObject objectface=new JSONObject(facejson);
                    JSONArray faceRecordArry = objectface.getJSONArray("FaceRecordArry");
                    System.out.println("人脸信息集合："+faceRecordArry);
                    if(faceRecordArry==null || faceRecordArry.size()==0){//有人脸库没有人脸信息,这时候直接添加所传的人脸信息
                        for(FaceInfo faceInfo:faceInfoList){
                            //设置出口人脸库对象
                            System.out.println("出口人脸库对象------------------------------");
                            PU_FACE_LIB_S exitstFacelib1=new PU_FACE_LIB_S();
                            exitstFacelib1.enLibType=enLibType;
                            exitstFacelib1.isControl=true;
                            exitstFacelib1.ulFaceLibID=new NativeLong(ulFaceLibID);
                            if (Platform.isWindows()){
                                exitstFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                            }else {
                                exitstFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                            }
                            exitstFacelib1.uiThreshold=new NativeLong(uiThreshold);
                            //设置添加人脸信息的对象
                            PU_FACE_INFO_ADD_S  exitpuFaceInfoAdd = new PU_FACE_INFO_ADD_S();
                            exitpuFaceInfoAdd.stFacelib=exitstFacelib1;
                            exitpuFaceInfoAdd.ulChannelId=new NativeLong(101);
                            //设置人脸信息
                            PU_FACE_RECORD exitaddface=new PU_FACE_RECORD();
                            exitaddface.enCardType=faceInfo.getCardType();
                            exitaddface.enGender=faceInfo.getGender();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String birthday = sdf.format(faceInfo.getBirthday());
                            exitaddface.szBirthday=Arrays.copyOf(birthday.getBytes(),32);
                            exitaddface.szCardID=Arrays.copyOf(faceInfo.getCardId().getBytes(),32);
                            if(Platform.isWindows()){
                                exitaddface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("gbk"),64);
                                exitaddface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("gbk"),48);
                                exitaddface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("gbk"),32);
                            }else {
                                exitaddface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("utf8"),64);
                                exitaddface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("utf8"),48);
                                exitaddface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("utf8"),32);
                            }
                            String exitabsolutePath = new String(new File(".").getCanonicalPath() + faceInfo.getFaceImage());
                            System.out.println(exitabsolutePath+"图片路径");
                            exitaddface.szPicPath=Arrays.copyOf(exitabsolutePath.getBytes(),128);
                            exitpuFaceInfoAdd.stRecord=exitaddface;
                            String exitfilename = faceInfo.getFaceImage().substring(faceInfo.getFaceImage().lastIndexOf("/")+1);
                            boolean exitaddfaceinfo;
                            if(Platform.isWindows()){
                                 exitaddfaceinfo = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, exitpuFaceInfoAdd, exitfilename);
                                 HuaWeiSdkApi.printReturnMsg();
                            }else {
                                 exitaddfaceinfo = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, exitpuFaceInfoAdd, exitfilename);
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            /**
                             * 添加完提取出口人脸特征值
                             */
                            if (exitaddfaceinfo){
                                PU_FACE_LIB_S exitTiquFace=new PU_FACE_LIB_S();
                                exitTiquFace.enLibType=2;
                                exitTiquFace.isControl=true;
                                exitTiquFace.ulFaceLibID=new NativeLong(1);
                                if (Platform.isWindows()){
                                    exitTiquFace.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);
                                }else {
                                    exitTiquFace.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);
                                }
                                exitTiquFace.uiThreshold=new NativeLong(90);
                                PU_FACE_FEATURE_EXTRACT_S exitpuFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                                exitpuFaceFeatureExtractS.stFacelib=exitTiquFace;
                                exitpuFaceFeatureExtractS.ulChannelId=new NativeLong(101);
                                boolean exitgetTeZheng;
                                if (Platform.isWindows()){
                                    exitgetTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,exitpuFaceFeatureExtractS);
                                    System.out.println("提取出口人脸特征值返回码------");
                                    HuaWeiSdkApi.printReturnMsg();
                                }else {
                                    exitgetTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,exitpuFaceFeatureExtractS);
                                    System.out.println("提取出口人脸特征值返回码------");
                                    HuaWeiSdkApi.printReturnMsg();
                                }
                                if (exitgetTeZheng){
                                    log.debug("提取出口人脸特征值成功------");
                                }else {
                                    log.debug("提取出口人脸特征值失败------");
                                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"提取出口人脸特征值失败,图片不规范",null);
                                }
                            }else {
                                log.debug("有人脸库没有人脸信息时，添加出口人脸失败------");
                                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"有人脸库没有人脸信息时，添加出口人脸失败",null);
                            }

                            //设置入口人脸库对象
                            System.out.println("入口人脸库对象------------------------------");
                            PU_FACE_LIB_S entrystFacelib1=new PU_FACE_LIB_S();
                            entrystFacelib1.enLibType=enLibType;
                            entrystFacelib1.isControl=true;
                            entrystFacelib1.ulFaceLibID=new NativeLong(ulFaceLibID);
                            if (Platform.isWindows()){
                                entrystFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                            }else {
                                entrystFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                            }
                            entrystFacelib1.uiThreshold=new NativeLong(uiThreshold);
                            //设置添加人脸信息的对象
                            PU_FACE_INFO_ADD_S  entrypuFaceInfoAdd = new PU_FACE_INFO_ADD_S();
                            entrypuFaceInfoAdd.stFacelib=exitstFacelib1;
                            entrypuFaceInfoAdd.ulChannelId=new NativeLong(101);
                            //设置人脸信息
                            PU_FACE_RECORD entrytaddface=new PU_FACE_RECORD();
                            entrytaddface.enCardType=faceInfo.getCardType();
                            entrytaddface.enGender=faceInfo.getGender();
                            SimpleDateFormat enrtysdf = new SimpleDateFormat("yyyy-MM-dd");
                            String entrybirthday = enrtysdf.format(faceInfo.getBirthday());
                            entrytaddface.szBirthday=Arrays.copyOf(entrybirthday.getBytes(),32);
                            entrytaddface.szCardID=Arrays.copyOf(faceInfo.getCardId().getBytes(),32);
                            if(Platform.isWindows()){
                                entrytaddface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("gbk"),64);
                                entrytaddface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("gbk"),48);
                                entrytaddface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("gbk"),32);
                            }else {
                                entrytaddface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("utf8"),64);
                                entrytaddface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes("utf8"),48);
                                entrytaddface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes("utf8"),32);
                            }
                            String entryabsolutePath = new String(new File(".").getCanonicalPath() + faceInfo.getFaceImage());
                            entrytaddface.szPicPath=Arrays.copyOf(entryabsolutePath.getBytes(),128);
                            entrypuFaceInfoAdd.stRecord=entrytaddface;
                            String entryfilename = faceInfo.getFaceImage().substring(faceInfo.getFaceImage().lastIndexOf("/")+1);
                            boolean entrytaddfaceinfo;
                            if(Platform.isWindows()){
                                entrytaddfaceinfo = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, entrypuFaceInfoAdd, entryfilename);
                                HuaWeiSdkApi.printReturnMsg();
                            }else {
                                entrytaddfaceinfo = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, entrypuFaceInfoAdd, entryfilename);
                                HuaWeiSdkApi.printReturnMsg();
                            }
                            /**
                             * 添加完提取入口口人脸特征值
                             */
                            if (entrytaddfaceinfo){
                                PU_FACE_LIB_S entryTiquFace=new PU_FACE_LIB_S();
                                entryTiquFace.enLibType=2;
                                entryTiquFace.isControl=true;
                                entryTiquFace.ulFaceLibID=new NativeLong(1);
                                if (Platform.isWindows()){
                                    entryTiquFace.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);
                                }else {
                                    entryTiquFace.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);
                                }
                                entryTiquFace.uiThreshold=new NativeLong(90);
                                PU_FACE_FEATURE_EXTRACT_S entrypuFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                                entrypuFaceFeatureExtractS.stFacelib=entryTiquFace;
                                entrypuFaceFeatureExtractS.ulChannelId=new NativeLong(101);
                                boolean exitgetTeZheng;
                                if (Platform.isWindows()){
                                    exitgetTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,entrypuFaceFeatureExtractS);
                                    System.out.println("提取入口口人脸特征值返回码------");
                                    HuaWeiSdkApi.printReturnMsg();
                                }else {
                                    exitgetTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,entrypuFaceFeatureExtractS);
                                    System.out.println("提取入口口人脸特征值返回码------");
                                    HuaWeiSdkApi.printReturnMsg();
                                }
                                if (exitgetTeZheng){
                                    log.debug("提取入口人脸特征值成功------");
                                }else {
                                    log.debug("提取入口人脸特征值失败------");
                                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"提取入口人脸特征值失败,图片不规范",null);
                                }
                            }else {
                                log.debug("有人脸库没有人脸信息时，添加入口人脸失败------");
                                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"有人脸库没有人脸信息时，添加入口人脸失败",null);
                            }
                        }
                    }else {//有人脸库也有人脸信息
                        ArrayList<FaceInfo> faceInfos=new ArrayList<>();
                        for (int i=0; i<faceRecordArry.size();i++){
                            FaceInfo faceInfo=new FaceInfo();
                            JSONObject faceInfojson=faceRecordArry.getJSONObject(i);
                            faceInfo.setFaceid(faceInfojson.getStr("ID"));
                            String userName=faceInfojson.getStr("Name");
                            System.out.println("用户名："+userName);
                            faceInfo.setUserName(userName);
                            String gender=faceInfojson.getStr("Gender");
                            faceInfo.setGender(Integer.parseInt(gender));
                            String birthday=faceInfojson.getStr("Birthday");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date birthday1 = sdf.parse(birthday);
                            faceInfo.setBirthday(birthday1);
                            String province=faceInfojson.getStr("Province");
                            faceInfo.setProvince(province);
                            String city = faceInfojson.getStr("City");
                            faceInfo.setCity(city);
                            String cardType = faceInfojson.getStr("CardType");
                            faceInfo.setCardType(Integer.parseInt(cardType));
                            String cardID = faceInfojson.getStr("CardID");
                            faceInfo.setCardId(cardID);
                            faceInfos.add(faceInfo);
                        }
                        System.out.println("从摄像头查询到的人脸集合对象："+faceInfos);
                        //若传入集合列表为空，则需要删除所有人脸
                        if(faceInfoList.isEmpty()){
                            for(FaceInfo houtaiFaceInfo:faceInfos){
                                /**
                                 * 删除出口摄像头人脸
                                 */
                                PU_FACE_INFO_DELETE_S exitpuFaceInfoDeleteS=new PU_FACE_INFO_DELETE_S();
                                int[] exituFaceID = new int[100];
                                exituFaceID[0]=Integer.parseInt(houtaiFaceInfo.getFaceid());
                                exitpuFaceInfoDeleteS.uFaceID= exituFaceID;
                                exitpuFaceInfoDeleteS.uFaceNum=1;
                                exitpuFaceInfoDeleteS.ulChannelId=new NativeLong(101);
                                PU_FACE_LIB_S exitfacelib = new PU_FACE_LIB_S();
                                exitfacelib.ulFaceLibID=new NativeLong(ulFaceLibID);
                                if(Platform.isWindows()){
                                    exitfacelib.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                                }else {
                                    exitfacelib.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                                }
                                exitfacelib.enLibType=enLibType;
                                exitfacelib.uiThreshold=new NativeLong(uiThreshold);
                                exitpuFaceInfoDeleteS.stFacelib=exitfacelib;
                                boolean exitdel;
                                if(Platform.isWindows()){
                                    exitdel =HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitIdentifyId, exitpuFaceInfoDeleteS);
                                }else {
                                    exitdel =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitIdentifyId, exitpuFaceInfoDeleteS);

                                }
                                HuaWeiSdkApi.printReturnMsg();
                                /**
                                 * 删除入口口摄像头人脸
                                 */
                                PU_FACE_INFO_DELETE_S entrypuFaceInfoDeleteS=new PU_FACE_INFO_DELETE_S();
                                int[] entryuFaceID = new int[100];
                                entryuFaceID[0]=Integer.parseInt(houtaiFaceInfo.getFaceid());
                                entrypuFaceInfoDeleteS.uFaceID= entryuFaceID;
                                entrypuFaceInfoDeleteS.uFaceNum=1;
                                entrypuFaceInfoDeleteS.ulChannelId=new NativeLong(101);
                                PU_FACE_LIB_S entryfacelib = new PU_FACE_LIB_S();
                                entryfacelib.ulFaceLibID=new NativeLong(ulFaceLibID);
                                if(Platform.isWindows()){
                                    entryfacelib.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                                }else {
                                    entryfacelib.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                                }
                                entryfacelib.enLibType=enLibType;
                                entryfacelib.uiThreshold=new NativeLong(uiThreshold);
                                entrypuFaceInfoDeleteS.stFacelib=entryfacelib;
                                boolean entrydel;
                                if(Platform.isWindows()){
                                    entrydel =HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryIdentifyId, entrypuFaceInfoDeleteS);
                                }else {
                                    entrydel =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryIdentifyId, entrypuFaceInfoDeleteS);

                                }
                                HuaWeiSdkApi.printReturnMsg();
                                if(exitdel && entrydel){
                                   log.debug("人脸门禁摄像头全部取消授权成功");
                                }else {
                                    System.out.println("删除人脸信息失败");
                                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"人脸门禁摄像头全部取消授权失败",null);
                                }
                            }
                            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"人脸门禁摄像头全部取消授权成功",null);
                        }
                        //先删除摄像头数据库在所传入列表不在的人脸信息
                        for(FaceInfo houtaiFaceInfo:faceInfos){
                            boolean needDel=true;
                            for(FaceInfo qiantaiFaceInfo:faceInfoList){
                                if(qiantaiFaceInfo.getUserName() !=null && qiantaiFaceInfo.getUserName().equals(houtaiFaceInfo.getUserName())){
                                    needDel=false;
                                    break;
                                }
                            }
                            if(needDel){
                                /**
                                 *  删除入口摄像头中的人脸信息，循环删除
                                 */
                                PU_FACE_INFO_DELETE_S entrypuFaceInfoDeleteS=new PU_FACE_INFO_DELETE_S();
                                int[] entryuFaceID = new int[100];
                                entryuFaceID[0]=Integer.parseInt(houtaiFaceInfo.getFaceid());
                                entrypuFaceInfoDeleteS.uFaceID= entryuFaceID;
                                entrypuFaceInfoDeleteS.uFaceNum=1;
                                entrypuFaceInfoDeleteS.ulChannelId=new NativeLong(101);
                                PU_FACE_LIB_S entryfacelib = new PU_FACE_LIB_S();
                                entryfacelib.ulFaceLibID=new NativeLong(ulFaceLibID);
                                if(Platform.isWindows()){
                                    entryfacelib.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                                }else {
                                    entryfacelib.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                                }
                                entryfacelib.enLibType=enLibType;
                                entryfacelib.uiThreshold=new NativeLong(uiThreshold);
                                entrypuFaceInfoDeleteS.stFacelib=entryfacelib;
                                boolean entrydel;
                                if(Platform.isWindows()){
                                    entrydel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryIdentifyId, entrypuFaceInfoDeleteS);
                                }else {
                                    entrydel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryIdentifyId, entrypuFaceInfoDeleteS);
                                }
                                log.debug("删除入口摄像头中的人脸信息返回码:");
                                HuaWeiSdkApi.printReturnMsg();
                                if (entrydel){
                                    log.debug("删除入口摄像头中的人脸信息成功:");
                                }else {
                                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"删除入口摄像头中的人脸信息失败",null);
                                }
                                /**
                                 *  删除出口摄像头中的人脸信息，循环删除
                                 */
                                PU_FACE_INFO_DELETE_S exitpuFaceInfoDeleteS=new PU_FACE_INFO_DELETE_S();
                               // int[] exituFaceID = new int[100];
                                //exituFaceID[0]=Integer.parseInt(houtaiFaceInfo.getFaceid());
                                exitpuFaceInfoDeleteS.uFaceID= entryuFaceID;
                                exitpuFaceInfoDeleteS.uFaceNum=1;
                                exitpuFaceInfoDeleteS.ulChannelId=new NativeLong(101);
                                PU_FACE_LIB_S exitfacelib = new PU_FACE_LIB_S();
                                exitfacelib.ulFaceLibID=new NativeLong(ulFaceLibID);
                                if(Platform.isWindows()){
                                    exitfacelib.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                                }else {
                                    exitfacelib.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                                }
                                exitfacelib.enLibType=enLibType;
                                exitfacelib.uiThreshold=new NativeLong(uiThreshold);
                                exitpuFaceInfoDeleteS.stFacelib=exitfacelib;
                                boolean exitdell;
                                if(Platform.isWindows()){
                                    exitdell = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitIdentifyId, exitpuFaceInfoDeleteS);
                                }else {
                                    exitdell = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitIdentifyId, exitpuFaceInfoDeleteS);
                                }
                                log.debug("删除出口摄像头中的人脸信息返回码:");
                                HuaWeiSdkApi.printReturnMsg();
                                if (exitdell){
                                    log.debug("删除出口摄像头中的人脸信息成功:");
                                }else {
                                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"删除入口摄像头中的人脸信息失败",null);
                                }
                                if(entrydel && exitdell){
                                    System.out.println("删除人脸信息成功");
                                }else {
                                    System.out.println("删除人脸信息失败");
                                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"人脸门禁摄像头授权失败",null);
                                }
                            }
                        }
                        //再添加传入列表在数据库中找不到的人脸信息
                        System.out.println("前台所传的人脸信息集合："+faceInfoList);
                        for(FaceInfo qiantaiFaceInfo:faceInfoList){
                            boolean needAdd=true;
                            for(FaceInfo houtaiFaceInfo:faceInfos){
                                if(qiantaiFaceInfo.getUserName() !=null && qiantaiFaceInfo.getUserName().equals(houtaiFaceInfo.getUserName())){
                                    needAdd=false;
                                    break;
                                }
                            }
                            if(needAdd){//添加人脸信息
                                /**
                                 * 添加入口人脸对象
                                 */
                                PU_FACE_LIB_S enrtystFacelib1=new PU_FACE_LIB_S();
                                enrtystFacelib1.enLibType=enLibType;
                                enrtystFacelib1.isControl=true;
                                enrtystFacelib1.ulFaceLibID=new NativeLong(ulFaceLibID);
                                if (Platform.isWindows()){
                                    enrtystFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                                }else {
                                    enrtystFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                                }
                                enrtystFacelib1.uiThreshold=new NativeLong(uiThreshold);
                                //设置添加人脸信息的对象
                                PU_FACE_INFO_ADD_S  entrypuFaceInfoAdd = new PU_FACE_INFO_ADD_S();
                                entrypuFaceInfoAdd.stFacelib=enrtystFacelib1;
                                entrypuFaceInfoAdd.ulChannelId=new NativeLong(101);
                                //设置人脸信息
                                PU_FACE_RECORD entryaddfaceInfo=new PU_FACE_RECORD();
                                entryaddfaceInfo.enCardType=qiantaiFaceInfo.getCardType();
                                entryaddfaceInfo.enGender=qiantaiFaceInfo.getGender();
                                SimpleDateFormat enrtysdf = new SimpleDateFormat("yyyy-MM-dd");
                                String entrybirthday = enrtysdf.format(qiantaiFaceInfo.getBirthday());
                                entryaddfaceInfo.szBirthday=Arrays.copyOf(entrybirthday.getBytes(),32);
                                entryaddfaceInfo.szCardID=Arrays.copyOf(qiantaiFaceInfo.getCardId().getBytes(),32);
                                if(Platform.isWindows()){
                                    entryaddfaceInfo.szCity=Arrays.copyOf(qiantaiFaceInfo.getCity().getBytes("gbk"),48);
                                    entryaddfaceInfo.szName=Arrays.copyOf(qiantaiFaceInfo.getUserName().getBytes("gbk"),64);
                                    entryaddfaceInfo.szProvince=Arrays.copyOf(qiantaiFaceInfo.getProvince().getBytes("gbk"),32);
                                }else {
                                    entryaddfaceInfo.szCity=Arrays.copyOf(qiantaiFaceInfo.getCity().getBytes("utf8"),48);
                                    entryaddfaceInfo.szName=Arrays.copyOf(qiantaiFaceInfo.getUserName().getBytes("utf8"),64);
                                    entryaddfaceInfo.szProvince=Arrays.copyOf(qiantaiFaceInfo.getProvince().getBytes("utf8"),32);
                                }
                                String entryabsolutePath = new String(new File(".").getCanonicalPath() + qiantaiFaceInfo.getFaceImage());
                                entryaddfaceInfo.szPicPath=Arrays.copyOf(entryabsolutePath.getBytes(),128);
                                entrypuFaceInfoAdd.stRecord=entryaddfaceInfo;
                                String entryfilename = qiantaiFaceInfo.getFaceImage().substring(qiantaiFaceInfo.getFaceImage().lastIndexOf("/")+1);
                                boolean entryaddOneFaceV2;
                                if(Platform.isWindows()){
                                    entryaddOneFaceV2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, entrypuFaceInfoAdd, entryfilename);
                                }else {
                                    entryaddOneFaceV2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, entrypuFaceInfoAdd, entryfilename);
                                }
                                HuaWeiSdkApi.printReturnMsg();
                                /**
                                 * 添加完提取入口人脸特征值
                                 */
                                if (entryaddOneFaceV2){
                                    PU_FACE_LIB_S entryTiquFace=new PU_FACE_LIB_S();
                                    entryTiquFace.enLibType=2;
                                    entryTiquFace.isControl=true;
                                    entryTiquFace.ulFaceLibID=new NativeLong(1);
                                    if (Platform.isWindows()){
                                        entryTiquFace.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);
                                    }else {
                                        entryTiquFace.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);
                                    }
                                    entryTiquFace.uiThreshold=new NativeLong(90);
                                    PU_FACE_FEATURE_EXTRACT_S entrypuFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                                    entrypuFaceFeatureExtractS.stFacelib=entryTiquFace;
                                    entrypuFaceFeatureExtractS.ulChannelId=new NativeLong(101);
                                    boolean exitgetTeZheng;
                                    if (Platform.isWindows()){
                                        exitgetTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,entrypuFaceFeatureExtractS);
                                        System.out.println("提取入口口人脸特征值返回码------");
                                        HuaWeiSdkApi.printReturnMsg();
                                    }else {
                                        exitgetTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,entrypuFaceFeatureExtractS);
                                        System.out.println("提取入口人脸特征值返回码------");
                                        HuaWeiSdkApi.printReturnMsg();
                                    }
                                    if (exitgetTeZheng){
                                        log.debug("提取入口人脸特征值成功------");
                                    }else {
                                        log.debug("提取入口人脸特征值失败------");
                                        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"提取出口人脸特征值失败,图片不规范",null);
                                    }
                                }else {
                                    log.debug("有人脸库没有人脸信息时，添加出口人脸失败------");
                                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"有人脸库有人脸信息时，添加入口人脸失败",null);
                                }

                                /**
                                 * 添加出口人脸对象
                                 */
                                PU_FACE_LIB_S exitstFacelib1=new PU_FACE_LIB_S();
                                exitstFacelib1.enLibType=enLibType;
                                exitstFacelib1.isControl=true;
                                exitstFacelib1.ulFaceLibID=new NativeLong(ulFaceLibID);
                                if (Platform.isWindows()){
                                    exitstFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                                }else {
                                    exitstFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                                }
                                exitstFacelib1.uiThreshold=new NativeLong(uiThreshold);
                                //设置添加人脸信息的对象
                                PU_FACE_INFO_ADD_S  exitpuFaceInfoAdd = new PU_FACE_INFO_ADD_S();
                                exitpuFaceInfoAdd.stFacelib=enrtystFacelib1;
                                exitpuFaceInfoAdd.ulChannelId=new NativeLong(101);
                                //设置人脸信息
                                PU_FACE_RECORD exitaddfaceInfo=new PU_FACE_RECORD();
                                exitaddfaceInfo.enCardType=qiantaiFaceInfo.getCardType();
                                exitaddfaceInfo.enGender=qiantaiFaceInfo.getGender();
                                SimpleDateFormat exitysdf = new SimpleDateFormat("yyyy-MM-dd");
                                String exitbirthday = exitysdf.format(qiantaiFaceInfo.getBirthday());
                                exitaddfaceInfo.szBirthday=Arrays.copyOf(exitbirthday.getBytes(),32);
                                exitaddfaceInfo.szCardID=Arrays.copyOf(qiantaiFaceInfo.getCardId().getBytes(),32);
                                if(Platform.isWindows()){
                                    exitaddfaceInfo.szCity=Arrays.copyOf(qiantaiFaceInfo.getCity().getBytes("gbk"),48);
                                    exitaddfaceInfo.szName=Arrays.copyOf(qiantaiFaceInfo.getUserName().getBytes("gbk"),64);
                                    exitaddfaceInfo.szProvince=Arrays.copyOf(qiantaiFaceInfo.getProvince().getBytes("gbk"),32);
                                }else {
                                    exitaddfaceInfo.szCity=Arrays.copyOf(qiantaiFaceInfo.getCity().getBytes("utf8"),48);
                                    exitaddfaceInfo.szName=Arrays.copyOf(qiantaiFaceInfo.getUserName().getBytes("utf8"),64);
                                    exitaddfaceInfo.szProvince=Arrays.copyOf(qiantaiFaceInfo.getProvince().getBytes("utf8"),32);
                                }
                                String exitabsolutePath = new String(new File(".").getCanonicalPath() + qiantaiFaceInfo.getFaceImage());
                                exitaddfaceInfo.szPicPath=Arrays.copyOf(exitabsolutePath.getBytes(),128);
                                exitpuFaceInfoAdd.stRecord=exitaddfaceInfo;
                                String exitfilename = qiantaiFaceInfo.getFaceImage().substring(qiantaiFaceInfo.getFaceImage().lastIndexOf("/")+1);
                                boolean exitaddOneFaceV2;
                                if(Platform.isWindows()){
                                    exitaddOneFaceV2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, exitpuFaceInfoAdd, exitfilename);
                                }else {
                                    exitaddOneFaceV2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, exitpuFaceInfoAdd, exitfilename);
                                }
                                HuaWeiSdkApi.printReturnMsg();
                                /**
                                 * 添加完提取出口人脸特征值
                                 */
                                if (exitaddOneFaceV2){
                                    PU_FACE_LIB_S exitTiquFace=new PU_FACE_LIB_S();
                                    exitTiquFace.enLibType=2;
                                    exitTiquFace.isControl=true;
                                    exitTiquFace.ulFaceLibID=new NativeLong(1);
                                    if (Platform.isWindows()){
                                        exitTiquFace.szLibName=Arrays.copyOf("facelib".getBytes("gbk"),65);
                                    }else {
                                        exitTiquFace.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);
                                    }
                                    exitTiquFace.uiThreshold=new NativeLong(90);
                                    PU_FACE_FEATURE_EXTRACT_S exitpuFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                                    exitpuFaceFeatureExtractS.stFacelib=exitTiquFace;
                                    exitpuFaceFeatureExtractS.ulChannelId=new NativeLong(101);
                                    boolean exitgetTeZheng;
                                    if (Platform.isWindows()){
                                        exitgetTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,exitpuFaceFeatureExtractS);
                                        System.out.println("提取出口人脸特征值返回码------");
                                        HuaWeiSdkApi.printReturnMsg();
                                    }else {
                                        exitgetTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,exitpuFaceFeatureExtractS);
                                        System.out.println("提取出口人脸特征值返回码------");
                                        HuaWeiSdkApi.printReturnMsg();
                                    }
                                    if (exitgetTeZheng){
                                        log.debug("提取出口人脸特征值成功------");
                                    }else {
                                        log.debug("提取出口人脸特征值失败------");
                                        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"提取出口人脸特征值失败,图片不规范",null);
                                    }
                                }else {
                                    log.debug("有人脸库没有人脸信息时，添加出口人脸失败------");
                                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"有人脸库有人脸信息时，添加出口人脸失败",null);
                                }
                            }
                        }
                    }
                }else {
                    System.out.println("查询第一个人脸库人脸信息失败");
                    return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询第一个人脸库人脸信息失败",null);
                }
            }
        }else{
            System.out.println("查询摄像头人脸库失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询摄像头人脸库失败",null);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //提取特征值之后接着修改人脸库，使其成为布控状态
        //接着布控
        /**
         * 先布控入口人脸库
         */
        PU_FACE_LIB_SET_S entrypuFaceLibSetS2 =new PU_FACE_LIB_SET_S();
        entrypuFaceLibSetS2.enOptType=2;//修改人脸库
        PU_FACE_LIB_S  entrystFacelib2=new PU_FACE_LIB_S();
        if(Platform.isWindows()){
            entrystFacelib2.szLibName=Arrays.copyOf("facelib".getBytes("GBK"),65);//windows名单库的名称
        }else {
            entrystFacelib2.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);//名单库的名称
        }
        entrystFacelib2.uiThreshold=new NativeLong(90);//布控的阀值
        entrystFacelib2.enLibType=2;//人脸库类型2为白名单
        entrystFacelib2.isControl=true;//修改为布控
        entrystFacelib2.ulFaceLibID=new NativeLong(1);
        entrypuFaceLibSetS2.stFacelib=entrystFacelib2;
        entrypuFaceLibSetS2.ulChannelId=new NativeLong(101);
        boolean entrybukong;
        if(Platform.isWindows()){
            entrybukong = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, entrypuFaceLibSetS2);
            System.out.println("布控入口的返回码");
            HuaWeiSdkApi.printReturnMsg();
        }else {
            entrybukong = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, entrypuFaceLibSetS2);
            HuaWeiSdkApi.printReturnMsg();
        }
        /**
         * 再布控出口人脸库
         */
        PU_FACE_LIB_SET_S exitpuFaceLibSetS2 =new PU_FACE_LIB_SET_S();
        exitpuFaceLibSetS2.enOptType=2;//修改人脸库
        PU_FACE_LIB_S  exitstFacelib2=new PU_FACE_LIB_S();
        if(Platform.isWindows()){
            exitstFacelib2.szLibName=Arrays.copyOf("facelib".getBytes("GBK"),65);//windows名单库的名称
        }else {
            exitstFacelib2.szLibName=Arrays.copyOf("facelib".getBytes("utf8"),65);//名单库的名称
        }
        exitstFacelib2.uiThreshold=new NativeLong(90);//布控的阀值
        exitstFacelib2.enLibType=2;//人脸库类型2为白名单
        exitstFacelib2.isControl=true;//修改为布控
        exitstFacelib2.ulFaceLibID=new NativeLong(1);
        exitpuFaceLibSetS2.stFacelib=exitstFacelib2;
        exitpuFaceLibSetS2.ulChannelId=new NativeLong(101);
        boolean exitbukong;
        if(Platform.isWindows()){
            exitbukong = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, exitpuFaceLibSetS2);
            System.out.println("布控出口的返回码");
            HuaWeiSdkApi.printReturnMsg();
        }else {
            exitbukong = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, exitpuFaceLibSetS2);
            HuaWeiSdkApi.printReturnMsg();
        }
        if (exitbukong && entrybukong){
            System.out.println("修改出口口人脸库布控成功");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"人脸授权成功",null);
        }else {
            log.error("修改出口人脸库布控失败");
            int i=faceInfoAccCtrlService.deleteFaceAccCtrlByAccCtlId(accessControlId);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"人脸授权失败",null);
        }
    }

    @ApiOperation(value = "根据门禁id查询已经授权的人脸信息列表",notes = "查询已经和门禁关联的人脸信息列表")
    @GetMapping(value = "/selectFaceInfoByAccCtrlId")
    public RestfulEntityBySummit<List<String>> selectFaceInfoByAccCtrlId(@ApiParam(value = "门禁id")@RequestParam(value = "accCtrlId",required = false)String accCtrlId){
        if(accCtrlId==null){
            log.error("门禁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"门禁id为空",null);
        }
        List<String> ids=new ArrayList<>();
        try {
            List<FaceInfoAccCtrl> faceInfoAccCtrls= faceInfoAccCtrlService.selectFaceInfoAccCtrlByActrlId(accCtrlId);
            if(faceInfoAccCtrls!=null){
                for(FaceInfoAccCtrl faceInfoAccCtrl:faceInfoAccCtrls){
                    ids.add(faceInfoAccCtrl.getFaceid());
                }
            }
        } catch (Exception e) {
           log.error("查询人脸信息列表失败");
           return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询人脸信息列表失败",ids);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询人脸信息列表成功",ids);
    }


    public String readFile(String path){
        BufferedReader  reader=null;
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

