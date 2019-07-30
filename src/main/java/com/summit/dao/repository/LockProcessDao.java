package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.LockProcess;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface LockProcessDao  extends BaseMapper<LockProcess> {

    LockProcess selectById(String processId);

    List<LockProcess> selectByLockCode(String lockCode);

    List<LockProcess> selectCondition(@Param("lockProcess") LockProcess lockProcess,
                                @Param("start") Date start,
                                @Param("end") Date end);

}