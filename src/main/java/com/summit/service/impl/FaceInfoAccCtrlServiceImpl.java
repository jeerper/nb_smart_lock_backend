package com.summit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;
import com.summit.dao.repository.FaceInfoAccCtrlDao;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.service.AccessControlService;
import com.summit.service.DeptsService;
import com.summit.service.FaceInfoAccCtrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @param accCtrlId
     * @return 人脸信息列表
     */
    @Override
    public List<FaceInfoAccCtrl> selectFaceInfoAccCtrlByActrlId(String accCtrlId) {
        if(accCtrlId==null){
            log.error("门禁id为空");
            return null;
        }
        return faceInfoAccCtrlDao.selectList(Wrappers.<FaceInfoAccCtrl>lambdaQuery().eq(FaceInfoAccCtrl::getAccessControlId,accCtrlId));
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
    public List<FaceInfo> selectAllFaceByDeptId(List<String> deptIds) {
        List<FaceInfo> faceInfos =null;
        if (deptIds.size()>1){
            List<String> depts=new ArrayList<>();
            for(String deptId:deptIds){
                depts.add(deptId);
            }
            faceInfos= faceInfoManagerDao.selectAllFaceByDeptId(depts);
        }else {
            String dept = deptIds.get(0);
            JSONObject paramJson=new JSONObject();
            paramJson.put("pdept",dept);
            List<String> depts = deptsService.getDeptsByPdept(paramJson);
            faceInfos= faceInfoManagerDao.selectAllFaceByDeptId(depts);
        }
        return faceInfos;
    }
}
