package com.summit.service;

import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.Page;

import java.util.List;

public interface CameraDeviceService {

    CameraDevice selectDeviceById(String devId);

    CameraDevice selectDeviceByIpAddress(String deviceIp);

    List<CameraDevice> selectDeviceByLockCode(String lockCode,Page page);

    List<CameraDevice> selectCondition(CameraDevice cameraDevice,Page page);

    List<CameraDevice> selectAll(Page page);

    int insertDevice(CameraDevice cameraDevice);

    int updateDevice(CameraDevice cameraDevice);

    int delDeviceByDevId(String devId);

    int delDeviceByIpAddress(String deviceIp);
}
