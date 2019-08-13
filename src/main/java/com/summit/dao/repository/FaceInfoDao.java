package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.FaceInfoEntity;
import com.summit.dao.entity.SimplePage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FaceInfoDao  extends BaseMapper<FaceInfoEntity> {

    FaceInfoEntity selectFaceById(String faceId);

    FaceInfoEntity selectByName(String userName);

    FaceInfoEntity selectByUserId(String userId);

    List<FaceInfoEntity> selectCondition(@Param("faceInfo") FaceInfoEntity faceInfo,
                                         @Param("start") Date start,
                                         @Param("end") Date end,
                                         @Param("page") SimplePage page);

    int insertFace(FaceInfoEntity faceInfo);

    int updateFace(FaceInfoEntity faceInfo);
}