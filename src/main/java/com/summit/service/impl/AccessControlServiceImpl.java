package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccessControlDao;
import com.summit.service.AccessControlService;
import com.summit.util.LockAuthCtrl;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccessControlServiceImpl implements AccessControlService {

    @Autowired
    private AccessControlDao accessControlDao;

    /**
     * 根据门禁id查询唯一门禁信息
     * @param accessControlId 门禁id
     * @return 唯一门禁信息对象
     */
    @Override
    public AccessControlInfo selectAccCtrlById(String accessControlId) {
        if(accessControlId == null){
            log.error("门禁id为空");
            return null;
        }
        return accessControlDao.selectAccCtrlById(accessControlId, LockAuthCtrl.getRoles());
    }

    /**
     * 根据门禁id查询唯一门禁信息,不考虑权限
     * @param accessControlId 门禁id
     * @return 唯一门禁信息对象
     */
    @Override
    public AccessControlInfo selectAccCtrlByIdBeyondAuthority(String accessControlId) {
        if(accessControlId == null){
            log.error("门禁id为空");
            return null;
        }
        return accessControlDao.selectById(accessControlId);
    }

    /**
     * 根据锁编号查询唯一门禁信息
     * @param lockCode 锁编号
     * @return 唯一门禁信息对象
     */
    @Override
    public AccessControlInfo selectAccCtrlByLockCode(String lockCode) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        return accessControlDao.selectAccCtrlByLockCode(lockCode, LockAuthCtrl.getRoles());
    }

    /**
     * 分页查询全部门禁信息
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public Page<AccessControlInfo> selectAccCtrlByPage(SimplePage page) {

        List<AccessControlInfo> accessControls = accessControlDao.selectCondition(new AccessControlInfo(), null, LockAuthCtrl.getRoles());
        int rowsCount = accessControls == null ? 0 : accessControls.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        List<AccessControlInfo> accessControlInfos = accessControlDao.selectCondition(new AccessControlInfo(), page, LockAuthCtrl.getRoles());
        Page<AccessControlInfo> backPage = new Page<>();
        backPage.setContent(accessControlInfos);
        backPage.setPageable(pageable);
        return backPage;
    }

    /**
     * 分页查询全部有操作记录的门禁信息
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public Page<AccessControlInfo> selectHaveHistoryByPage(SimplePage page) {
        //转换当前页之前设置当前页
        List<String> roles = LockAuthCtrl.getRoles();
        Integer rowsCount = accessControlDao.selectHaveHistoryCountByPage(null, roles);
        Pageable pageable = PageConverter.getPageable(page,rowsCount);
        PageConverter.convertPage(page);
        List<AccessControlInfo> accessControlInfos = accessControlDao.selectHaveHistoryByPage(page, roles);
        Page<AccessControlInfo> backPage = new Page<>();
        backPage.setContent(accessControlInfos);
        backPage.setPageable(pageable);
        return backPage;
    }

    /**
     * 条件查询门禁信息
     * @param accessControlInfo 门禁信息对象
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public List<AccessControlInfo> selectCondition(AccessControlInfo accessControlInfo, SimplePage page) {
        PageConverter.convertPage(page);
        if(accessControlInfo == null){
            accessControlInfo = new AccessControlInfo();
        }
        return accessControlDao.selectCondition(accessControlInfo, page, LockAuthCtrl.getRoles());
    }

    /**
     * 插入门禁信息
     * @param accessControlInfo 门禁信息对象
     * @return 返回不为-1则为成功
     */
    @Override
    public int insertAccCtrl(AccessControlInfo accessControlInfo) {
        if(accessControlInfo == null){
            log.error("门禁信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        return accessControlDao.insert(accessControlInfo);
    }

    /**
     * 更新门禁信息
     * @param accessControlInfo 门禁信息对象
     * @return 返回不为-1则为成功
     */
    @Override
    public int updateAccCtrl(AccessControlInfo accessControlInfo) {
        if(accessControlInfo == null){
            log.error("门禁信息为空");
            return CommonConstants.UPDATE_ERROR;
        }

        UpdateWrapper<AccessControlInfo> updateWrapper = new UpdateWrapper<>();
        return accessControlDao.update(accessControlInfo, updateWrapper.eq("access_control_id", accessControlInfo.getAccessControlId()));
    }

    /**
     * 根据id删除门禁信息
     * @param accessControlId 门禁id
     * @return 返回不为-1则为成功
     */
    @Override
    public int delAccCtrlByAccCtrlId(String accessControlId) {
        if(accessControlId == null){
            log.error("门禁id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<AccessControlInfo> wrapper = new UpdateWrapper<>();
        return accessControlDao.delete(wrapper.eq("access_control_id",accessControlId));
    }

    /**
     * 根据锁编号删除门禁信息
     * @param lockCode 锁编号
     * @return 返回不为-1则为成功
     */
    @Override
    public int delAccCtrlByLockCode(String lockCode) {
        if(lockCode == null){
            log.error("锁编号为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<AccessControlInfo> wrapper = new UpdateWrapper<>();
        return accessControlDao.delete(wrapper.eq("lock_code",lockCode));
    }
}
