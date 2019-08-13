package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.SimplePage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccessControlDao  extends BaseMapper<AccessControlInfo> {

    AccessControlInfo selectAccCtrlById(@Param("accessControlId") String accessControlId, @Param("roles") List<String> roles);

    AccessControlInfo selectAccCtrlByLockCode(@Param("lockCode")String lockCode, @Param("roles") List<String> roles);

    List<AccessControlInfo> selectCondition(@Param("accessControlInfo") AccessControlInfo accessControlInfo,
                                   @Param("page") SimplePage page,
                                   @Param("roles") List<String> roles);

    List<AccessControlInfo> selectHaveHistoryByPage(@Param("page") SimplePage page, @Param("roles") List<String> roles);

    Integer selectHaveHistoryCountByPage(@Param("page") SimplePage page, @Param("roles") List<String> roles);
}
