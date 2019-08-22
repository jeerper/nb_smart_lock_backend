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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Override
    public int insertAccCtrlRoleBatch(List<AccCtrlRole>  accCtrlRoles) {
        if(accCtrlRoles == null || accCtrlRoles.isEmpty()){
            log.error("门禁角色权限对象数组为空");
            return CommonConstants.UPDATE_ERROR;
        }
        int result = 0;
        int failCount = 0;
        for(AccCtrlRole accRole : accCtrlRoles) {
            try {
                if(accCtrlRoleDao.insert(accRole) == CommonConstants.UPDATE_ERROR){
                    failCount++;
                }
            } catch (Exception e) {
                log.error("为角色添加门禁{}时失败",accRole.getAccessControlId());
            }
        }
        //全部失败才算失败
        if(failCount == accCtrlRoles.size())
            result = CommonConstants.UPDATE_ERROR;
        return result;
    }

    /**
     * 批量刷新门禁角色权限信息，，所传角色之前没有关联某门禁且所传列表中有则添加，之前已关联某门禁权限而所传列表中有则不添加，之前已关联某门禁权限而所传列表中没有则删除
     * @param accessControlIds 角色关联的所有门禁id
     * @param roleCode 角色code
     * @return 返回不为-1则为成功
     */
    @Override
    public int refreshAccCtrlRoleBatch(List<String> accessControlIds, String roleCode) {
        if(accessControlIds == null || roleCode == null){
            return CommonConstants.UPDATE_ERROR;
        }
        //查出角色当前关联的角色
        List<AccCtrlRole> ctrlRoles = selectAccCtrlRolesByRoleCode(roleCode);

        if(ctrlRoles != null && !ctrlRoles.isEmpty()){
            //若传入列表为空集合，说明需要删除所有授权
            if(accessControlIds.isEmpty()){
                List<String> authIds = new ArrayList<>();
                for(AccCtrlRole ctrlRole : ctrlRoles){
                    if(ctrlRole == null)
                        continue;
                    authIds.add(ctrlRole.getId());
                }
                return accCtrlRoleDao.deleteBatchIds(authIds);
            }
            //先删除数据库在传入列表中找不到的门禁授权
            for(AccCtrlRole ctrlRole : ctrlRoles) {
                boolean needDel = true;
                for(String acId : accessControlIds) {
                    if(acId != null && acId.equals(ctrlRole.getAccessControlId())){
                        needDel = false;
                    }
                }
                if(needDel){
                    accCtrlRoleDao.deleteById(ctrlRole.getId());
                }
            }

            //再添加传入列表在数据库在中找不到的门禁授权
            for(String acId : accessControlIds) {
                boolean needAdd = true;
                for(AccCtrlRole ctrlRole : ctrlRoles) {
                    if(acId != null && acId.equals(ctrlRole.getAccessControlId())){
                        needAdd = false;
                    }
                }
                if(needAdd){
                    accCtrlRoleDao.insert(new AccCtrlRole(null,null,roleCode,acId));
                }
            }
        }else{
            //若之前无此角色对应的门禁权限，则直接添加
            List<AccCtrlRole>  accCtrlRoles = new ArrayList<>();
            for(String accessControlId : accessControlIds) {
                accCtrlRoles.add(new AccCtrlRole(null,null,roleCode,accessControlId));
            }
            return insertAccCtrlRoleBatch(accCtrlRoles);
        }
        return 0;
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
     * 根据角色code查询所有门禁角色权限
     * @param roleCode 角色code
     * @return 门禁角色权限列表
     */
    @Override
    public List<AccCtrlRole> selectAccCtrlRolesByRoleCode(String roleCode) {
        if(roleCode == null){
            log.error("角色code为空");
            return null;
        }
        QueryWrapper<AccCtrlRole> wrapper = new QueryWrapper<>();
        return accCtrlRoleDao.selectList(wrapper.eq("role_id",roleCode));
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
