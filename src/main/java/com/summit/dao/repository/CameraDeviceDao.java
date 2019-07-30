package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.CameraDevice;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CameraDeviceDao  extends BaseMapper<CameraDevice> {

    CameraDevice selectById(String devId);

    List<CameraDevice> selectCondition(CameraDevice cameraDevice);
}