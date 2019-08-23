package com.summit.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.repository.AccessControlDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.sdk.huawei.model.AccCtrlStatus;
import com.summit.sdk.huawei.model.LockStatus;
import com.summit.service.impl.NBLockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class LockProcessUtil {
    @Autowired
    private NBLockServiceImpl nbLockServiceImpl;
    @Autowired
    private AccessControlDao accessControlDao;
    @Autowired
    private LockInfoDao lockInfoDao;

    /**
     * 根据开锁操作返回状态更新相应门禁和锁状态
     * @param status 开锁操作返回状态
     * @param lockCode 当前摄像头对应锁编号
     */
    public void toUpdateAccCtrlAndLockStatus(Integer status, String lockCode) {
        AccCtrlStatus accCtrlStatus =  AccCtrlStatus.codeOf(status);
        LockStatus lockStatus = LockStatus.codeOf(status);
        //状态不合法则不更新
        if(accCtrlStatus != null){
            AccessControlInfo accessControlInfo = new AccessControlInfo();
            accessControlInfo.setStatus(status);
            accessControlInfo.setUpdatetime(new Date());
            //直接根据锁编号更新门禁状态
            accessControlDao.update(accessControlInfo, new UpdateWrapper<AccessControlInfo>().eq("lock_code", lockCode));
        }
        //状态不合法则不更新
        if(lockStatus != null){
            LockInfo lockInfo = new LockInfo();
            lockInfo.setStatus(lockStatus.getCode());
            lockInfo.setUpdatetime(new Date());
            lockInfoDao.update(lockInfo, new UpdateWrapper<LockInfo>().eq("lock_code", lockCode));
        }
    }

    /**
     * 查询开锁状态
     * @param lockRequest 请求参数
     * @return 开锁状态
     */
    public Integer getLockStatus(LockRequest lockRequest) {
        try {
            //休眠半秒再查询状态
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RestfulEntityBySummit back = nbLockServiceImpl.toQueryLockStatus(lockRequest);
        Object backData = back.getData();
        if((backData instanceof BackLockInfo)){
            return ((BackLockInfo) backData).getObjx();
        }
        return null;
    }

    /**
     * 根据锁id获取锁编号
     * @return 锁编号
     */
    public String getLockCodeById(String lockId){
        if(lockId == null || "".equals(lockId))
            return null;
        LockInfo lockInfo = lockInfoDao.selectById(lockId);
        String lockCode = null;
        if(lockInfo != null){
            lockCode = lockInfo.getLockCode();
        }
        return lockCode;
    }

    /**
     * 根据锁编号获取锁id
     * @return 锁id
     */
    public String getLockIdByCode(String lockCode){
        if(lockCode == null || "".equals(lockCode))
            return null;
        List<LockInfo> lockInfos = lockInfoDao.selectList(new QueryWrapper<LockInfo>().eq("lock_code", lockCode));
        if(lockInfos == null || lockInfos.isEmpty())
            return null;
        LockInfo lockInfo = lockInfos.get(0);
        String lockId = null;
        if(lockInfo != null){
            lockId = lockInfo.getLockId();
        }
        return lockId;
    }
}
