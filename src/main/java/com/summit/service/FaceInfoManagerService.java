package com.summit.service;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.entity.FaceInfoManagerEntity;

import java.util.List;

/**
 * Created by Administrator on 2019/8/21.
 */

/**
 * 人脸信息service接口
 */
public interface FaceInfoManagerService {

    /**
     * 人脸信息插入
     * @param faceInfo 人脸信息
     * @return 不为-1则成功
     */
    int insertFaceInfo(FaceInfo faceInfo);

    /**
     * 根据id批量删除人脸信息
     * @param faceInfoIds
     * @return 返回-1则为不成功
     */
    int delFaceInfoByIds(List<String> faceInfoIds);

    /**
     * 分页查询人脸信息
     * @param faceInfoManagerEntity 人脸对象
     * @param  simplePage 分页对象
     * @return  人脸信息列表分页对象
     */
    Page<FaceInfo> selectFaceInfoByPage(FaceInfoManagerEntity faceInfoManagerEntity, SimplePage simplePage);

    /**
     * 更新人脸信息
     * @param faceInfo 需要修改的人脸信息对象
     * @return 返回-1则为不成功
     */
    int updateFaceInfo(FaceInfo faceInfo);

    /**
     * 更具faceid查询唯一一条人脸信息
     * @param faceid
     * @return 唯一确定的人脸信息记录
     */
    FaceInfo selectFaceInfoByID(String faceid);
}
