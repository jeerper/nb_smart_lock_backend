package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LockInfoDao  extends BaseMapper<LockInfo> {

    LockInfo selectLockById(@Param("lockId") String lockId, @Param("roles") List<String> roles);

    LockInfo selectBylockCode(@Param("lockCode")String lockCode, @Param("roles") List<String> roles);

    List<LockInfo> selectCondition(@Param("lockInfo") LockInfo lockInfo,
                                   @Param("page") Page page,
                                   @Param("roles") List<String> roles);

    List<LockInfo> selectAllHaveHistory(@Param("page") Page page,@Param("roles") List<String> roles);
}