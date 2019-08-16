package com.summit.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.dao.entity.Alarm;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.LockRole;
import com.summit.dao.repository.LockInfoDao;
import com.summit.dao.repository.LockRoleDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class LockAuthCtrl {

//    @Autowired
//    private LockInfoDao lockInfoDao;
//
//    private static LockAuthCtrl lockAuthCtrl;
//    @PostConstruct
//    public void init() {
//        lockAuthCtrl = this;
//        lockAuthCtrl.lockInfoDao = this.lockInfoDao;
//    }


    public static List<String> getRoles() {
        UserInfo uerInfo = UserContextHolder.getUserInfo();
        List<String> rolesList;
        if (uerInfo != null) {
            rolesList = Arrays.asList(uerInfo.getRoles());
        } else {
            rolesList = Arrays.asList("ROLE_1489581392504","ROLE_1565148602817","ROLE_SUPERUSER");
        }
        if(rolesList.contains("ROLE_SUPERUSER")){
            rolesList = null;
        }
        return rolesList;
    }
}
