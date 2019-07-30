package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.FaceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FaceInfoDao  extends BaseMapper<FaceInfo> {

    FaceInfo selectById(String faceId);

    FaceInfo selectByName(String userName);

    List<FaceInfo> selectCondition(@Param("faceInfo") FaceInfo faceInfo,
                                @Param("start") Date start,
                                @Param("end") Date end);


}