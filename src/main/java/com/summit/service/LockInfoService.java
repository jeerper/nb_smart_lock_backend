package com.summit.service;

import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.Page;

import java.util.List;

public interface LockInfoService {

    LockInfo selectLockById(String lockId);

    LockInfo selectBylockCode(String lockCode);

    List<LockInfo> selectAll(Page page);

    List<LockInfo> selectCondition(LockInfo lockInfo, Page page);

    int insertLock(LockInfo lockInfo);

    int updateLock(LockInfo lockInfo);

    int delLockByLockId(String lockId);

    int delLockByLockCod(String lockCode);

}
