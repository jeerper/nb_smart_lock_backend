package com.summit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;
import com.summit.dao.repository.FaceInfoAccCtrlDao;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.service.AccessControlService;
import com.summit.service.DeptsService;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/8/29.
 */
@Slf4j
@Service
public class FaceInfoAccCtrlServiceImpl implements FaceInfoAccCtrlService {

    @Autowired
    private FaceInfoAccCtrlDao faceInfoAccCtrlDao;
    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;
    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private DeptsService deptsService;

    /**
     * 根据门禁id查询人脸信息列表
     * @param accCtrlIds
     * @return 人脸信息列表
     */
    @Override
    public List<FaceInfo> selectFaceInfoAccCtrlByActrlIds(List<String> accCtrlIds) {
        if(CommonUtil.isEmptyList(accCtrlIds)){
            log.error("门禁id为空");
            return null;
        }
        List<FaceInfo> faceInfos=faceInfoAccCtrlDao.selectFaceInfoAccCtrlByActrlIds(accCtrlIds);
        return faceInfos;
    }

    /**
     * 根据人脸名称和身份证号查询门禁的id
     * @param userName 人脸名称
     * @param accessControlId 门禁ID
     * @return 返回门禁的id列表
     */
    @Override
    public int selectAccCtrlIdByUserNameAndAccessControlId(String userName,String accessControlId) {
        if(userName == null){
            log.error("人脸名称或者cardId为空");
            return 0;
        }
        String faceid= faceInfoManagerDao.selectFaceIdByUserName(userName);
        if(faceid==null){
            return 0;
        }
        return faceInfoAccCtrlDao.selectCountAccCtrlIdByFaceIdAndAccessControlId(faceid,accessControlId);
    }

    /**
     * 根据人脸id 删除门禁授权记录
     * @param faceid
     * @return -1删除不成功
     */
    @Override
    public int deleteFaceAccCtrlByFaceId(String faceid) {
        int i = faceInfoAccCtrlDao.delete( Wrappers.<FaceInfoAccCtrl>lambdaQuery().eq(FaceInfoAccCtrl::getFaceid,faceid));
        return i;
    }

    /**
     * 根据部门id查询当前以及子部门下的人脸信息
     * @param deptIds
     * @return
     */
    @Override
    public List<FaceInfo> selectAllFaceByDeptId(List<String> deptIds) throws Exception {
        List<String> dept_ids =new ArrayList<>();
        if (!CommonUtil.isEmptyList(deptIds)){
            for(String deptId:deptIds){
                JSONObject paramJson=new JSONObject();
                paramJson.put("pdept",deptId);
                List<String> depts = deptsService.getDeptsByPdept(paramJson);
                if (!CommonUtil.isEmptyList(depts)){
                    for (String dept_id:depts){
                        dept_ids.add(dept_id);
                    }
                }
            }
        }else {
            String currentDeptService = deptsService.getCurrentDeptService();
            JSONObject paramJson=new JSONObject();
            paramJson.put("pdept",currentDeptService);
            List<String> depts = deptsService.getDeptsByPdept(paramJson);
            if (!CommonUtil.isEmptyList(depts)){
                for (String dept_id:depts){
                    dept_ids.add(dept_id);
                }
            }
        }
        CommonUtil.removeDuplicate(dept_ids);//去重
        if (!CommonUtil.isEmptyList(dept_ids)){
            List<FaceInfo> faceInfos= faceInfoManagerDao.selectAllFaceByDeptId(dept_ids);
            return faceInfos;
        }
        return null;
    }

