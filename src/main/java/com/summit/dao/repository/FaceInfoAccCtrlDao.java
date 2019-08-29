package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.FaceInfoAccCtrl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2019/8/29.
 */
public interface FaceInfoAccCtrlDao extends BaseMapper<FaceInfoAccCtrl> {
    List<String> seletAccCtrlIdByFaceId(@Param("faceid") String faceid);
}
