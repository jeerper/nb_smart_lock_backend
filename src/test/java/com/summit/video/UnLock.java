package com.summit.video;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.repository.AccCtrlProcessDao;
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.sdk.huawei.model.LockProcessType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UnLock {
    @Autowired
    private AccCtrlProcessDao accCtrlProcessDao;

    @Test
    public void test1() {
        IPage<AccCtrlProcess> accCtrlProcessPage = accCtrlProcessDao.selectPage(new Page<>(1, 1), Wrappers.<AccCtrlProcess>lambdaQuery()
                .eq(AccCtrlProcess::getAccessControlId, "ac02")
                .eq(AccCtrlProcess::getUserName, "刘源")
                .in(AccCtrlProcess::getProcessResult,
                        LockProcessResultType.Success.getCode(),
                        LockProcessResultType.NotResponse.getCode(),
                        LockProcessResultType.WaitSendCommand.getCode(),
                        LockProcessResultType.CommandSuccess.getCode()
                )
                .eq(AccCtrlProcess::getProcessType, LockProcessType.UNLOCK.getCode())
                .orderByDesc(AccCtrlProcess::getProcessTime)
        );
        List<AccCtrlProcess> accCtrlProcessList = accCtrlProcessPage.getRecords();
        if (accCtrlProcessList.size() != 0) {
            AccCtrlProcess accCtrlProcess = accCtrlProcessList.get(0);
            long differMinute = DateUtil.between(accCtrlProcess.getProcessTime(), new Date(), DateUnit.MINUTE);
            if (differMinute <= 2) {
                log.debug("同一门禁同一人脸不能在2分钟内开锁多次");
            }
        }
    }
}
