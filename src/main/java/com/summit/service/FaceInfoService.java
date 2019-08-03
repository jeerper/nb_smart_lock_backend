package com.summit.service;

import com.summit.dao.entity.FaceInfoEntity;
import com.summit.dao.entity.Page;

import java.util.Date;
import java.util.List;

public interface FaceInfoService {

    int insertFaceInfo(FaceInfoEntity faceInfo);

    int updateFaceInfo(FaceInfoEntity faceInfo);

    int delFaceInfoById(String faceId);

    int delFaceInfoByUserName(String userName);

    FaceInfoEntity selectFaceInfoById(String faceId);

    FaceInfoEntity selectByUserName(String userName);

    FaceInfoEntity selectByUserId(String userId);

    List<FaceInfoEntity> selectAll(Page page);

    List<FaceInfoEntity> selectCondition(FaceInfoEntity faceInfo, Page page);

    List<FaceInfoEntity> selectCondition(FaceInfoEntity faceInfo, Date start, Date end, Page page);


}
