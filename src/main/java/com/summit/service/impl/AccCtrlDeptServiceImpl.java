package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.AccCtrlDeptDao;
import com.summit.entity.AccCtrlDept;
import com.summit.service.AccCtrlDeptService;
import com.summit.util.CommonUtil;
import com.summit.util.MybatisPage;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class AccCtrlDeptServiceImpl  implements AccCtrlDeptService {

    @Autowired
    private AccCtrlDeptDao accCtrlDeptDao;
    /**
     * @param accessControlIds  门禁列表
     * @param deptId 部门id
     * @return 返回不为-1则为失败
     */
    @Override
    public int refreshAccCtrlDeptBatch(List<String> accessControlIds, String deptId) {
        if(accessControlIds == null || deptId == null){
            return CommonConstants.UPDATE_ERROR;
        }
        //查出部门关联的门禁
        List<AccCtrlDept> accCtrlDepts=selectAccCtrlDeptsByDeptId(deptId);
        if(accCtrlDepts != null && !accCtrlDepts.isEmpty()){
            //若传入列表为空集合，说明需要删除所有授权
            if(accessControlIds.isEmpty()){
                List<String> authIds = new ArrayList<>();
                for(AccCtrlDept accCtrlDept : accCtrlDepts){
                    if(accCtrlDept == null)
                        continue;
                    authIds.add(accCtrlDept.getId());
                }
                return accCtrlDeptDao.deleteBatchIds(authIds);
            }
            //先删除数据库在传入列表中找不到的门禁授权
            for(AccCtrlDept accCtrlDept : accCtrlDepts) {
                if(accCtrlDept == null)
                    continue;
                if(!accessControlIds.contains(accCtrlDept.getAccessControlId())){
                    accCtrlDeptDao.deleteById(accCtrlDept.getId());
                }
            }
            //再添加传入列表在数据库在中找不到的门禁授权
            for(String acId : accessControlIds) {
                boolean needAdd = true;
                for(AccCtrlDept accCtrlDept : accCtrlDepts) {
                    if(acId != null && acId.equals(accCtrlDept.getAccessControlId())){
                        needAdd = false;
                        break;
                    }
                }
                if(needAdd){
                    accCtrlDeptDao.insert(new AccCtrlDept(null,deptId,acId));
                }
            }

        }else {
            //若之前无此角色对应的门禁权限，则直接添加
            List<AccCtrlDept>  acctrlDepts = new ArrayList<>();
            for(String accessControlId : accessControlIds) {
                acctrlDepts.add(new AccCtrlDept(null,deptId,accessControlId));
            }
            return insertAccCtrlDeptBatch(acctrlDepts);
        }
        return 0;
    }

    @Override
    public Page<AccCtrlDept> selectAccCtrlDeptsByPage(SimplePage page) {
        QueryWrapper<AccCtrlDept> wrapper = new QueryWrapper<>();
        List<AccCtrlDept> accCtrlDepts = accCtrlDeptDao.selectList(wrapper);
        int rowsCount = accCtrlDepts == null ? 0 : accCtrlDepts.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        page = PageConverter.filterPage(page);
        IPage<AccCtrlDept> tempPage = new MybatisPage<>();
        if(page != null){
            tempPage = new MybatisPage<>(page.getCurrent(),page.getPageSize());
        }
        IPage<AccCtrlDept> accCtrlDeptIPage = accCtrlDeptDao.selectPage(tempPage, wrapper);
        List<AccCtrlDept> records = null;
        if(accCtrlDeptIPage != null)
            records = accCtrlDeptIPage.getRecords();
        Page<AccCtrlDept> backPage = new Page<>(records,pageable);
        return backPage;
    }

    @Override
    public List<AccCtrlDept> selectAccCtrlInfoByDeptId(String deptId) {
        if(deptId==null){
            log.error("部门id为空");
            return null;
        }
        return accCtrlDeptDao.selectList(Wrappers.<AccCtrlDept>lambdaQuery().eq(AccCtrlDept::getDeptId,deptId));
    }


    private int insertAccCtrlDeptBatch(List<AccCtrlDept> acctrlDepts) {
        if(CommonUtil.isEmptyList(acctrlDepts)){
            log.error("门禁部门权限对象数组为空");
            return CommonConstants.UPDATE_ERROR;
        }
        int result = 0;
        int failCount = 0;
        for(AccCtrlDept accCtrlDept : acctrlDepts) {
            try {
                if(accCtrlDeptDao.insert(accCtrlDept) == CommonConstants.UPDATE_ERROR){
                    failCount++;
                }
            } catch (Exception e) {
                log.error("为部门添加门禁{}时失败",accCtrlDept.getAccessControlId());
            }
        }
        //全部失败才算失败
        if(failCount == acctrlDepts.size())
            result = CommonConstants.UPDATE_ERROR;
        return result;
    }

    /**
     * 根据部门id查询所有门禁部门权限
     * @param deptId 部门id
     * @return 门禁角色权限列表
     */
    public List<AccCtrlDept> selectAccCtrlDeptsByDeptId(String deptId) {
        if(deptId == null){
            log.error("角色code为空");
            return null;
        }
        QueryWrapper<AccCtrlDept> wrapper = new QueryWrapper<>();
        return accCtrlDeptDao.selectList(wrapper.eq("dept_id",deptId));
    }

}
