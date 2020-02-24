package com.summit.service;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.SimplePage;
import com.summit.entity.AccCtrlDept;

import java.util.List;

public interface AccCtrlDeptService {

    int refreshAccCtrlDeptBatch(List<String> accessControlIds, String deptId);

    Page<AccCtrlDept> selectAccCtrlDeptsByPage(SimplePage simplePage);

    List<AccCtrlDept> selectAccCtrlInfoByDeptId(String deptId);

}
