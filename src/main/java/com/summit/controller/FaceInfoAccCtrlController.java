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
        AccessControlInfo accessControlInfo = accessControlService.selectAccCtrlById(accessControlId);
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
        NativeLong entryIdentifyId = entrydeviceInfo.getUlIdentifyId();
        //NativeLong entryIdentifyId=new NativeLong(2);
        System.out.println(entryIdentifyId+"：entryIdentifyId");

        //把人脸信息再加入到出口摄像头中
        DeviceInfo exitdeviceInfo = HuaWeiSdkApi.DEVICE_MAP.get(exitCameraIp);
      //  NativeLong exitIdentifyId = exitdeviceInfo.getUlIdentifyId();
        NativeLong exitIdentifyId=new NativeLong(2);
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
        if(getFaceLib || exitgetFaceLib){
            System.out.println("查询人脸库成功");
            String getfacelibpath1 = new String(new File(".").getCanonicalPath() + File.separator +"facelib"+File.separator+"faceLib.json");
            String json = readFile(getfacelibpath1);
            JSONObject object=new JSONObject(json);
            JSONArray faceListsArry = object.getJSONArray("FaceListsArry");
            System.out.println("人脸库集合："+faceListsArry);
            //2 判断有没有人脸库，没有人脸库则直接添加人脸库，接着添加人脸信息
            if(faceListsArry==null || faceListsArry.size()==0){ //说明没有人脸库则需要添加人脸库,接着添加人脸信息
                PU_FACE_LIB_SET_S puFaceLibSetS =new PU_FACE_LIB_SET_S();
                puFaceLibSetS.enOptType=1;//新增人脸库
                PU_FACE_LIB_S  stFacelib=new PU_FACE_LIB_S();
                if(Platform.isWindows()){
                    stFacelib.szLibName=Arrays.copyOf("人脸库".getBytes("gbk"),65);//windows名单库的名称
                }else {
                    stFacelib.szLibName=Arrays.copyOf("人脸库".getBytes("utf8"),65);//名单库的名称
                }
                stFacelib.uiThreshold=new NativeLong(90);//布控的阀值
                stFacelib.enLibType=2;//人脸库类型
                stFacelib.isControl=true;
                puFaceLibSetS.stFacelib=stFacelib;
                puFaceLibSetS.ulChannelId=new NativeLong(101);
                boolean addFaceLib;
                boolean exitaddFaceLib2;
                if(Platform.isWindows()){
                     addFaceLib = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, puFaceLibSetS);
                     exitaddFaceLib2 = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, puFaceLibSetS);
                }else {
                     addFaceLib = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, puFaceLibSetS);
                     exitaddFaceLib2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, puFaceLibSetS);
                }
                HuaWeiSdkApi.printReturnMsg();
                if (addFaceLib || exitaddFaceLib2 ){//人脸库添加成功，接着添加人脸信息，循环添加
                    System.out.println("人脸库添加成功");
                    //新建完再次人脸库查询人脸库取人脸库信息,并且给人脸库信息赋值
                    szLibName="人脸库";
                    enLibType = 2;
                    ulFaceLibID = 1;
                    uiThreshold=90;
                    //添加人脸信息，循环添加
                    for(FaceInfo faceInfo:faceInfoList){
                        //设置人脸库对象
                        PU_FACE_LIB_S stFacelib1=new PU_FACE_LIB_S();
                        stFacelib1.enLibType=enLibType;
                        stFacelib1.isControl=true;
                        stFacelib1.ulFaceLibID=new NativeLong(ulFaceLibID);
                        stFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes(),65);
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
                        addface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes(),48);
                        if(Platform.isWindows()){
                            addface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("gbk"),64);
                        }else {
                            addface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("utf8"),64);
                        }
                        String absolutePath = new String(new File(".").getCanonicalPath() + faceInfo.getFaceImage());
                        System.out.println(absolutePath+"图片路径");
                        addface.szPicPath=Arrays.copyOf(absolutePath.getBytes(),128);
                        addface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes(),32);
                        puFaceInfoAdd.stRecord=addface;
                        String filename = faceInfo.getFaceImage().substring(faceInfo.getFaceImage().lastIndexOf("/")+1);
                        System.out.println(filename);
                        boolean addface2;
                        boolean exitaddface2;
                        if(Platform.isWindows()){
                             addface2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, puFaceInfoAdd, filename);
                             exitaddface2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, puFaceInfoAdd, filename);
                        }else {
                             addface2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, puFaceInfoAdd, filename);
                             exitaddface2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, puFaceInfoAdd, filename);
                        }
                        HuaWeiSdkApi.printReturnMsg();
                        if(addface2 || exitaddface2){
                            System.out.println("添加人脸信息成功");
                            //添加完人脸信息之后需要提取特征值
                            PU_FACE_FEATURE_EXTRACT_S puFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                            puFaceFeatureExtractS.stFacelib=stFacelib1;
                            puFaceFeatureExtractS.ulChannelId=new NativeLong(101);
                            //puFaceFeatureExtractS.taskID=new NativeLong(1);
                            boolean getTeZheng;
                            boolean getTeZhengexit;
                            if(Platform.isWindows()){
                                 getTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,puFaceFeatureExtractS);
                                 getTeZhengexit = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,puFaceFeatureExtractS);
                            }else {
                                 getTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,puFaceFeatureExtractS);
                                 getTeZhengexit = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,puFaceFeatureExtractS);
                            }
                            //提取特征值之后接着修改人脸库，使其成为布控状态
                            if(getTeZheng || getTeZhengexit){
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
                                boolean bukongExit;
                                if(Platform.isWindows()){
                                     bukong = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, puFaceLibSetS2);
                                     bukongExit=HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, puFaceLibSetS2);
                                }else {
                                     bukong = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, puFaceLibSetS2);
                                     bukongExit=HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, puFaceLibSetS2);
                                }
                                if (bukong || bukongExit){
                                    System.out.println("修改布控成功");
                                }
                            }
                        }else {
                            System.out.println("添加人脸信息失败");
                        }
                    }
                }else {
                    System.out.println("人脸库添加失败");
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
                //查询第一个人脸库的人脸信息
                String faceinfoPath=new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.FaceInfo)
                        .toString();
                File face=new File(faceinfoPath);
                if(!face.exists()){
                    face.mkdirs();
                }
                PU_FACE_INFO_FIND_S faceInfoFindS = new PU_FACE_INFO_FIND_S();
                faceInfoFindS.ulChannelId=new NativeLong(101);
                String faceInfoPath=faceinfoPath+"/faceInfo.json";
                faceInfoFindS.szFindResultPath= Arrays.copyOf(faceInfoPath.getBytes(),129);
                PU_FACE_LIB_S facelib2 = new PU_FACE_LIB_S();
                facelib2.ulFaceLibID=new NativeLong(ulFaceLibID);
                if(Platform.isWindows()){
                    facelib2.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                }else {
                    facelib2.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                }
                facelib2.enLibType=enLibType;
                facelib2.uiThreshold=new NativeLong(uiThreshold);
                FACE_FIND_CONDITION faceFindCondition=new FACE_FIND_CONDITION();
                faceFindCondition.enFeatureStatus=1;
                faceFindCondition.szName=ByteBuffer.allocate(64).put("".getBytes()).array();
                faceFindCondition.szProvince=ByteBuffer.allocate(32).put("".getBytes()).array();
                faceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                faceFindCondition.szCardID=ByteBuffer.allocate(32).put("".getBytes()).array();
                faceFindCondition.szCity=ByteBuffer.allocate(48).put("".getBytes()).array();
                faceFindCondition.enGender=-1;
                faceFindCondition.enCardType=-1;
                faceInfoFindS.stCondition=faceFindCondition;
                faceInfoFindS.stFacelib=facelib2;
                faceInfoFindS.uStartIndex=0;
                //faceInfoFindS.uFindNum=2;
                boolean getFace;
                boolean exitgetFace;
                if(Platform.isWindows()){
                     getFace =HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryIdentifyId, faceInfoFindS);
                     exitgetFace =HWPuSDKLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitIdentifyId, faceInfoFindS);
                }else {
                     getFace =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(entryIdentifyId, faceInfoFindS);
                     exitgetFace =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FindFaceInfo(exitIdentifyId, faceInfoFindS);
                }
                if (getFace || exitgetFace){
                    System.out.println("查询人脸信息成功");
                    //查询到人脸信息json数据
                    String getfaceInfoPath = new String(new File(".").getCanonicalPath() + File.separator+"faceInfo"+File.separator+"faceInfo.json");
                    String facejson = readFile(getfaceInfoPath);
                    JSONObject objectface=new JSONObject(facejson);
                    JSONArray faceRecordArry = objectface.getJSONArray("FaceRecordArry");
                    System.out.println("人脸信息集合："+faceRecordArry);
                    if(faceRecordArry==null || faceRecordArry.size()==0){//有人脸库没有人脸信息,这时候直接添加所传的人脸信息
                        for(FaceInfo faceInfo:faceInfoList){
                            //设置人脸库对象
                            PU_FACE_LIB_S stFacelib1=new PU_FACE_LIB_S();
                            stFacelib1.enLibType=enLibType;
                            stFacelib1.isControl=true;
                            stFacelib1.ulFaceLibID=new NativeLong(ulFaceLibID);
                            stFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes(),65);
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
                            addface.szCity=Arrays.copyOf(faceInfo.getCity().getBytes(),48);
                            if(Platform.isWindows()){
                                addface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("gbk"),64);
                            }else {
                                addface.szName=Arrays.copyOf(faceInfo.getUserName().getBytes("utf8"),64);
                            }
                            String absolutePath = new String(new File(".").getCanonicalPath() + faceInfo.getFaceImage());
                            System.out.println(absolutePath+"图片路径");
                            addface.szPicPath=Arrays.copyOf(absolutePath.getBytes(),128);
                            addface.szProvince=Arrays.copyOf(faceInfo.getProvince().getBytes(),32);
                            puFaceInfoAdd.stRecord=addface;
                            String filename = faceInfo.getFaceImage().substring(faceInfo.getFaceImage().lastIndexOf("/")+1);
                            System.out.println(filename);
                            boolean addfaceinfo;
                            boolean exitaddfaceinfo;
                            if(Platform.isWindows()){
                                 addfaceinfo = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, puFaceInfoAdd, filename);
                                 exitaddfaceinfo = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, puFaceInfoAdd, filename);
                            }else {
                                 addfaceinfo = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, puFaceInfoAdd, filename);
                                 exitaddfaceinfo = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, puFaceInfoAdd, filename);
                            }
                            HuaWeiSdkApi.printReturnMsg();
                            if(addfaceinfo || exitaddfaceinfo){
                                System.out.println("添加人脸信息成功");
                                //提取特征值
                                PU_FACE_FEATURE_EXTRACT_S puFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                                puFaceFeatureExtractS.stFacelib=stFacelib1;
                                puFaceFeatureExtractS.ulChannelId=new NativeLong(101);
                               // puFaceFeatureExtractS.taskID=new NativeLong(1);
                                boolean getTeZheng;
                                boolean getTeZhengexit;
                                if(Platform.isWindows()){
                                    getTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,puFaceFeatureExtractS);
                                    getTeZhengexit = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,puFaceFeatureExtractS);
                                }else {
                                    getTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,puFaceFeatureExtractS);
                                    getTeZhengexit = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,puFaceFeatureExtractS);
                                }
                                if(getTeZheng || getTeZhengexit){
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
                                    boolean bukongExit;
                                    if(Platform.isWindows()){
                                        bukong = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, puFaceLibSetS2);
                                        bukongExit=HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, puFaceLibSetS2);
                                    }else {
                                        bukong = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, puFaceLibSetS2);
                                        bukongExit=HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, puFaceLibSetS2);
                                    }
                                    if (bukong || bukongExit){
                                        System.out.println("修改布控成功");
                                    }
                                }
                            }else {
                                System.out.println("添加人脸信息失败");
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
                                PU_FACE_INFO_DELETE_S puFaceInfoDeleteS=new PU_FACE_INFO_DELETE_S();
                                int[] uFaceID = new int[100];
                                uFaceID[0]=Integer.parseInt(houtaiFaceInfo.getFaceid());
                                puFaceInfoDeleteS.uFaceID= uFaceID;
                                puFaceInfoDeleteS.uFaceNum=1;
                                puFaceInfoDeleteS.ulChannelId=new NativeLong(101);
                                PU_FACE_LIB_S facelib = new PU_FACE_LIB_S();
                                facelib.ulFaceLibID=new NativeLong(ulFaceLibID);
                                if(Platform.isWindows()){
                                    facelib.szLibName=Arrays.copyOf(szLibName.getBytes("gbk"),65);
                                }else {
                                    facelib.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                                }
                                facelib.enLibType=enLibType;
                                facelib.uiThreshold=new NativeLong(uiThreshold);
                                puFaceInfoDeleteS.stFacelib=facelib;
                                boolean del;
                                boolean exitdel;
                                if(Platform.isWindows()){
                                     del =HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryIdentifyId, puFaceInfoDeleteS);
                                     exitdel =HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitIdentifyId, puFaceInfoDeleteS);
                                }else {
                                     del =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryIdentifyId, puFaceInfoDeleteS);
                                     exitdel =HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitIdentifyId, puFaceInfoDeleteS);
                                }
                                HuaWeiSdkApi.printReturnMsg();
                                if(del || exitdel){
                                    System.out.println("删除人脸信息成功");

                                }else {
                                    System.out.println("删除人脸信息失败");
                                }
                            }
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
                                //删除摄像头中的人脸信息，循环删除
                                PU_FACE_INFO_DELETE_S puFaceInfoDeleteS=new PU_FACE_INFO_DELETE_S();
                                int[] uFaceID = new int[100];
                                uFaceID[0]=Integer.parseInt(houtaiFaceInfo.getFaceid());
                                puFaceInfoDeleteS.uFaceID= uFaceID;
                                puFaceInfoDeleteS.uFaceNum=1;
                                puFaceInfoDeleteS.ulChannelId=new NativeLong(101);
                                PU_FACE_LIB_S facelib = new PU_FACE_LIB_S();
                                facelib.ulFaceLibID=new NativeLong(ulFaceLibID);
                                facelib.szLibName=Arrays.copyOf(szLibName.getBytes("utf8"),65);
                                facelib.enLibType=enLibType;
                                facelib.uiThreshold=new NativeLong(uiThreshold);
                                puFaceInfoDeleteS.stFacelib=facelib;
                                boolean del;
                                boolean exitdel;
                                if(Platform.isWindows()){
                                     del = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryIdentifyId, puFaceInfoDeleteS);
                                     exitdel = HWPuSDKLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitIdentifyId, puFaceInfoDeleteS);
                                }else {
                                     del = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(entryIdentifyId, puFaceInfoDeleteS);
                                     exitdel = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_DelFaceInfo(exitIdentifyId, puFaceInfoDeleteS);
                                }
                                HuaWeiSdkApi.printReturnMsg();
                                if(del || exitdel){
                                    System.out.println("删除人脸信息成功");
                                }else {
                                    System.out.println("删除人脸信息失败");
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
                                //添加人脸信息
                                //设置人脸库对象
                                PU_FACE_LIB_S stFacelib1=new PU_FACE_LIB_S();
                                stFacelib1.enLibType=enLibType;
                                stFacelib1.isControl=true;
                                stFacelib1.ulFaceLibID=new NativeLong(ulFaceLibID);
                                stFacelib1.szLibName=Arrays.copyOf(szLibName.getBytes(),65);
                                stFacelib1.uiThreshold=new NativeLong(uiThreshold);
                                //设置添加人脸信息的对象
                                PU_FACE_INFO_ADD_S  puFaceInfoAdd = new PU_FACE_INFO_ADD_S();
                                puFaceInfoAdd.stFacelib=stFacelib1;
                                puFaceInfoAdd.ulChannelId=new NativeLong(101);
                                //设置人脸信息
                                PU_FACE_RECORD addfaceInfo=new PU_FACE_RECORD();
                                addfaceInfo.enCardType=qiantaiFaceInfo.getCardType();
                                addfaceInfo.enGender=qiantaiFaceInfo.getGender();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String birthday = sdf.format(qiantaiFaceInfo.getBirthday());
                                addfaceInfo.szBirthday=Arrays.copyOf(birthday.getBytes(),32);
                                addfaceInfo.szCardID=Arrays.copyOf(qiantaiFaceInfo.getCardId().getBytes(),32);
                                addfaceInfo.szCity=Arrays.copyOf(qiantaiFaceInfo.getCity().getBytes(),48);
                                addfaceInfo.szName=Arrays.copyOf(qiantaiFaceInfo.getUserName().getBytes("utf8"),64);
                                String absolutePath = new String(new File(".").getCanonicalPath() + qiantaiFaceInfo.getFaceImage());
                                System.out.println(absolutePath+"图片路径");
                                addfaceInfo.szPicPath=Arrays.copyOf(absolutePath.getBytes(),128);
                                addfaceInfo.szProvince=Arrays.copyOf(qiantaiFaceInfo.getProvince().getBytes(),32);
                                puFaceInfoAdd.stRecord=addfaceInfo;
                                String filename = qiantaiFaceInfo.getFaceImage().substring(qiantaiFaceInfo.getFaceImage().lastIndexOf("/")+1);
                                System.out.println(filename);
                                boolean addOneFaceV2;
                                boolean exitaddOneFaceV2;
                                if(Platform.isWindows()){
                                    addOneFaceV2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, puFaceInfoAdd, filename);
                                    exitaddOneFaceV2 = HWPuSDKLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, puFaceInfoAdd, filename);
                                }else {
                                    addOneFaceV2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(entryIdentifyId, puFaceInfoAdd, filename);
                                    exitaddOneFaceV2 = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_AddOneFaceV2(exitIdentifyId, puFaceInfoAdd, filename);
                                }
                                HuaWeiSdkApi.printReturnMsg();
                                if(addOneFaceV2 || exitaddOneFaceV2){
                                    System.out.println("添加人脸信息成功2");
                                    //提取特征值，接着设置布控
                                    //提取特征值
                                    PU_FACE_FEATURE_EXTRACT_S puFaceFeatureExtractS=new PU_FACE_FEATURE_EXTRACT_S();
                                    puFaceFeatureExtractS.stFacelib=stFacelib1;
                                    puFaceFeatureExtractS.ulChannelId=new NativeLong(101);
                                    //puFaceFeatureExtractS.taskID=new NativeLong(1);
                                    boolean getTeZheng;
                                    boolean getTeZhengexit;
                                    if(Platform.isWindows()){
                                        getTeZheng = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,puFaceFeatureExtractS);
                                        getTeZhengexit = HWPuSDKLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,puFaceFeatureExtractS);
                                    }else {
                                        getTeZheng = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(entryIdentifyId,puFaceFeatureExtractS);
                                        getTeZhengexit = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_FeatureExtract(exitIdentifyId,puFaceFeatureExtractS);
                                    }
                                    if(getTeZheng || getTeZhengexit){
                                        //接着设置布控
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
                                        boolean bukongExit;
                                        if(Platform.isWindows()){
                                            bukong = HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, puFaceLibSetS2);
                                            bukongExit=HWPuSDKLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, puFaceLibSetS2);
                                        }else {
                                            bukong = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(entryIdentifyId, puFaceLibSetS2);
                                            bukongExit=HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_SetFaceLib(exitIdentifyId, puFaceLibSetS2);
                                        }
                                        if (bukong || bukongExit){
                                            System.out.println("修改布控成功");
                                        }
                                    }
                                }else {
                                    System.out.println("添加人脸信息失败2");
                                }
                            }
                        }
                    }
                }else {
                    System.out.println("查询人脸信息失败");
                }
            }
        }else{
            System.out.println("查询人脸库失败");
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"人脸门禁授权成功",null);
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

