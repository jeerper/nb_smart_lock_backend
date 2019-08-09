package com.summit.service;

import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.Page;

import java.util.List;

/**
 * 摄像头设备信息service接口
 */
public interface CameraDeviceService {

    /**
     * 根据摄像头设备id查询唯一确定设备
     * @param devId 设备id
     * @return 设备对象
     */
    CameraDevice selectDeviceById(String devId);

    /**
     * 根据摄像头设备ip地址查询唯一确定设备
     * @param deviceIp 设备ip地址
     * @return 设备对象
     */
    CameraDevice selectDeviceByIpAddress(String deviceIp);

    /**
     * 根据锁编号查询多个设备
     * @param lockCode 锁编号
     * @param page 分页对象
     * @return 设备列表
     */
    List<CameraDevice> selectDeviceByLockCode(String lockCode,Page page);

    /**
     * 条件查询多个设备
     * @param cameraDevice 设备对象
     * @param page 分页对象
     * @return 设备列表
     */
    List<CameraDevice> selectCondition(CameraDevice cameraDevice,Page page);

    /**
     * 分页查询所有设备
     * @param page 分页对象
     * @return 设备列表
     */
    List<CameraDevice> selectAll(Page page);

    /**
     * 插入摄像头信息
     * @param cameraDevice 设备对象
     * @return 不为-1则为成功
     */
    int insertDevice(CameraDevice cameraDevice);

    /**
     * 更新摄像头信息
     * @param cameraDevice 设备对象
     * @return 不为-1则为成功
     */
    int updateDevice(CameraDevice cameraDevice);

    /**
     * 根据设备id地址删除
     * @param devId 设备id
     * @return 不为-1则为成功
     */
    int delDeviceByDevId(String devId);

    /**
     * 根据设备ip地址删除
     * @param deviceIp 设备ip地址
     * @return 不为-1则为成功
     */
    int delDeviceByIpAddress(String deviceIp);
}
