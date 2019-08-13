package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.cbb.utils.page.Page;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlRole;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccCtrlRoleDao;
import com.summit.service.AccCtrlRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccCtrlRoleServiceImpl implements AccCtrlRoleService {

    @Autowired
    private AccCtrlRoleDao accCtrlRoleDao;

    /**
     * 插入门禁角色权限信息
     * @param accCtrlRole 门禁角色权限对象
     * @return 返回不为-1则为成功
     */
    @Override
    public int insertAccCtrlRole(AccCtrlRole accCtrlRole) {
        if(accCtrlRole == null){
            log.error("门禁角色权限对象为空");
            return CommonConstants.UPDATE_ERROR;
        }
        return accCtrlRoleDao.insert(accCtrlRole);
    }

    /**
     * 更新门禁角色权限信息
     * @param accCtrlRole 门禁角色权限对象
     * @return 返回不为-1则为成功
     */
    @Override
    public int updateAccCtrlRole(AccCtrlRole accCtrlRole) {
        if(accCtrlRole == null){
            log.error("门禁角色权限对象为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<AccCtrlRole> wrapper = new UpdateWrapper<>();
        return accCtrlRoleDao.update(accCtrlRole,wrapper.eq("id",accCtrlRole.getId()));
    }

    /**
     * 删除门禁角色权限信息
     * @param id 门禁角色权限id
     * @return 返回不为-1则为成功
     */
    @Override
    public int delAccCtrlRoleById(String id) {
        if(id == null){
            log.error("门禁角色权限id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<AccCtrlRole> wrapper = new UpdateWrapper<>();
        return accCtrlRoleDao.delete(wrapper.eq("id",id));
    }

    /**
     * 根据id查询门禁角色权限
     * @param id 门禁角色权限id
     * @return 查询门禁角色权限对象
     */
    @Override
    public AccCtrlRole selectAccCtrlRoleById(String id) {
        if(id == null){
            log.error("门禁角色权限id为空");
            return null;
        }
        return accCtrlRoleDao.selectById(id);
    }

    /**
     * 根据角色code分页查询门禁角色权限
     * @param roleCode 角色code
     * @return 门禁角色权限分页对象
     */
    @Override
    public Page<AccCtrlRole> selectAccCtrlRolesByRoleCode(String roleCode) {
        if(roleCode == null){
            log.error("角色code为空");
            return null;
        }
        return null;
    }

    /**
     * 分页查询门禁角色权限
     * @param page 简单分页对象
     * @return 门禁角色权限分页对象
     */
    @Override
    public Page<AccCtrlRole> selectAccCtrlRolesByPage(SimplePage page) {

        return null;
    }
}
