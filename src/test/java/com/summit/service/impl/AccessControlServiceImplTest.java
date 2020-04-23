package com.summit.service.impl;

import com.summit.common.util.UserAuthUtils;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.repository.AccessControlDao;
import com.summit.service.AccessControlService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccessControlServiceImplTest {

    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private AccessControlDao accessControlDao;

    @Test
    public void selectAccCtrlById() throws Exception {
        List<String> roles = UserAuthUtils.getRoles();
        AccessControlInfo accessControlInfo = accessControlService.selectAccCtrlById("ac02");
        System.out.println(accessControlInfo);
    }

    @Test
    public void selectAllAccessControlIds() {
//        List<String> aaa = accessControlDao.selectAllAccessControlIds(null);
//        List<String> aaa = accessControlDao.selectAllLockCodes(null);
        Integer aaa = accessControlDao.selectStatusLockCode("ssszzz",null);
        System.out.println(aaa);
    }
    @Test
    public void selectAllAccessControl() {
        List<AccessControlInfo> accessControlInfos = accessControlService.selectAllAccessControl(null);
        System.out.println(accessControlInfos);
    }

    @Test
    public void selectAllAccessControl2() {
        AccessControlInfo accessControlInfo = new AccessControlInfo();
        accessControlInfo.setAccessControlName("山");
        accessControlInfo.setLockCode("6");
        List<AccessControlInfo> accessControlInfos = accessControlService.selectCondition(accessControlInfo,null,null);
        System.out.println(accessControlInfos);
    }
}