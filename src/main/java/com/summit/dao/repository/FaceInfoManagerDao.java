package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.FaceInfo;

import java.util.List;

/**
 * Created by Administrator on 2019/8/21.
 */
public interface FaceInfoManagerDao extends BaseMapper<FaceInfo> {
    int insertFaceInfo(FaceInfo faceInfo);

}
