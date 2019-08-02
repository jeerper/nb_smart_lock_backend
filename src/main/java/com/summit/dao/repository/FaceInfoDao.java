package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FaceInfoDao  extends BaseMapper<FaceInfo> {

    FaceInfo selectFaceById(String faceId);

    FaceInfo selectByName(String userName);

    FaceInfo selectByUserId(String userId);

    List<FaceInfo> selectCondition(@Param("faceInfo") FaceInfo faceInfo,
                                   @Param("start") Date start,
                                   @Param("end") Date end,
                                   @Param("page") Page page);

    int insertFace(FaceInfo faceInfo);

    int updateFace(FaceInfo faceInfo);
}