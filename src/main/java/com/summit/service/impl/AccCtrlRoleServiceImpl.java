package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccCtrlRole;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccCtrlRoleDao;
import com.summit.service.AccCtrlRoleService;
import com.summit.util.MybatisPage;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Page<AccCtrlRole> selectAccCtrlRolesByRoleCode(String roleCode,SimplePage page) {
        if(roleCode == null){
            log.error("角色code为空");
            return null;
        }
        QueryWrapper<AccCtrlRole> wrapper = new QueryWrapper<>();
        page = PageConverter.filterPage(page);
        IPage<AccCtrlRole> tempPage = new MybatisPage<>();;
        if(page != null){
            tempPage = new MybatisPage<>(page.getCurrent(),page.getPageSize());
        }
        IPage<AccCtrlRole> accCtrlRoleIPage = accCtrlRoleDao.selectPage(tempPage, wrapper.eq("role_id", roleCode));
        int rowsCount = 0;
        if(accCtrlRoleIPage != null){
            rowsCount = (int)accCtrlRoleIPage.getTotal();
        }
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        List<AccCtrlRole> records = null;
        if(accCtrlRoleIPage != null)
            records = accCtrlRoleIPage.getRecords();
        Page<AccCtrlRole> backPage = new Page<>();
        backPage.setContent(records);
        backPage.setPageable(pageable);
        return backPage;
    }

    /**
     * 分页查询门禁角色权限
     * @param page 简单分页对象
     * @return 门禁角色权限分页对象
     */
    @Override
    public Page<AccCtrlRole> selectAccCtrlRolesByPage(SimplePage page) {
        QueryWrapper<AccCtrlRole> wrapper = new QueryWrapper<>();
        List<AccCtrlRole> accCtrlRoles = accCtrlRoleDao.selectList(wrapper);
        int rowsCount = accCtrlRoles == null ? 0 : accCtrlRoles.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        page = PageConverter.filterPage(page);
        IPage<AccCtrlRole> tempPage = new MybatisPage<>();;
        if(page != null){
            tempPage = new MybatisPage<>(page.getCurrent(),page.getPageSize());
        }
        IPage<AccCtrlRole> accCtrlRoleIPage = accCtrlRoleDao.selectPage(tempPage, wrapper);
        List<AccCtrlRole> records = null;
        if(accCtrlRoleIPage != null)
            records = accCtrlRoleIPage.getRecords();
        Page<AccCtrlRole> backPage = new Page<>();
        backPage.setContent(records);
        backPage.setPageable(pageable);
        return backPage;
    }
}
