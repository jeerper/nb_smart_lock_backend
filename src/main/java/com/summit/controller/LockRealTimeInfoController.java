package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.FaceInfoEntity;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import com.summit.entity.LockRealTimeInfo;
import com.summit.sdk.huawei.model.AlarmStatus;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @ApiOperation(value = "查询全部锁信息及其对应的最新操作历史", notes = "分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/selectAllLockRealTimeInfo")
    public RestfulEntityBySummit<Map<String,Object>> selectAllLockRealTimeInfo(@ApiParam(value = "当前页，大于等于1")  @RequestParam("current") Integer current,
                                                                               @ApiParam(value = "每页条数，大于等于0")  @RequestParam("pageSize") Integer pageSize) {
        Map<String,Object> data = new HashMap<>();
        List<LockRealTimeInfo> lockRealTimeInfos;
        List<LockInfo> lockInfos;
        try {
            //查出有操作记录的锁并再分页
            lockInfos = lockInfoService.selectAllHaveHistory(new Page(current,pageSize));
            lockRealTimeInfos = getLockRealTimeInfo(lockInfos);
            Integer integer = alarmService.selectAlarmCountByStatus(AlarmStatus.UNPROCESSED.getCode());
            int allAlarmCount = integer == null ? 0 : integer;
            data.put("allAlarmCount",allAlarmCount);
            data.put("lockRealTimeInfos",lockRealTimeInfos);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询锁操作记录失败", data);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询锁操作记录成功", data);

    }

    /**
     * 根据锁信息列表查询组装锁实时信息列表
     * @param lockInfos 锁信息列表
     * @return 锁实时信息列表
     */
    private List<LockRealTimeInfo> getLockRealTimeInfo(List<LockInfo> lockInfos) {
        List<LockRealTimeInfo> lockRealTimeInfos = new ArrayList<>();
        for (LockInfo lock : lockInfos){
            if(lock == null)
                continue;
            String lockCode = lock.getLockCode();
            LockRealTimeInfo lockRealTimeInfo = new LockRealTimeInfo();
            if(lockCode != null){
                lockRealTimeInfo.setLockStatus(lock.getStatus());
                lockRealTimeInfo.setLockCode(lockCode);
                List<LockProcess> lockProcesses = lockRecordService.selectLockProcessByLockCode(lockCode,null);

                if(lockProcesses == null || lockProcesses.isEmpty()){
                    continue;
                }
                //取最新的一条操作记录(dao sql已排好序)
                LockProcess lockProcess = lockProcesses.get(0);
                lockRealTimeInfo.setLockId(lock.getLockId());

                if(lockProcess != null){
                    String userName = lockProcess.getUserName();
                    lockRealTimeInfo.setDeviceIp(lockProcess.getDeviceIp());
                    lockRealTimeInfo.setName(userName);

                    lockRealTimeInfo.setGender(lockProcess.getGender());
                    if(lockProcess.getBirthday() != null)
                        lockRealTimeInfo.setBirthday(CommonConstants.dateFormat.format(lockProcess.getBirthday()));
                    lockRealTimeInfo.setProvince(lockProcess.getProvince());
                    lockRealTimeInfo.setCity(lockProcess.getCity());
                    lockRealTimeInfo.setCardId(lockProcess.getCardId());
                    lockRealTimeInfo.setCardType(lockProcess.getCardType());
                    lockRealTimeInfo.setFaceMatchRate(lockProcess.getFaceMatchRate());
                    lockRealTimeInfo.setFaceLibName(lockProcess.getFaceLibName());
                    lockRealTimeInfo.setFaceLibType(lockProcess.getFaceLibType());

                    if(lockProcess.getProcessTime() != null)
                        lockRealTimeInfo.setPicSnapshotTime(CommonConstants.timeFormat.format(lockProcess.getProcessTime()));
                    FileInfo facePanorama = lockProcess.getFacePanorama();
                    if(facePanorama != null){
                        lockRealTimeInfo.setFacePanoramaUrl(facePanorama.getFilePath());
                    }
                    FileInfo facePic = lockProcess.getFacePic();
                    if(facePic != null){
                        lockRealTimeInfo.setFacePicUrl(facePic.getFilePath());
                    }
                }
            }
            lockRealTimeInfos.add(lockRealTimeInfo);
        }
        return lockRealTimeInfos;
    }
}
