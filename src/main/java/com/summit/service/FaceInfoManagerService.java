package com.summit.service;

import com.summit.dao.entity.FaceInfo;

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
}
