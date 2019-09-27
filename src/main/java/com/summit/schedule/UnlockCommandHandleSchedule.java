package com.summit.schedule;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.UnlockCommandQueue;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.dao.repository.UnlockCommandQueueDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.service.impl.NBLockServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class UnlockCommandHandleSchedule {
    @Autowired
    private UnlockCommandQueueDao unlockCommandQueueDao;
    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;
    @Autowired
    private NBLockServiceImpl unLockService;


    @Scheduled(fixedDelay = 2000)
    public void refreshUnlockCommandQueue() {
        List<UnlockCommandQueue> unlockCommandList = unlockCommandQueueDao.selectList(null);
        for (UnlockCommandQueue entity : unlockCommandList) {
            long differMinute = DateUtil.between(entity.getCreateTime(), new Date(), DateUnit.MINUTE);
            if (differMinute > 5) {
                //超时处理
                accCtrlProcessDao.update(null, Wrappers.<AccCtrlProcess>lambdaUpdate()
                        .set(AccCtrlProcess::getProcessResult, LockProcessResultType.NotOnline.getCode())
                        .set(AccCtrlProcess::getFailReason, LockProcessResultType.NotOnline.getDescription())
                        .set(AccCtrlProcess::getProcessTime, new Date())
                        .eq(AccCtrlProcess::getAccCtrlProId, entity.getAccCtrlProId()));
                unlockCommandQueueDao.deleteById(entity.getId());
                continue;
            }
            LockRequest lockRequest = new LockRequest(null, entity.getLockCode(), entity.getUnlockFaceName(), null, null);
            log.debug("下发开锁指令");
            //调用开锁接口
            RestfulEntityBySummit result = unLockService.toUnLock(lockRequest);

            BackLockInfo backLockInfo = result.getData() == null ? null : (BackLockInfo) result.getData();
            if (backLockInfo == null || backLockInfo.getObjx() == null) {
                continue;
            }
            //下发开锁指令后返回的状态码
            LockProcessResultType processResult = LockProcessResultType.codeOf(backLockInfo.getObjx());
            if (processResult != LockProcessResultType.CommandSuccess) {
                continue;
            }
            if (StrUtil.isBlank(backLockInfo.getRmid())) {
                continue;
            }
            log.debug("开锁成功");
            //用于查询开锁状态的命令UUID
            //开锁处理UUID
            String unlockProcessUuid = backLockInfo.getRmid();
            accCtrlProcessDao.update(null, Wrappers.<AccCtrlProcess>lambdaUpdate()
                    .set(AccCtrlProcess::getProcessResult, processResult.getCode())
                    .set(AccCtrlProcess::getProcessUuid, unlockProcessUuid)
                    .set(AccCtrlProcess::getProcessTime, new Date())
                    .eq(AccCtrlProcess::getAccCtrlProId, entity.getAccCtrlProId()));
            unlockCommandQueueDao.deleteById(entity.getId());
        }
    }
}
