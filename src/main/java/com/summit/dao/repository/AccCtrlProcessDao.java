package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.SimplePage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AccCtrlProcessDao extends BaseMapper<AccCtrlProcess> {

    AccCtrlProcess selectAccCtrlProcessById(@Param("accCtrlProId")  String accCtrlProId, @Param("roles") List<String> roles);

    List<AccCtrlProcess> selectAccCtrlProcessByLockCode(@Param("lockCode") String lockCode,
                                       @Param("page") SimplePage page,
                                       @Param("roles") List<String> roles);

    List<AccCtrlProcess> selectCondition(Page page, @Param("accCtrlProcess") AccCtrlProcess accCtrlProcess,
                                         @Param("start") Date start,
                                         @Param("end") Date end,
                                         @Param("roles") List<String> roles);

    List<AccCtrlProcess> selectRecodByAccessControlIds(@Param("accessControlIds") List<String> accessControlIds);

    int insertRecord(AccCtrlProcess accCtrlProcess);

    int updateRecord(AccCtrlProcess accCtrlProcess);

    int updateAccCtrlProcess(@Param("oldAccCtrlProcess") AccCtrlProcess oldAccCtrlProcess, @Param("newAccCtrlProcess") AccCtrlProcess newAccCtrlProcess);

    int selectConutByCondition(@Param("accCtrlProcess") AccCtrlProcess accCtrlProcess,
                               @Param("start") Date start,
                               @Param("end") Date end,
                               @Param("roles") List<String> roles);

}
