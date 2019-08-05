package com.summit.controller;

import com.netflix.discovery.converters.Auto;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.FaceInfoEntity;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.entity.LockRealTimeInfo;
import com.summit.service.AlarmService;
import com.summit.service.FaceInfoService;
import com.summit.service.LockInfoService;
import com.summit.service.LockRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags = "锁实时信息接口")
@RestController
@RequestMapping("/lockRealTimeInfo")
public class LockRealTimeInfoController {

    @Autowired
    private LockRecordService lockRecordService;
    @Autowired
    private LockInfoService lockInfoService;
    @Autowired
    private FaceInfoService faceInfoService;
    @Autowired
    private AlarmService alarmService;

    /**
     * 增删改操作异常
     */
    private static final Integer UPDATE_ERROR = -1;

    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @ApiOperation(value = "查询全部锁信息及其对应的最新操作历史", notes = "分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/selectAllLockRealTimeInfo")
    public RestfulEntityBySummit<List<LockRealTimeInfo>> selectAllLockRealTimeInfo(@ApiParam(value = "当前页，大于等于1")  @RequestParam("current") Integer current,
                                                           @ApiParam(value = "每页条数，大于等于1")  @RequestParam("pageSize") Integer pageSize) {
        List<LockRealTimeInfo> lockRealTimeInfos = new ArrayList<>();
        List<LockInfo> lockInfos = null;
        try {
            if(current == null && pageSize == null){
                lockInfos = lockInfoService.selectAll(null);

            }else{
                lockInfos = lockInfoService.selectAll(new Page(current,pageSize));
            }
            for (LockInfo lock : lockInfos){
                if(lock == null)
                    break;
                String lockCode = lock.getLockCode();
                if(lockCode != null){
                    LockRealTimeInfo lockRealTimeInfo = new LockRealTimeInfo();
                    lockRealTimeInfo.setLockStatus(lock.getStatus());
                    lockRealTimeInfo.setLockCode(lockCode);
                    List<LockProcess> lockProcesses = lockRecordService.selectLockProcessByLockCode(lockCode,null);
                    if(lockProcesses != null && !lockProcesses.isEmpty()){
                        Collections.sort(lockProcesses, new Comparator<LockProcess>() {
                            @Override
                            public int compare(LockProcess lockProcessOne, LockProcess lockProcessTwo) {
                                Date processOneTime = lockProcessOne.getProcessTime();
                                Date processTwoTime = lockProcessTwo.getProcessTime();
                                if(processOneTime != null  && processTwoTime != null){
                                    if(processOneTime.getTime() < processTwoTime.getTime()){
                                        return 1;
                                    }else if(processOneTime.getTime() == processTwoTime.getTime()){
                                        return 0;
                                    }else {
                                        return -1;
                                    }
                                }
                                return 0;
                            }
                        });
                        //取最新的一条操作记录
                        LockProcess lockProcess = lockProcesses.get(0);
                        lockRealTimeInfo.setLockId(lock.getLockId());

                        if(lockProcess != null){
                            String userName = lockProcess.getUserName();
                            lockRealTimeInfo.setDeviceIp(lockProcess.getDeviceIp());
                            lockRealTimeInfo.setName(userName);
                            lockRealTimeInfo.setPicSnapshotTime(dateTimeFormat.format(lockProcess.getProcessTime()));
                            FileInfo facePanorama = lockProcess.getFacePanorama();
                            if(facePanorama != null){
                                lockRealTimeInfo.setFacePanoramaUrl(facePanorama.getFilePath());
                            }
                            FileInfo facePic = lockProcess.getFacePic();
                            if(facePic != null){
                                lockRealTimeInfo.setFacePicUrl(facePic.getFilePath());
                            }
                            FaceInfoEntity faceInfoEntity = faceInfoService.selectByUserName(userName);
                            if(faceInfoEntity != null){
                                lockRealTimeInfo.setBirthday(dateFormat.format(faceInfoEntity.getBirthday()));
                                lockRealTimeInfo.setCardId(faceInfoEntity.getCardId());
                                lockRealTimeInfo.setCardType(faceInfoEntity.getCardType());
                                lockRealTimeInfo.setCity(faceInfoEntity.getCity());
                                lockRealTimeInfo.setGender(faceInfoEntity.getGender());
                                lockRealTimeInfo.setFaceMatchRate(faceInfoEntity.getFaceMatchRate());
                                lockRealTimeInfo.setFaceLibName(faceInfoEntity.getFaceLibName());
                                lockRealTimeInfo.setFaceLibType(faceInfoEntity.getFaceLibType());

                            }
                        }

                    }
                    List<Alarm> alarms = alarmService.selectAlarmByLockCode(lockCode, null);
                    lockRealTimeInfo.setAlarmCount(alarms == null ? 0: alarms.size());
                    lockRealTimeInfos.add(lockRealTimeInfo);
                }
            }

        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询锁操作记录失败", lockRealTimeInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询锁操作记录成功", lockRealTimeInfos);

    }

}
