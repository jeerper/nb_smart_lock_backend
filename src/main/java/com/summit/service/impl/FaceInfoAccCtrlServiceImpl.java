package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;
import com.summit.dao.repository.FaceInfoAccCtrlDao;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.service.AccessControlService;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.util.CommonUtil;
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
     * 批量刷新指定人脸关联的门禁",notes = "为指定的人脸信息更新门禁权限，所传的人脸信息之前没有关联某门禁且所传列表中有添加，之前已关联过门禁而所传列表中有则不添加，之前已关联过门禁而所传列表中没有则删除
     * @param accessControlId  门禁id
     * @param faceids 人脸关联的所有门禁的id
     * @return 返回-1则为不成功
     */
    @Override
    public int authorityFaceInfoAccCtrl(String accessControlId, List<String> faceids) {
        if (accessControlId==null || faceids==null){
            return CommonConstants.UPDATE_ERROR;
        }
        //查出当前门禁关联的人脸信息
         List<FaceInfoAccCtrl> faceInfoAccCtrls=selectFaceInfoAccCtrlByActrlID(accessControlId);
        System.out.println(faceInfoAccCtrls+"111");
         if(faceInfoAccCtrls !=null && !faceInfoAccCtrls.isEmpty()){
             //若传入集合列表为空，则需要删除所有授权
             if(faceids.isEmpty()){
                 List<String> authids=new ArrayList<>();
                 for(FaceInfoAccCtrl faceInfoAccCtrl:faceInfoAccCtrls){
                     if(faceInfoAccCtrl==null){
                         continue;
                     }
                     authids.add(faceInfoAccCtrl.getId());
                 }
                 return faceInfoAccCtrlDao.deleteBatchIds(authids);
             }
             //先删除数据库在所传入列表找不到的门禁授权
             for(FaceInfoAccCtrl faceInfoAccCtrl:faceInfoAccCtrls){
                 boolean needDel=true;
                 for(String faceid:faceids){
                     if(faceid !=null && faceid.equals(faceInfoAccCtrl.getFaceid())){
                         needDel=false;
                         break;
                     }
                 }
                 if(needDel){
                     faceInfoAccCtrlDao.deleteById(faceInfoAccCtrl.getId());
                 }
             }
             //再添加传入列表在数据库中找不到的门禁授权
             for(String faceid:faceids){
                 boolean needAdd=true;
                 for(FaceInfoAccCtrl faceInfoAccCtrl:faceInfoAccCtrls){
                     if(faceid !=null && faceid.equals(faceInfoAccCtrl.getFaceid())){
                         needAdd=false;
                         break;
                     }
                 }
                 if(needAdd){
                     faceInfoAccCtrlDao.insert(new FaceInfoAccCtrl(null,accessControlId,faceid));
                 }
             }
         }else {
             //若之前此门禁没有对应的人脸信息，则直接添加
             List<FaceInfoAccCtrl> faceInfoCtrls=new ArrayList<>();
             for(String faceid:faceids){
                 faceInfoCtrls.add(new FaceInfoAccCtrl(null,accessControlId,faceid));
             }
             //System.out.println("TestPath"+faceInfoCtrls);
             return insertFaceAccCtrl(faceInfoCtrls);
         }
        return 0;
    }

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
        QueryWrapper<FaceInfoAccCtrl> wrapper=new QueryWrapper<>();
        return faceInfoAccCtrlDao.selectList(wrapper.eq("access_control_id",accCtrlId));
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
     * 根据门禁id删除门禁授权记录
     * @param accessControlId
     * @return -1删除不成功
     */
    public int deleteFaceAccCtrlByAccCtlId(String accessControlId) {
        QueryWrapper<FaceInfoAccCtrl> wrapper=new QueryWrapper<>();
        int i = faceInfoAccCtrlDao.delete(wrapper.eq("access_control_id", accessControlId));
        return i;
    }

    /**
     * 根据人脸id 删除门禁授权记录
     * @param faceid
     * @return -1删除不成功
     */
    @Override
    public int deleteFaceAccCtrlByFaceId(String faceid) {
        QueryWrapper<FaceInfoAccCtrl> wrapper=new QueryWrapper<>();
        int i = faceInfoAccCtrlDao.delete(wrapper.eq("face_id", faceid));
        return i;
    }

    @Override
    public FaceInfoAccCtrl seleAccCtrlInfoByFaceIdAndAccCtlId(String faceid, String accessControlId) {
        QueryWrapper<FaceInfoAccCtrl> wrapper=new QueryWrapper<>();
        FaceInfoAccCtrl faceInfoAccCtrl = faceInfoAccCtrlDao.selectOne(wrapper.eq("face_id", faceid).eq("access_control_id", accessControlId));
        return faceInfoAccCtrl;
    }

    private int insertFaceAccCtrl(List<FaceInfoAccCtrl> faceInfoAccCtrls) {
        if(CommonUtil.isEmptyList(faceInfoAccCtrls)){
            log.error("人脸门禁权限列表为空");
            return CommonConstants.UPDATE_ERROR;
        }
        int result=0;
        int failCount=0;
        for(FaceInfoAccCtrl faceInfoAccCtrl:faceInfoAccCtrls){
            try {
                if(faceInfoAccCtrlDao.insert(faceInfoAccCtrl)==CommonConstants.UPDATE_ERROR){
                    failCount++;
                }
            } catch (Exception e) {
               e.printStackTrace();
               log.error("为门禁添加人脸信息{}失败",faceInfoAccCtrl.getFaceid());
            }
        }
        //全部失败才算失败
        if(failCount==faceInfoAccCtrls.size()){
            result =CommonConstants.UPDATE_ERROR;
        }
        return result;

    }

    /**
     * 根据门禁查询人脸门禁权限
     * @param accessControlId 门禁id
     * @return  人脸门禁权限列表
     */
    public List<FaceInfoAccCtrl> selectFaceInfoAccCtrlByActrlID(String accessControlId){
        if(accessControlId==null){
            log.error("门禁id为空");
            return null;
        }
        QueryWrapper<FaceInfoAccCtrl> wrapper=new QueryWrapper<>();
        return faceInfoAccCtrlDao.selectList(wrapper.eq("access_control_id",accessControlId));
    }
}
