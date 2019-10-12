package com.summit.schedule;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.AddAccCtrlprocess;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.AddAccCtrlprocessDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.util.AccCtrlProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class AccessControlProcessSchedule {

    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;


    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;
    @Autowired
    private AddAccCtrlprocessDao addAccCtrlprocessDao;


    /**
     * 实时刷新门禁的锁实时状态
     */

    @Scheduled(fixedDelay = 2000)
    public void refreshAccessControlProcessLockStatus() {
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
        accCtrlProcess.setProcessResult(LockProcessResultType.CommandSuccess.getCode());
        List<AccCtrlProcess> accCtrlProcessList = accCtrlProcessDao.selectCondition(null, accCtrlProcess, null, null, null);
        LockRequest lockRequest = new LockRequest();
        for (AccCtrlProcess accCtrlProcessEntity : accCtrlProcessList) {

            String processUuid = accCtrlProcessEntity.getProcessUuid();
            if (processUuid == null || "".equals(processUuid)) {
                continue;
            }

            lockRequest.setTerminalNum(accCtrlProcessEntity.getLockCode());
            lockRequest.setUuid(processUuid);
            //锁的真实状态
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
               log.error("线程睡眠失败",e);
            }
            BackLockInfo backLockInfo = accCtrlProcessUtil.getLockStatus(lockRequest);
            if (backLockInfo == null) {
                accCtrlProcessDao.update(null, Wrappers.<AccCtrlProcess>lambdaUpdate()
                        .set(AccCtrlProcess::getProcessResult, LockProcessResultType.Failure.getCode())
                        .set(AccCtrlProcess::getFailReason, "通讯异常")
                        .set(AccCtrlProcess::getProcessTime, new Date())
                        .eq(AccCtrlProcess::getAccCtrlProId, accCtrlProcessEntity.getAccCtrlProId()));
                continue;
            }

            Integer lockStatus = backLockInfo.getObjx();
            if (lockStatus == null) {
                //更新process_result状态为失败，和fail_reason失败原因
                accCtrlProcessDao.update(null, Wrappers.<AccCtrlProcess>lambdaUpdate()
                        .set(AccCtrlProcess::getProcessResult, LockProcessResultType.Failure.getCode())
                        .set(AccCtrlProcess::getFailReason, backLockInfo.getContent())
                        .set(AccCtrlProcess::getProcessTime, new Date())
                        .eq(AccCtrlProcess::getAccCtrlProId, accCtrlProcessEntity.getAccCtrlProId()));
            } else {
                log.debug("开锁结果:" + LockProcessResultType.codeOf(lockStatus).getDescription());
                //更新process_result状态
                accCtrlProcessDao.update(null, Wrappers.<AccCtrlProcess>lambdaUpdate()
                        .set(AccCtrlProcess::getProcessResult, lockStatus)
                        .set(AccCtrlProcess::getProcessTime, new Date())
                        .eq(AccCtrlProcess::getAccCtrlProId, accCtrlProcessEntity.getAccCtrlProId()));
                if (backLockInfo.getVol() != null) {
                    addAccCtrlprocessDao.update(null, Wrappers.<AddAccCtrlprocess>lambdaUpdate()
                            .set(AddAccCtrlprocess::getBatteryLeve, backLockInfo.getVol())
                            .eq(AddAccCtrlprocess::getAccessControlId, accCtrlProcessEntity.getAccessControlId()));
                }
            }
        }
    }
}
