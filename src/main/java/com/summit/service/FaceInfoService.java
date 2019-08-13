package com.summit.service;

import com.summit.dao.entity.FaceInfoEntity;
import com.summit.dao.entity.SimplePage;

import java.util.Date;
import java.util.List;

/**
 * 人脸信息service接口
 */
public interface FaceInfoService {

    /**
     * 插入人脸信息
     * @param faceInfo 人脸信息对象
     * @return 不为-1则为成功
     */
    int insertFaceInfo(FaceInfoEntity faceInfo);

    /**
     * 更新人脸信息
     * @param faceInfo 人脸信息对象
     * @return 不为-1则为成功
     */
    int updateFaceInfo(FaceInfoEntity faceInfo);

    /**
     * 根据人脸信息id删除
     * @param faceId 人脸信息id
     * @return 不为-1则为成功
     */
    int delFaceInfoById(String faceId);

    /**
     * 根据人脸信息对应姓名删除
     * @param userName 人脸信息对应姓名
     * @return 不为-1则为成功
     */
    int delFaceInfoByUserName(String userName);

    /**
     * 根据id查询唯一人脸信息
     * @param faceId 人脸信息id
     * @return 唯一人脸信息
     */
    FaceInfoEntity selectFaceInfoById(String faceId);

    /**
     * 根据姓名查询唯一人脸信息
     * @param userName 姓名
     * @return 唯一人脸信息
     */
    FaceInfoEntity selectByUserName(String userName);

    /**
     * 根据userId查询唯一人脸信息
     * @param userId 人脸信息对应userId
     * @return 唯一人脸信息
     */
    FaceInfoEntity selectByUserId(String userId);

    /**
     * 分页查询所有人脸信息
     * @param page 分页对象
     * @return 人脸信息列表
     */
    List<FaceInfoEntity> selectAll(SimplePage page);

    /**
     * 条件查询人脸信息，不带时间重载
     * @param faceInfo 人脸信息对象
     * @param page 分页对象
     * @return 人脸信息列表
     */
    List<FaceInfoEntity> selectCondition(FaceInfoEntity faceInfo, SimplePage page);

    /**
     * 条件查询人脸信息,可指定时间段
     * @param faceInfo 人脸信息对象
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 人脸信息列表
     */
    List<FaceInfoEntity> selectCondition(FaceInfoEntity faceInfo, Date start, Date end, SimplePage page);

}
