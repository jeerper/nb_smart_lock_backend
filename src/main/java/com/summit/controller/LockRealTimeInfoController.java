package com.summit.controller;

import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.entity.LockRealTimeInfo;
import com.summit.sdk.huawei.model.AlarmStatus;
import com.summit.service.AccCtrlProcessService;
import com.summit.service.AccessControlService;
import com.summit.service.AlarmService;
import com.summit.service.FaceInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "门禁实时信息接口")
@RestController
@RequestMapping("/accCtrlRealTimeInfo")
public class LockRealTimeInfoController {

//    @Autowired
//    private LockRecordService lockRecordService;
//    @Autowired
//    private LockInfoService lockInfoService;
    @Autowired
    private FaceInfoService faceInfoService;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AccCtrlProcessService accCtrlProcessService;
    @Autowired
    private AccessControlService accessControlService;


    @ApiOperation(value = "分页查询门禁操作实时信息", notes = "分页参数为空则查全部，不合法则取第一条")
    @GetMapping(value = "/selectAccCtrlRealTimeByPage")
    public RestfulEntityBySummit<Map<String,Object>> selectAllLockRealTimeInfo(@ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                               @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String,Object> data = new HashMap<>();
        List<LockRealTimeInfo> lockRealTimeInfos;
        List<LockInfo> lockInfos;
        List<AccessControlInfo> accessControlInfos;
        try {
            //查出有操作记录的门禁并再分页
            Page<AccessControlInfo> acPage = accessControlService.selectHaveHistoryByPage(new SimplePage(current, pageSize));
            if(acPage == null){
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"无门禁操作记录成功", null);
            }
            accessControlInfos = acPage.getContent();
            lockRealTimeInfos = getLockRealTimeInfo(accessControlInfos);
            Integer integer = alarmService.selectAlarmCountByStatus(AlarmStatus.UNPROCESSED.getCode());
            int allAlarmCount = integer == null ? 0 : integer;
            data.put("allAlarmCount",allAlarmCount);
            data.put("content",lockRealTimeInfos);
            data.put("pageable",acPage.getPageable());
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询门禁操作记录失败", data);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询门禁操作记录成功", data);

    }

    /**
     * 根据门禁信息列表查询组装门禁实时信息列表
     * @param accessControlInfos 门禁信息列表
     * @return 门禁实时信息列表
     */
    private List<LockRealTimeInfo> getLockRealTimeInfo(List<AccessControlInfo> accessControlInfos) {
        List<LockRealTimeInfo> lockRealTimeInfos = new ArrayList<>();
        for (AccessControlInfo accCtrl : accessControlInfos){
            if(accCtrl == null)
                continue;
            String accessControlId = accCtrl.getAccessControlId();
            LockRealTimeInfo lockRealTimeInfo = new LockRealTimeInfo();
            if(accessControlId != null){
                lockRealTimeInfo.setAccessControlId(accessControlId);
                lockRealTimeInfo.setAccessControlName(accCtrl.getAccessControlName());
                lockRealTimeInfo.setAccCtrlStatus(accCtrl.getStatus());
                lockRealTimeInfo.setLockId(accCtrl.getLockId());
                List<AccCtrlProcess> accCtrlProcesses = accCtrlProcessService.selectAccCtrlProcessByAccCtrlId(accessControlId,null);

                if(accCtrlProcesses == null || accCtrlProcesses.isEmpty()){
                    continue;
                }
                //取最新的一条操作记录(dao sql已排好序)
                AccCtrlProcess accCtrlProcess = accCtrlProcesses.get(0);

                if(accCtrlProcess != null){
                    String userName = accCtrlProcess.getUserName();
                    //门禁记录中操作的具体摄像头
                    lockRealTimeInfo.setDeviceIp(accCtrlProcess.getDeviceIp());
                    lockRealTimeInfo.setName(userName);

                    lockRealTimeInfo.setGender(accCtrlProcess.getGender());
                    Date birthday = accCtrlProcess.getBirthday();
                    try {
                        if(birthday != null)
                            lockRealTimeInfo.setBirthday(CommonConstants.dateFormat.format(birthday));
                    } catch (Exception e) {
                        log.error("生日格式有误");
                    }
                    lockRealTimeInfo.setProvince(accCtrlProcess.getProvince());
                    lockRealTimeInfo.setCity(accCtrlProcess.getCity());
                    lockRealTimeInfo.setCardId(accCtrlProcess.getCardId());
                    lockRealTimeInfo.setCardType(accCtrlProcess.getCardType());
                    lockRealTimeInfo.setFaceMatchRate(accCtrlProcess.getFaceMatchRate());
                    lockRealTimeInfo.setFaceLibName(accCtrlProcess.getFaceLibName());
                    lockRealTimeInfo.setFaceLibType(accCtrlProcess.getFaceLibType());
                    try {
                        if(accCtrlProcess.getProcessTime() != null)
                            lockRealTimeInfo.setPicSnapshotTime(CommonConstants.timeFormat.format(accCtrlProcess.getProcessTime()));
                    } catch (Exception e) {
                        log.error("操作时间格式有误");
                    }
                    FileInfo facePanorama = accCtrlProcess.getFacePanorama();
                    if(facePanorama != null){
                        lockRealTimeInfo.setFacePanoramaUrl(facePanorama.getFilePath());
                    }
                    FileInfo facePic = accCtrlProcess.getFacePic();
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
