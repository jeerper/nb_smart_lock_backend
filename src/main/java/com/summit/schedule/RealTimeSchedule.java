package com.summit.schedule;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.dao.entity.AccCtrlRealTimeEntity;
import com.summit.dao.repository.AccCtrlRealTimeDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.util.AccCtrlProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class RealTimeSchedule {

    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;

    @Autowired
    private AccCtrlRealTimeDao accCtrlRealTimeDao;


    /**
     * 实时刷新门禁的锁实时状态
     */

    @Scheduled(fixedDelay = 2000)
    public void refreshRealTimeLockStatus() {
        List<AccCtrlRealTimeEntity> accCtrlRealTimeList = accCtrlRealTimeDao.selectCondition(null, null, null);
        LockRequest lockRequest = new LockRequest();
        for (AccCtrlRealTimeEntity accCtrlRealTime : accCtrlRealTimeList) {

            lockRequest.setTerminalNum(accCtrlRealTime.getLockCode());
            //锁的真实状态
            BackLockInfo backLockInfo = accCtrlProcessUtil.getLockStatus(lockRequest);
            if (backLockInfo == null) {
                continue;
            }
            Integer lockStatus = backLockInfo.getObjx();
            //数据库中，门禁实时表中门禁的状态
            Integer currentLockStatus = accCtrlRealTime.getAccCtrlStatus();
            //如果任意状态为空，则不更新数据
            if (lockStatus == null || currentLockStatus == null) {
                continue;
            }
            //如果锁不在线，不更新实时状态
            if (lockStatus == LockStatus.NOT_ONLINE.getCode()) {
                continue;
            }
            //如果锁是关闭的，并且门禁状态是报警，不更新实时状态
            if (lockStatus == LockStatus.LOCK_CLOSED.getCode() && currentLockStatus == LockStatus.LOCK_ALARM.getCode()) {
                continue;
            }
            //log.debug("锁当前状态:" + LockStatus.codeOf(lockStatus).getDescription());

            //锁的真实状态和数据库中的状态一致，不更新实时状态
            if (lockStatus.equals(currentLockStatus)) {
                continue;
            }
            //更新门禁实时表
            accCtrlRealTimeDao.update(null, Wrappers.<AccCtrlRealTimeEntity>lambdaUpdate()
                    .set(AccCtrlRealTimeEntity::getAccCtrlStatus, lockStatus)
                    .set(AccCtrlRealTimeEntity::getUpdatetime, new Date())
                    .eq(AccCtrlRealTimeEntity::getAccCrtlRealTimeId, accCtrlRealTime.getAccCrtlRealTimeId()));
        }
    }
}
