package com.summit.util;

import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class UserDeptAuthUtils {
    private static final Logger log = LoggerFactory.getLogger(UserDeptAuthUtils.class);

    public UserDeptAuthUtils() {
    }

    public static List<String> getDepts(){
        UserInfo uerInfo = UserContextHolder.getUserInfo();
        List deptList;
        List rolesList=null;
        if (uerInfo != null) {
            deptList = Arrays.asList(uerInfo.getDepts());
            rolesList=Arrays.asList(uerInfo.getRoles());
        } else {
            deptList = Arrays.asList("DEPT_INVALID");
        }
        if (rolesList.contains("ROLE_SUPERUSER")) {
            deptList = null;
        }
        return deptList;
    }
}
