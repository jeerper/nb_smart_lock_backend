package com.summit.service;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.AccCtrlProcess;
import com.summit.dao.entity.SimplePage;

import java.util.Date;
import java.util.List;

/**
 * 门禁操作记录service
 */
public interface AccCtrlProcessService {
    /**
     * 门禁操作记录插入
     * @param accCtrlProcess 门禁操作记录
     * @return 不为-1则成功
     */
    int insertAccCtrlProcess(AccCtrlProcess accCtrlProcess);

    /**
     * 门禁操作记录更新
     * @param accCtrlProcess 门禁操作记录
     * @return 不为-1则成功
     */
    int updateAccCtrlProcess(AccCtrlProcess accCtrlProcess);

    /**
     * 门禁操作记录删除
     * @param accCtrlProId 门禁操作记录id
     * @return 不为-1则成功
     */
    int delAccCtrlProcess(String accCtrlProId);

    /**
     * 门禁操作记录批量删除
     * @param accCtrlProIds 门禁操作记录id列表
     * @return 不为-1则成功
     */
    int delAccCtrlProcessByIdBatch(List<String> accCtrlProIds);


    //###门禁操作记录查询------------------------------

    /**
     * 查询所有门禁操作记录
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    List<AccCtrlProcess> selectAll(SimplePage page);

    /**
     * 根据Id查询
     * @param accCtrlProId 门禁操作记录id
     * @return 唯一确定门禁操作记录
     */
    AccCtrlProcess selectAccCtrlProcessById(String accCtrlProId);

    /**
     * 根据门禁id查询，可指定时间段
     * @param accessControlId 门禁id
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    List<AccCtrlProcess>  selectAccCtrlProcessByAccCtrlId(String accessControlId, Date start, Date end, SimplePage page);

    /**
     * 根据门禁id查询，不带时间重载
     * @param accessControlId 门禁id
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    List<AccCtrlProcess>  selectAccCtrlProcessByAccCtrlId(String accessControlId, SimplePage page);


    /**
     * 根据门禁名查询，可指定时间段
     * @param accessControlName 门禁名称
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    List<AccCtrlProcess>  selectAccCtrlProcessByName(String accessControlName, Date start, Date end, SimplePage page);

    /**
     * 根据门禁名查询，不带时间重载
     * @param accessControlName 门禁名称
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    List<AccCtrlProcess>  selectAccCtrlProcessByName(String accessControlName, SimplePage page);

    /**
     * 根据锁编号查询，可指定时间段
     * @param lockCode 锁编号
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    List<AccCtrlProcess> selectAccCtrlProcessByLockCode(String lockCode, Date start, Date end, SimplePage page);

    /**
     * 根据锁编号查询，不带时间重载
     * @param lockCode 锁编号
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    List<AccCtrlProcess> selectAccCtrlProcessByLockCode(String lockCode, SimplePage page);

    /**
     * 根据操作类型查询（开门禁或关门禁），可指定时间段
     * @param processType 门禁操作类型（开门禁或关门禁）
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    List<AccCtrlProcess> selectAccCtrlProcessByType(Integer processType, Date start, Date end, SimplePage page);

    /**
     * 根据操作类型查询（开门禁或关门禁），不带时间重载
     * @param processType 门禁操作类型（开门禁或关门禁）
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    List<AccCtrlProcess> selectAccCtrlProcessByType(Integer processType, SimplePage page);


    /**
     * 指定条件查询，可指定时间段
     * @param accCtrlProcess 门禁操作记录对象
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    Page<AccCtrlProcess> selectAccCtrlProcessCondition(AccCtrlProcess accCtrlProcess, Date start, Date end, SimplePage page);

    /**
     * 指定条件查询，不带日期的重载
     * @param accCtrlProcess 门禁操作记录对象
     * @param page 分页对象
     * @return 门禁操作记录列表
     */
    Page<AccCtrlProcess> selectAccCtrlProcessCondition(AccCtrlProcess accCtrlProcess, SimplePage page);
}
