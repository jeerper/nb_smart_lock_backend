package com.summit.util;

import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
            rolesList = Arrays.asList("ROLE_SUPERUSERxx");
        }
        if(rolesList.contains("ROLE_SUPERUSER")){
            rolesList = null;
        }
        return rolesList;
    }
}
