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

    @Autowired
    private LockInfoDao lockInfoDao;

    private static LockAuthCtrl lockAuthCtrl;
    @PostConstruct
    public void init() {
        lockAuthCtrl = this;
        lockAuthCtrl.lockInfoDao = this.lockInfoDao;
    }

    public static boolean toFilter(Object obj){
        if(obj == null){
            return false;
        }
//        UserInfo uerInfo = UserContextHolder.getUserInfo();
//        List<String> rolesList = Arrays.asList(uerInfo.getRoles());

        List<String> rolesList = Arrays.asList("r1","r2");
        if(obj instanceof LockInfo){
            LockInfo lockInfo = (LockInfo)obj;
            List<LockRole> lockRoles = lockInfo.getRoles();

            if(lockRoles == null || lockRoles.size() == 0){
                return false;
            }
            for (LockRole role : lockRoles){
                String roleId = role.getRoleCode();
                for (String roleStr : rolesList){
                    if (roleId != null && roleId.equals(roleStr)){
//                        log.info("用户{}有锁{}的权限",uerInfo.getUserName(),lockInfo.getLockCode());
                        return true;
                    }
                }
            }
            //能走到这里说明没有此锁权限
//            log.info("用户{}没有锁{}的权限",uerInfo.getUserName(),lockInfo.getLockCode());
            return false;
        }else if(obj instanceof LockProcess){
            LockProcess lockProcess = (LockProcess)obj;
            String lockCode = lockProcess.getLockCode();
            LockInfo lockInfo = lockAuthCtrl.lockInfoDao.selectBylockCode(lockCode);
            if(lockInfo == null){
                return false;
            }
            List<LockRole> lockRoles = lockInfo.getRoles();

            if(lockRoles == null || lockRoles.size() == 0){
                return false;
            }
            for (LockRole role : lockRoles){
                String roleId = role.getRoleCode();
                for (String roleStr : rolesList){
                    if (roleId != null && roleId.equals(roleStr)){
//                        log.info("用户{}有锁{}的权限",uerInfo.getUserName(),lockInfo.getLockCode());
                        return true;
                    }
                }
            }
            return false;
        }else if(obj instanceof Alarm){
            Alarm alarm = (Alarm)obj;
            LockProcess lockProRecord = alarm.getLockProRecord();
            if(lockProRecord == null){
                return false;
            }
            String lockCode = lockProRecord.getLockCode();
            LockInfo lockInfo = lockAuthCtrl.lockInfoDao.selectBylockCode(lockCode);
            if(lockInfo == null){
                return false;
            }
            List<LockRole> lockRoles = lockInfo.getRoles();

            if(lockRoles == null || lockRoles.size() == 0){
                return false;
            }
            for (LockRole role : lockRoles){
                String roleId = role.getRoleCode();
                for (String roleStr : rolesList){
                    if (roleId != null && roleId.equals(roleStr)){
//                        log.info("用户{}有锁{}的权限",uerInfo.getUserName(),lockInfo.getLockCode());
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    public static void toFilterLocks(List<LockInfo> lockInfos){
//        UserInfo uerInfo = UserContextHolder.getUserInfo();
//        String[] roles = uerInfo.getRoles();
        if(lockInfos == null || lockInfos.isEmpty()){
            return;
        }

        int size = lockInfos.size();
        for (int i = 0; i < size; i++) {
            LockInfo lock = lockInfos.get(i);
            if(!toFilter(lock)){
                lockInfos.remove(i);
                size--;
                i--;
            }
        }
    }

    public static void toFilterLockProcesses(List<LockProcess> lockProcess){
//        UserInfo uerInfo = UserContextHolder.getUserInfo();
//        String[] roles = uerInfo.getRoles();
        if(lockProcess == null || lockProcess.isEmpty()){
            return;
        }
        int size = lockProcess.size();
        for (int i = 0; i < size; i++) {
            LockProcess lockp = lockProcess.get(i);
            if(!toFilter(lockp)){
                lockProcess.remove(i);
                size--;
                i--;
            }
        }
    }
    public static void toFilterAlarms(List<Alarm> alarms){
//        UserInfo uerInfo = UserContextHolder.getUserInfo();
//        String[] roles = uerInfo.getRoles();
        if(alarms == null || alarms.isEmpty()){
            return;
        }
        int size = alarms.size();
        for (int i = 0; i < size; i++) {
            Alarm am = alarms.get(i);
            if(!toFilter(am)){
                alarms.remove(i);
                size--;
                i--;
            }
        }
    }

}
