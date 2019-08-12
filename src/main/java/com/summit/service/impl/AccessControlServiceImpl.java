package com.summit.service.impl;

import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.Page;
import com.summit.dao.repository.AccessControlDao;
import com.summit.service.AccessControlService;
import com.summit.util.LockAuthCtrl;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
    public List<AccessControlInfo> selectAll(Page page) {
        PageConverter.convertPage(page);
        return accessControlDao.selectCondition(new AccessControlInfo(), page, LockAuthCtrl.getRoles());
    }

    /**
     * 分页查询全部有操作记录的门禁信息
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public List<AccessControlInfo> selectAllHaveHistory(Page page) {
        PageConverter.convertPage(page);
        return accessControlDao.selectAllHaveHistory(page, LockAuthCtrl.getRoles());
    }

    /**
     * 条件查询门禁信息
     * @param accessControlInfo 门禁信息对象
     * @param page 分页对象
     * @return 门禁信息列表
     */
    @Override
    public List<AccessControlInfo> selectCondition(AccessControlInfo accessControlInfo, Page page) {
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
        return 0;
    }

    /**
     * 更新门禁信息
     * @param accessControlInfo 门禁信息对象
     * @return 返回不为-1则为成功
     */
    @Override
    public int updateAccCtrl(AccessControlInfo accessControlInfo) {
        return 0;
    }

    /**
     * 根据id删除门禁信息
     * @param accessControlId 门禁id
     * @return 返回不为-1则为成功
     */
    @Override
    public int delAccCtrlByAccCtrlId(String accessControlId) {
        return 0;
    }

    /**
     * 根据锁编号删除门禁信息
     * @param lockCode 锁编号
     * @return 返回不为-1则为成功
     */
    @Override
    public int delAccCtrlByLockCode(String lockCode) {
        return 0;
    }
}
