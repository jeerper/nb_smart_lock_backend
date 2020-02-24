package com.summit.service;

import java.util.List;

public interface AccCtrlDeptService {
    int refreshAccCtrlDeptBatch(List<String> accessControlIds, String deptId);
}
