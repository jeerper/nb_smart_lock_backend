package com.summit.service;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.SimplePage;
import org.springframework.web.multipart.MultipartFile;

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
    AccessControlInfo selectAccCtrlById(String accessControlId) throws Exception;

    /**
     * 根据门禁id查询唯一门禁信息,不考虑权限
     * @param accessControlId 门禁id
     * @return 唯一门禁信息对象
     */
    AccessControlInfo selectAccCtrlByIdBeyondAuthority(String accessControlId);

    /**
     * 根据锁编号查询唯一门禁信息
     * @param lockCode 锁编号
     * @return 唯一门禁信息对象
     */
    AccessControlInfo selectAccCtrlByLockCode(String lockCode);

    /**
     * 分页条件查询门禁信息
     * @return 门禁信息列表分页对象
     */
    Page<AccessControlInfo> selectAccCtrlByPage(AccessControlInfo accessControlInfo, Integer current, Integer pageSize,String deptIds) throws Exception;

    /**
     * 分页查询全部有操作记录的门禁信息
     * @param page 分页对象
     * @return 门禁信息列表分页对象
     */
    Page<AccessControlInfo> selectHaveHistoryByPage(SimplePage page);

    /**
     * 根据当前用户查询该用户所属部门下以及子部门下所有的门禁列表
     * @param page 分页对象
     * @return 门禁信息列表
     */
    List<AccessControlInfo> selectAllAccessControl() throws Exception;

    /**
     * 条件查询门禁信息
     * @param accessControlInfo 门禁信息对象
     * @return 门禁信息列表
     */
    List<AccessControlInfo> selectCondition(AccessControlInfo accessControlInfo, Integer current, Integer pageSize);

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
     * 根据id List批量删除门禁信息
     * @param accessControlIds 门禁id列表
     * @return 返回不为-1则为成功
     */
    int delBatchAccCtrlByAccCtrlId(List<String> accessControlIds);

    /**
     * 根据锁编号删除门禁信息
     * @param lockCode 锁编号
     * @return 返回不为-1则为成功
     */
    int delAccCtrlByLockCode(String lockCode);

    /**
     * 根据用户名查询所关联的门禁信息列表
     * @param userName 登录名
     * @return
     */
    List<AccessControlInfo> selectAccCtrlInfosByUserName(String userName);

    boolean batchImport(MultipartFile uploadFile) throws Exception;

    List<AccessControlInfo> loginAccessControlInfoExport(AccessControlInfo accessControlInfo);

}
