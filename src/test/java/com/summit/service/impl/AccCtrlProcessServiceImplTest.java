package com.summit.service.impl;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.service.AccCtrlProcessService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccCtrlProcessServiceImplTest {

    @Autowired
    private AccCtrlProcessService accCtrlProcessService;

    @Test
    public void selectAccCtrlProcessCondition() {
        AccCtrlProcess accCtrlProcess = new AccCtrlProcess();
//        accCtrlProcess.setAccCtrlProId("1163356805951225858");
//        accCtrlProcess.setAccessControlName("门");
//        accCtrlProcess.setProcessType(3);
        Page<AccCtrlProcess> accCtrlProcessPage = accCtrlProcessService.selectAccCtrlProcessCondition(accCtrlProcess,null);
        System.out.println(accCtrlProcessPage);
    }
}