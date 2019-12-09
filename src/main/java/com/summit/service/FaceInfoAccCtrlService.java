package com.summit.service;

import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;

import java.util.List;

/**
 * Created by Administrator on 2019/8/29.
 */
public interface FaceInfoAccCtrlService {

    /**
     * 根据门禁id查询所有的人脸信息列表
     * @param accCtrlId
     * @return 人脸信息列表
     */
    List<FaceInfoAccCtrl> selectFaceInfoAccCtrlByActrlId(String accCtrlId);

    /**
     * 根据人脸名称和身份证号查询门禁的id
     * @param userName 人脸名称
     * @param accessControlId 门禁ID
     * @return  返回门禁的id列表
     */
    int selectAccCtrlIdByUserNameAndAccessControlId(String userName,String accessControlId);


    /**
     * 根据人脸id查询当前人脸已经关联的门禁信息列表
     * @param faceid
     * @return 返回门禁信息列表
     */
    List<AccessControlInfo> seleAccCtrlInfoByFaceID(String faceid);


    /**
     * 根据人脸id 删除门禁授权记录
     * @param faceid
     * @return -1删除不成功
     */
    int deleteFaceAccCtrlByFaceId(String faceid);

}
