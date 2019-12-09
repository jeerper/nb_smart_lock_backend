package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;
import com.summit.dao.repository.FaceInfoAccCtrlDao;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.service.AccessControlService;
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

    @Override
    public List<AccessControlInfo> seleAccCtrlInfoByFaceID(String faceid) {
        QueryWrapper<FaceInfoAccCtrl> wrapper=new QueryWrapper<>();
        List<FaceInfoAccCtrl> faceInfoAccCtrls = faceInfoAccCtrlDao.selectList(wrapper.eq("face_id", faceid));
        //System.out.println(faceInfoAccCtrls+"关联门禁");
        List<AccessControlInfo> accessControlInfos=new ArrayList<>();
        for(FaceInfoAccCtrl faceInfoAccCtrl:faceInfoAccCtrls){
            AccessControlInfo accessControlInfo = accessControlService.selectAccCtrlByIdBeyondAuthority(faceInfoAccCtrl.getAccessControlId());
            accessControlInfos.add(accessControlInfo);
        }
        return accessControlInfos;
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
}
