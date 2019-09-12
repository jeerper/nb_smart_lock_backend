package com.summit.schedule;


import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AccCtrlRealTimeDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.util.AccCtrlProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AccessControlProcessSchedule {

    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;

    @Autowired
    private AccCtrlRealTimeDao accCtrlRealTimeDao;
    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;


    /**
     * 实时刷新门禁的锁实时状态
     */

    @Scheduled(fixedDelay = 2000)
    public void refreshAccessControlProcessLockStatus() {
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setProcessResult(LockProcessResultType.CommandSuccess.getCode());
        List<AccCtrlProcess> accCtrlProcessList = accCtrlProcessDao.selectCondition(accCtrlProcess, null, null, null, null);
        LockRequest lockRequest = new LockRequest();
        for (AccCtrlProcess accCtrlProcessEntity : accCtrlProcessList) {

            String processUuid = accCtrlProcessEntity.getProcessUuid();
            if (processUuid == null || "".equals(processUuid)) {
                continue;
            }

            lockRequest.setTerminalNum(accCtrlProcessEntity.getLockCode());
            lockRequest.setUuid(processUuid);
            lockRequest.setOperName(accCtrlProcessEntity.getAccessControlInfo().getCreateby());
            //锁的真实状态
            BackLockInfo backLockInfo = accCtrlProcessUtil.getLockStatus(lockRequest);
            if (backLockInfo == null) {
                continue;
            }

            Integer lockStatus = backLockInfo.getObjx();
            if (lockStatus == null) {
                //TODO：更新process_result状态为失败，和fail_reason失败原因
//                LockProcessResultType.Failure;

            } else {
                //TODO：调度器加入线程池
                //TODO：更新process_result状态
                log.debug(lockStatus.toString());
            }


        }

//        List<AccCtrlRealTimeEntity> accCtrlRealTimeList= accCtrlRealTimeDao.selectCondition(null, null, null);
//        LockRequest lockRequest = new LockRequest();
//        for (AccCtrlRealTimeEntity accCtrlRealTime : accCtrlRealTimeList) {
//
//            lockRequest.setTerminalNum(accCtrlRealTime.getLockCode());
//            //锁的真实状态
//            Integer lockStatus = accCtrlProcessUtil.getLockStatus(lockRequest);
//            //数据库中，门禁实时表中门禁的状态
//            Integer currentLockStatus = accCtrlRealTime.getAccCtrlStatus();
//            //如果任意状态为空，则不更新数据
//            if (lockStatus == null || currentLockStatus == null) {
//                continue;
//            }
//            //如果锁不在线，不更新实时状态
//            if (lockStatus == LockStatus.NOT_ONLINE.getCode()) {
//                continue;
//            }
//            //如果锁是关闭的，并且门禁状态是报警，不更新实时状态
//            if (lockStatus == LockStatus.LOCK_CLOSED.getCode() && currentLockStatus == LockStatus.LOCK_ALARM.getCode()) {
//                continue;
//            }
//            AccCtrlRealTimeEntity accCtrlRealTimeEntity=new AccCtrlRealTimeEntity();
//            accCtrlRealTimeEntity.setAccCrtlRealTimeId(accCtrlRealTime.getAccCrtlRealTimeId());
//            accCtrlRealTimeEntity.setAccCtrlStatus(lockStatus);
//            accCtrlRealTimeEntity.setUpdatetime(new Date());
//            //更新门禁实时表
//            accCtrlRealTimeDao.updateById(accCtrlRealTimeEntity);
//        }
    }
}
