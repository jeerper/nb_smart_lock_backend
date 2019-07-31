package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.LockProcess;
import com.summit.dao.entity.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface LockProcessDao  extends BaseMapper<LockProcess> {

    LockProcess selectLockProcessById(String processId);

    List<LockProcess> selectByLockCode(@Param("lockCode") String lockCode,@Param("page") Page page);

    List<LockProcess> selectCondition(@Param("lockProcess") LockProcess lockProcess,
                                @Param("start") Date start,
                                @Param("end") Date end,
                                @Param("page") Page page);

    int insertRecord(LockProcess lockProcess);

    int updateRecord(LockProcess lockProcess);

}