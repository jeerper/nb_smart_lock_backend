package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.FaceInfoAccCtrl;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2019/8/29.
 */
public interface FaceInfoAccCtrlDao extends BaseMapper<FaceInfoAccCtrl> {
    int selectCountAccCtrlIdByFaceIdAndAccessControlId(@Param("faceid") String faceid,@Param("accessControlId") String accessControlId);
}
