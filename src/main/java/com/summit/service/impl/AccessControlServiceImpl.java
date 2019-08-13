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
    public List<AccessControlInfo> selectAll(SimplePage page) {
        PageConverter.convertPage(page);
        return accessControlDao.selectCondition(new AccessControlInfo(), page, LockAuthCtrl.getRoles());
    }

    /**
     * 分页查询全部有操作记录的门禁信息
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public Page<AccessControlInfo> selectHaveHistoryByPage(SimplePage page) {
        Pageable pageable = new Pageable();
        //转换当前页之前设置当前页
        pageable.setCurPage(page.getCurrent());
        PageConverter.convertPage(page);
        List<String> roles = LockAuthCtrl.getRoles();
        List<AccessControlInfo> accessControlInfos = accessControlDao.selectHaveHistoryByPage(page, roles);
        Page<AccessControlInfo> backPage = new Page<>();
        backPage.setContent(accessControlInfos);
        Integer pageSize = page.getPageSize();
        pageable.setPageSize(pageSize);
        Integer rowsCount = accessControlDao.selectHaveHistoryCountByPage(null, roles);
        pageable.setRowsCount(rowsCount);
        pageable.setPageCount(PageConverter.getPageCount(pageSize, rowsCount));
        pageable.setPageRowsCount(accessControlInfos == null ? 0 : accessControlInfos.size());
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
