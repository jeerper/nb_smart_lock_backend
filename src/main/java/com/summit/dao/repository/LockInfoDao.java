package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.LockInfo;

import java.util.List;

public interface LockInfoDao  extends BaseMapper<LockInfo> {

    LockInfo selectById(String lockId);

    LockInfo selectBylockCode(String lockCode);

    List<LockInfo> selectCondition(LockInfo lockInfo);
}