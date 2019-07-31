package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CameraDeviceDao  extends BaseMapper<CameraDevice> {

    CameraDevice selectDeviceById(String devId);

    List<CameraDevice> selectCondition(@Param("cameraDevice") CameraDevice cameraDevice,
                                       @Param("page") Page page);
}