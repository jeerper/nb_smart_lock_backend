package com.summit.service;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccCtrlRole;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccCtrlRoleDao;

import java.util.List;

/**
 * 门禁角色权限service接口
 */
public interface AccCtrlRoleService {

    /**
     * 插入门禁角色权限信息
     * @param accCtrlRole 门禁角色权限对象
     * @return 返回不为-1则为成功
     */
    int insertAccCtrlRole(AccCtrlRole accCtrlRole);

    /**
     * 批量插入门禁角色权限信息
     * @param accCtrlRoles 门禁角色权限对象列表
     * @return 返回不为-1则为成功
     */
    int insertAccCtrlRoleBatch(List<AccCtrlRole> accCtrlRoles);

    /**
     * 批量刷新门禁角色权限信息，，所传角色之前没有关联某门禁且所传列表中有则添加，之前已关联某门禁权限而所传列表中有则不添加，之前已关联某门禁权限而所传列表中没有则删除
     * @param accessControlIds 角色关联的所有门禁id
     * @param roleCode 角色code
     * @return 返回不为-1则为成功
     */
    int refreshAccCtrlRoleBatch(List<String> accessControlIds,String roleCode);

    /**
     * 更新门禁角色权限信息
     * @param accCtrlRole 门禁角色权限对象
     * @return 返回不为-1则为成功
     */
    int updateAccCtrlRole(AccCtrlRole accCtrlRole);

    /**
     * 删除门禁角色权限信息
     * @param id 门禁角色权限id
     * @return 返回不为-1则为成功
     */
    int delAccCtrlRoleById(String id);

    /**
     * 根据id查询门禁角色权限
     * @param id 门禁角色权限id
     * @return 查询门禁角色权限对象
     */
    AccCtrlRole selectAccCtrlRoleById(String id);

    /**
     * 根据角色code查询所有门禁角色权限
     * @param roleCode 角色code
     * @return 门禁角色权限列表
     */
    List<AccCtrlRole> selectAccCtrlRolesByRoleCode(String roleCode);

    /**
     * 根据角色code分页查询门禁角色权限
     * @param roleCode 角色code
     * @param page 简单分页对象
     * @return 门禁角色权限分页对象
     */
    Page<AccCtrlRole> selectAccCtrlRolesByRoleCode(String roleCode,SimplePage page);

    /**
     * 分页查询门禁角色权限
     * @param page 简单分页对象
     * @return 门禁角色权限分页对象
     */
    Page<AccCtrlRole> selectAccCtrlRolesByPage(SimplePage page);

}
