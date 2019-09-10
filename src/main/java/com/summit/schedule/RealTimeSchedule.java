package com.summit.schedule;


import com.summit.dao.repository.AccessControlDao;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.util.AccCtrlProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
public class RealTimeSchedule {

    @Autowired
    private AccCtrlProcessUtil accCtrlProcessUtil;
    @Autowired
    private AccessControlDao accessControlDao;

    private static volatile List<String> lockCodes;


    @PostConstruct
    public void init() {
        lockCodes = accessControlDao.selectAllLockCodes(null);
        log.info("初始化后所有门禁对应的锁编号{}",lockCodes);
    }

    /**
     * 实时刷新锁、门禁、实时状态
     */

    @Scheduled(fixedDelay=2000)
    public void refreshStatus() {
        LockRequest lockRequest = new LockRequest();
        for(String lockCode : lockCodes) {
            lockRequest.setTerminalNum(lockCode);
            Integer currentStatus = accessControlDao.selectStatusLockCode(lockCode, null);
            Integer status = accCtrlProcessUtil.getLockStatus(lockRequest);
            //当前数据库状态不为告警，则更新门禁为当前查询外部接口所得结果
            if(status != null && currentStatus != null && !status.equals(currentStatus) && currentStatus != LockStatus.LOCK_ALARM.getCode())
                accCtrlProcessUtil.toUpdateAccCtrlAndLockStatus(status, lockCode);
        }
    }

    /**
     * 门禁发生变化时（增或删），刷新锁code列表
     */
    public void refreshIdsCall() {
        lockCodes = accessControlDao.selectAllAccessControlIds(null);
        log.info("刷新后所有所有门禁对应的锁编号{}",lockCodes);
    }
}
