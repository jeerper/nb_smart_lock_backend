package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.SimplePage;
import com.summit.entity.FaceInfoManagerEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2019/8/21.
 */
public interface FaceInfoManagerDao extends BaseMapper<FaceInfo> {
    int insertFaceInfo(FaceInfo faceInfo);
    List<FaceInfo> selectFaceInfoByPage(@Param("faceInfoManagerEntity") FaceInfoManagerEntity faceInfoManagerEntity, @Param("page") SimplePage page);
}
