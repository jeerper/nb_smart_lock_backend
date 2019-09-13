package com.summit.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.util.ResultBuilder;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AccCtrlRealTimeDao;
import com.summit.dao.repository.AlarmDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.UpdateAlarmParam;
import com.summit.sdk.huawei.model.LockProcessMethod;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.sdk.huawei.model.LockProcessType;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.service.AlarmService;
import com.summit.service.impl.NBLockServiceImpl;
import com.summit.util.AccCtrlProcessUtil;
import com.summit.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags = "门禁告警接口")
@RestController
@RequestMapping("/accessControlAlarm")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;
    @Autowired
    private NBLockServiceImpl nbLockServiceImpl;
    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private AccCtrlRealTimeDao accCtrlRealTimeDao;


    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;

    @ApiOperation(value = "更新门禁告警状态", notes = "alarmId为null或空串则不进行更新告警操作")
    @PutMapping(value = "/updateAlarmStatus")
    public RestfulEntityBySummit<String> updateAlarmStatus(@RequestBody UpdateAlarmParam updateAlarmParam) {
        if (updateAlarmParam == null) {
            log.error("参数为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "参数为空", null);
        }


        String msg = "操作成功";

        boolean needUnLock = updateAlarmParam.isNeedUnLock();
        String lockId = updateAlarmParam.getLockId();
        Integer alarmStatus = updateAlarmParam.getAlarmStatus();
        String processRemark = updateAlarmParam.getProcessRemark();
        String operName = null;
        UserInfo userInfo = UserContextHolder.getUserInfo();
        if (userInfo != null)
            operName = userInfo.getName();

        //开锁指令下发成功的处理逻辑
        //处理结果
        Integer processResult = null;
        //开锁处理UUID
        String unlockProcessUuid = null;
        String accCtrlProId = updateAlarmParam.getAccCtrlProId();
        if (needUnLock) {
            LockRequest lockRequest = new LockRequest();
            lockRequest.setLockId(lockId);
            lockRequest.setOperName(operName);

            RestfulEntityBySummit result = nbLockServiceImpl.toUnLock(lockRequest);
            if (result == null) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, msg + "开锁失败", null);
            }

            BackLockInfo backLockInfo = result.getData() == null ? null : (BackLockInfo) result.getData();

            if (backLockInfo == null) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "开锁失败", null);
            }
            if (backLockInfo.getObjx() == null) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "开锁失败:" + backLockInfo.getContent(), null);
            }
            if (backLockInfo.getObjx() != LockProcessResultType.CommandSuccess.getCode()) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "开锁失败:" + backLockInfo.getContent(), null);
            }

            //开锁指令下发成功的处理逻辑
            processResult = backLockInfo.getObjx();

            unlockProcessUuid = backLockInfo.getRmid();


            AccCtrlProcess currentAccCtrlProcess = accCtrlProcessDao.selectOne(Wrappers.<AccCtrlProcess>lambdaQuery()
                    .eq(AccCtrlProcess::getAccCtrlProId, accCtrlProId));

            //新增门禁操作记录
            AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
            accCtrlProcess.setProcessMethod(LockProcessMethod.INTERFACE_BY.getCode());
            accCtrlProcess.setProcessTime(new Date());
            accCtrlProcess.setProcessType(LockProcessType.UNLOCK.getCode());
            accCtrlProcess.setUserName(operName);
            accCtrlProcess.setAccessControlId(currentAccCtrlProcess.getAccessControlId());
            accCtrlProcess.setAccessControlName(currentAccCtrlProcess.getAccessControlName());
            accCtrlProcess.setProcessResult(processResult);
            accCtrlProcess.setProcessUuid(unlockProcessUuid);
            accCtrlProcessDao.insert(accCtrlProcess);
        }


        Alarm alarm = alarmDao.selectOne(Wrappers.<Alarm>lambdaQuery()
                .eq(Alarm::getAccCtrlProId, accCtrlProId));

        String alarmId = null;
        if (alarm != null) {
            alarmId = alarm.getAlarmId();
        }
        //处理告警业务
        if (alarmId != null && !"".equals(alarmId) && !"".equals(processRemark)) {
            //更新告警表
            alarmDao.update(null, Wrappers.<Alarm>lambdaUpdate()
                    .set(Alarm::getAlarmStatus, alarmStatus)
                    .set(Alarm::getProcessPerson, operName)
                    .set(Alarm::getProcessRemark, processRemark)
                    .set(Alarm::getUpdatetime, new Date())
                    .eq(Alarm::getAlarmId, alarmId));

            if (needUnLock) {
                //更新门禁操作记录
                accCtrlProcessDao.update(null, Wrappers.<AccCtrlProcess>lambdaUpdate()
                        .set(AccCtrlProcess::getProcessUuid, unlockProcessUuid)
                        .set(AccCtrlProcess::getProcessResult, processResult)
                        .set(AccCtrlProcess::getProcessTime, new Date())
                        .eq(AccCtrlProcess::getAccCtrlProId, accCtrlProId));
            } else {
                //只处理告警任务，不开锁时的业务
                //更新实时状态为锁定状态
                accCtrlRealTimeDao.update(null, Wrappers.<AccCtrlRealTimeEntity>lambdaUpdate()
                        .set(AccCtrlRealTimeEntity::getAccCtrlStatus, LockStatus.LOCK_CLOSED.getCode())
                        .set(AccCtrlRealTimeEntity::getUpdatetime, new Date())
                        .eq(AccCtrlRealTimeEntity::getAlarmId, alarmId));
            }
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, msg + "更新告警状态成功", null);
    }


    @ApiOperation(value = "用告警id列表批量删除门禁告警信息", notes = "alarmIds不能为空")
    @DeleteMapping(value = "/delAlarmByIdBatch")
    public RestfulEntityBySummit<String> delAlarmByIdBatch(@ApiParam(value = "门禁告警id数组", required = true) @RequestParam(value = "alarmIds") List<String> alarmIds) {
        if (CommonUtil.isEmptyList(alarmIds)) {
            log.error("告警id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "告警id为空", null);
        }
        try {
            alarmService.delAlarmByIdBatch(alarmIds);
        } catch (Exception e) {
            log.error("删除告警信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "删除告警信息失败", null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "删除告警信息成功", null);
    }


    @ApiOperation(value = "查询当前用户所有未处理的告警数量", notes = "查询当前用户所有未处理的告警数量")
    @GetMapping(value = "/queryUntreatedAlarmCount")
    public RestfulEntityBySummit<Integer> queryUntreatedAlarmCount() {

        Integer alarmCount;
        try {
            alarmCount = alarmService.selectAlarmCountByStatus(1);
        } catch (Exception e) {
            log.error("查询未处理告警数量失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询未处理告警数量失败", null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询未处理告警数量成功", alarmCount);
    }

    @ApiOperation(value = "根据告警id查询告警信息", notes = "alarmId不能为空。查询唯一一条告警信息")
    @GetMapping(value = "/queryAlarmById")
    public RestfulEntityBySummit<Alarm> queryAlarmById(@ApiParam(value = "门禁告警id", required = true) @RequestParam(value = "alarmId") String alarmId) {
        if (alarmId == null) {
            log.error("告警id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "告警id为空", null);
        }
        Alarm alarm = null;
        try {
            alarm = alarmService.selectAlarmById(alarmId);
        } catch (Exception e) {
            log.error("根据告警id查询告警信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "根据告警id查询告警信息失败", alarm);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "根据告警id查询告警信息成功", alarm);
    }


    @ApiOperation(value = "根据所传一个或多个条件组合分页查询告警信息", notes = "各字段都为空则查询全部。时间信息为空或不合法则无时间限制。分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1" +
            "，pageSize<=0则查不到结果")
    @GetMapping(value = "/queryAlarmCondition")
    public RestfulEntityBySummit<Page<Alarm>> queryAlarmCondition(@ApiParam(value = "门禁id，精确匹配") @RequestParam(value = "accessControlId", required
            = false) String accessControlId,
                                                                  @ApiParam(value = "门禁名称，模糊匹配") @RequestParam(value = "accessControlName",
                                                                          required = false) String accessControlName,
                                                                  @ApiParam(value = "门禁告警处理状态，，0已处理，1未处理") @RequestParam(value = "alarmStatus",
                                                                          required = false) Integer alarmStatus,
                                                                  @ApiParam(value = "起始时间") @RequestParam(value = "startTime", required = false) String startTime,
                                                                  @ApiParam(value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
                                                                  @ApiParam(value = "当前页，大于等于1") @RequestParam(value = "current", required = false) Integer current,
                                                                  @ApiParam(value = "每页条数，大于等于0") @RequestParam(value = "pageSize", required =
                                                                          false) Integer pageSize) {
        Page<Alarm> alarms = null;
        Date start = CommonUtil.strToDate(startTime, CommonConstants.STARTTIMEMARK);
        Date end = CommonUtil.strToDate(endTime, CommonConstants.ENDTIMEMARK);
        try {
            Alarm alarm = new Alarm();
            if (!CommonUtil.isEmptyStr(accessControlId))
                alarm.setAccessControlId(accessControlId);
            if (!CommonUtil.isEmptyStr(accessControlName))
                alarm.setAccessControlName(accessControlName);
            alarm.setAlarmStatus(alarmStatus);
            alarms = alarmService.selectAlarmConditionByPage(alarm, start, end, new SimplePage(current, pageSize));
        } catch (Exception e) {
            log.error("条件查询告警信息失败", e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "条件查询告警信息失败", alarms);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "条件查询告警信息成功", alarms);
    }

}
