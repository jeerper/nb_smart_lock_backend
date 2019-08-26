package com.summit.service;

import com.summit.dao.entity.FaceInfo;

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
}
