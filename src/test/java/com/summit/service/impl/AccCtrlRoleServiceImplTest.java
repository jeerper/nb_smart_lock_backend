package com.summit.service.impl;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccCtrlRole;
import com.summit.dao.entity.SimplePage;
import com.summit.service.AccCtrlRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccCtrlRoleServiceImplTest {

    @Autowired
    private AccCtrlRoleService accCtrlRoleService;
    @Test
    public void insertAccCtrlRole() {
        AccCtrlRole accCtrlRole = new AccCtrlRole(null,null,"ROLE_1489581392504","ac03");
        int result = accCtrlRoleService.insertAccCtrlRole(accCtrlRole);
        System.out.println(result);
    }

    @Test
    public void updateAccCtrlRole() {
        AccCtrlRole accCtrlRole = new AccCtrlRole("1161467601109413890",null,"ROLE_1565148602817","ac02");
        int result = accCtrlRoleService.updateAccCtrlRole(accCtrlRole);
        System.out.println(result);
    }

    @Test
    public void delAccCtrlRoleById() {

    }

    @Test
    public void selectAccCtrlRoleById() {
    }

    @Test
    public void selectAccCtrlRolesByRoleCode() {

        Page<AccCtrlRole> role = accCtrlRoleService.selectAccCtrlRolesByRoleCode("ROLE_SUPERUSER",
                new SimplePage(0,1));
        System.out.println(role);
    }

    @Test
    public void selectAccCtrlRolesByPage() {
        Page<AccCtrlRole> accCtrlRolePage = accCtrlRoleService.selectAccCtrlRolesByPage(new SimplePage(0, 3));
        System.out.println(accCtrlRolePage);
    }
}