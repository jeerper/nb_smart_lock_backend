package com.summit.service;

/**
 * Created by Administrator on 2019/9/16.
 */

import com.summit.dao.entity.AddAccCtrlprocess;

/**
 * 统计分析接口
 */
public interface AddAccCtrlprocessService {
    /**
     * 分析统计记录插入
     * @param addAccCtrlprocess
     * @return -1则为不成功
     */
    int insert(AddAccCtrlprocess addAccCtrlprocess);

    AddAccCtrlprocess selectAccCtrlByAccCtrlName(String accessControlName);

    int update(AddAccCtrlprocess addAccCtrlprocess);
}