    @Override
    public List<AccessControlInfo> selectAllAccCtrlByDeptId(List<String> deptIds) throws Exception {
        List<String> dept_ids =new ArrayList<>();
        if (!CommonUtil.isEmptyList(deptIds)){
            for(String deptId:deptIds){
                JSONObject paramJson=new JSONObject();
                paramJson.put("pdept",deptId);
                List<String> depts = deptsService.getDeptsByPdept(paramJson);
                if (!CommonUtil.isEmptyList(depts)){
                    for (String dept_id:depts){
                        dept_ids.add(dept_id);
                    }
                }
            }
        }else {
            String currentDeptService = deptsService.getCurrentDeptService();
            JSONObject paramJson=new JSONObject();
            paramJson.put("pdept",currentDeptService);
            List<String> depts = deptsService.getDeptsByPdept(paramJson);
            if (!CommonUtil.isEmptyList(depts)){
                for (String dept_id:depts){
                    dept_ids.add(dept_id);
                }
            }

        }
        CommonUtil.removeDuplicate(dept_ids);//去重
        if (!CommonUtil.isEmptyList(dept_ids)){
            List<AccessControlInfo> accessControlInfos= faceInfoAccCtrlDao.selectAllAccCtrlByDeptId(dept_ids);
            return accessControlInfos;
        }
        return null;

    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int refreshAccCtrlFaceBatch(List<String> accessControlIds, List<String> faceids) throws Exception {
        for (String accessControlId:accessControlIds){
            // 查出门禁关联的人脸
            List<FaceInfoAccCtrl> faceInfoAccCtrls=selectFaceInfoAccCtrlsByAccCtrlId(accessControlId);
            if(faceInfoAccCtrls != null && !faceInfoAccCtrls.isEmpty()){
               /* //若传入列表为空集合，说明需要删除所有授权
                if(faceids.isEmpty()){
                    List<String> authIds = new ArrayList<>();
                    for(FaceInfoAccCtrl faceInfoAccCtrl : faceInfoAccCtrls){
                        if(faceInfoAccCtrl == null)
                            continue;
                        authIds.add(faceInfoAccCtrl.getId());
                    }
                     faceInfoAccCtrlDao.deleteBatchIds(authIds);
                }*/
                //先删除数据库在传入列表中找不到的门禁授权
                /*for(FaceInfoAccCtrl faceInfoAccCtrl : faceInfoAccCtrls) {
                    if(faceInfoAccCtrl == null)
                        continue;
                    if(!faceids.contains(faceInfoAccCtrl.getFaceid())){
                        faceInfoAccCtrlDao.deleteById(faceInfoAccCtrl.getId());
                    }
                }*/
                //再添加传入列表在数据库在中找不到的门禁授权
                for(String faceid : faceids) {
                    boolean needAdd = true;
                    for(FaceInfoAccCtrl faceInfoAccCtrl : faceInfoAccCtrls) {
                        if(faceid != null && faceid.equals(faceInfoAccCtrl.getFaceid())){
                            needAdd = false;
                            break;
                        }
                    }
                    if(needAdd){
                        faceInfoAccCtrlDao.insert(new FaceInfoAccCtrl(null, accessControlId, faceid,"0"));
                    }
                }
            }else {
                //若之前无此角色对应的门禁权限，则直接添加
                List<FaceInfoAccCtrl>  faceInfoAccCtrlList = new ArrayList<>();
                for(String faceid : faceids) {
                    faceInfoAccCtrlList.add(new FaceInfoAccCtrl(null,accessControlId,faceid,"0"));
                }
                insertFaceInfoAccCtrlBatch(faceInfoAccCtrlList);
            }
        }
        return 0;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void insertFaceInfoAccCtrlBatch(List<FaceInfoAccCtrl> faceInfoAccCtrlList) throws Exception {
        if(CommonUtil.isEmptyList(faceInfoAccCtrlList)){
            log.error("门禁人脸权限对象数组为空");
        }
        for(FaceInfoAccCtrl faceInfoAccCtrl : faceInfoAccCtrlList) {
            try {
                faceInfoAccCtrlDao.insert(faceInfoAccCtrl);
            } catch (Exception e) {
                log.error("为门禁添加人脸{}时失败",faceInfoAccCtrl.getFaceid());
                log.error("为门禁添加人脸失败",e);
                throw new Exception(e.getMessage());
            }
        }
    }

    private List<FaceInfoAccCtrl> selectFaceInfoAccCtrlsByAccCtrlId(String accessControlId) {
        if(accessControlId == null){
            log.error("门禁id魏空");
            return null;
        }
        QueryWrapper<FaceInfoAccCtrl> wrapper = new QueryWrapper<>();
        return faceInfoAccCtrlDao.selectList(wrapper.eq("access_control_id",accessControlId));
    }
}
