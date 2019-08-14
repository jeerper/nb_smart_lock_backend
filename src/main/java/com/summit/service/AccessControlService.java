package com.summit.service;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.SimplePage;

import java.util.List;

/**
 * 门禁信息service接口
 */
public interface AccessControlService {
    /**
     * 根据门禁id查询唯一门禁信息
     * @param accessControlId 门禁id
     * @return 唯一门禁信息对象
     */
    AccessControlInfo selectAccCtrlById(String accessControlId);

    /**
     * 根据锁编号查询唯一门禁信息
     * @param lockCode 锁编号
     * @return 唯一门禁信息对象
     */
    AccessControlInfo selectAccCtrlByLockCode(String lockCode);

    /**
     * 分页查询全部门禁信息
     * @param page 分页对象
     * @return 门禁信息列表
     */
    List<AccessControlInfo> selectAll(SimplePage page);

    /**
     * 分页查询全部有操作记录的门禁信息
     * @param page 分页对象
     * @return 门禁信息列表分页对象
     */
    Page<AccessControlInfo> selectHaveHistoryByPage(SimplePage page);

    /**
     * 条件查询门禁信息
     * @param accessControlInfo 门禁信息对象
     * @param page 分页对象
     * @return 门禁信息列表
     */
    List<AccessControlInfo> selectCondition(AccessControlInfo accessControlInfo, SimplePage page);

    /**
     * 插入门禁信息
     * @param accessControlInfo 门禁信息对象
     * @return 返回不为-1则为成功
     */
    int insertAccCtrl(AccessControlInfo accessControlInfo);

    /**
     * 更新门禁信息
     * @param accessControlInfo 门禁信息对象
     * @return 返回不为-1则为成功
     */
    int updateAccCtrl(AccessControlInfo accessControlInfo);

    /**
     * 根据id删除门禁信息
     * @param accessControlId 门禁id
     * @return 返回不为-1则为成功
     */
    int delAccCtrlByAccCtrlId(String accessControlId);

    /**
     * 根据锁编号删除门禁信息
     * @param lockCode 锁编号
     * @return 返回不为-1则为成功
     */
    int delAccCtrlByLockCode(String lockCode);
}