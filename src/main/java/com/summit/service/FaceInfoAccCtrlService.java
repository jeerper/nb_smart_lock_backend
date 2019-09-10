package com.summit.service;

import com.summit.dao.entity.FaceInfoAccCtrl;

import java.util.List;

/**
 * Created by Administrator on 2019/8/29.
 */
public interface FaceInfoAccCtrlService {
    /**
     * 批量刷新指定人脸关联的门禁",notes = "为指定的人脸信息更新门禁权限，所传的人脸信息之前没有关联某门禁且所传列表中有添加，之前已关联过门禁而所传列表中有则不添加，之前已关联过门禁而所传列表中没有则删除
     * @param accessControlId  门禁id
     * @param faceids 人脸关联的所有门禁的id
     * @return 返回-1则为不成功
     */
    int authorityFaceInfoAccCtrl(String accessControlId, List<String> faceids);

    /**
     * 根据门禁id查询所有的人脸信息列表
     * @param accCtrlId
     * @return 人脸信息列表
     */
    List<FaceInfoAccCtrl> selectFaceInfoAccCtrlByActrlId(String accCtrlId);

    /**
     * 根据人脸名称和身份证号查询门禁的id
     * @param userName 人脸名称
     * @param cardId 身份证号码
     * @return  返回门禁的id列表
     */
    List<String> selectAccCtrlIdByUserNameAndCardId(String userName,String cardId);
}