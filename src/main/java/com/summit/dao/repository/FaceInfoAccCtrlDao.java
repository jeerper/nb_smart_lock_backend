package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.FaceInfoAccCtrl;
import org.apache.ibatis.annotations.Param;

/**
 * 门禁和人脸授权信息关联表Dao
 * Created by Administrator on 2019/8/29.
 */
public interface FaceInfoAccCtrlDao extends BaseMapper<FaceInfoAccCtrl> {
    int selectCountAccCtrlIdByFaceIdAndAccessControlId(@Param("faceid") String faceid,@Param("accessControlId") String accessControlId);
    int selectCountByFaceIdAndLockCode(@Param("faceId") String faceid,@Param("lockCode") String lockCode);
}
