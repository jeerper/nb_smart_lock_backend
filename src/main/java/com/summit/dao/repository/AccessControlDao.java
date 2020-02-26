package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.SimplePage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccessControlDao  extends BaseMapper<AccessControlInfo> {

    AccessControlInfo selectAccCtrlById(@Param("accessControlId") String accessControlId, @Param("roles") List<String> roles);

    AccessControlInfo selectAccCtrlByLockCode(@Param("lockCode")String lockCode, @Param("roles") List<String> roles);

    List<AccessControlInfo> selectCondition(Page page, @Param("accessControlInfo") AccessControlInfo accessControlInfo,
                                            @Param("roles") List<String> roles);

    List<AccessControlInfo> selectHaveHistoryByPage(@Param("page") SimplePage page, @Param("roles") List<String> roles);

    Integer selectHaveHistoryCountByPage(@Param("page") SimplePage page, @Param("roles") List<String> roles);

    List<String>  selectAllAccessControlIds(@Param("roles") List<String> roles);

    List<String>  selectAllLockCodes(@Param("roles") List<String> roles);

    Integer  selectStatusLockCode(@Param("lockCode") String lockCode, @Param("roles") List<String> roles);

    List<AccessControlInfo> selectAccCtrlInfosByUserName(@Param("userName") String userName);

    void insertAccessControlInfos(@Param("accessControlInfos")List<AccessControlInfo> accessControlInfos);

    List<AccessControlInfo> loginAccessControlInfoExport( @Param("accessControlInfo")AccessControlInfo accessControlInfo);

}
