package com.summit.service;

import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.Page;

import java.util.Date;
import java.util.List;

public interface FaceInfoService {

    int insertFaceInfo(FaceInfo faceInfo);

    int updateFaceInfo(FaceInfo faceInfo);

    int delFaceInfoById(String faceId);

    int delFaceInfoByUserName(String userName);

    FaceInfo selectFaceInfoById(String faceId);

    FaceInfo selectByUserName(String userName);

    FaceInfo selectByUserId(String userId);

    List<FaceInfo> selectAll(Page page);

    List<FaceInfo> selectCondition(FaceInfo faceInfo,Page page);

    List<FaceInfo> selectCondition(FaceInfo faceInfo, Date start, Date end, Page page);


}
