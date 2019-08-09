package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.Page;
import com.summit.dao.repository.CameraDeviceDao;
import com.summit.dao.repository.LockInfoDao;
import com.summit.service.CameraDeviceService;
import com.summit.util.LockAuthCtrl;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CameraDeviceServiceImpl implements CameraDeviceService {

    @Autowired
    private CameraDeviceDao cameraDeviceDao;
    @Autowired
    private LockInfoDao lockInfoDao;

    /**
     * 插入摄像头信息
     * @param cameraDevice 设备对象
     * @return 不为-1则为成功
     */
    @Override
    public int insertDevice(CameraDevice cameraDevice) {
        if(cameraDevice == null){
            log.error("设备信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        if(lockInfoDao.selectBylockCode(cameraDevice.getLockCode(),roles) == null){
            log.warn("所插入的锁编号在锁信息表中不存在");
        }
        return cameraDeviceDao.insert(cameraDevice);
    }

    /**
     * 更新摄像头信息
     * @param cameraDevice 设备对象
     * @return 不为-1则为成功
     */
    @Override
    public int updateDevice(CameraDevice cameraDevice) {
        if(cameraDevice == null){
            log.error("设备信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        String devId = cameraDevice.getDevId();
        String deviceIp = cameraDevice.getDeviceIp();
        if(devId != null &&  deviceIp!= null){
            deviceIp = null;
        }
        List<String> roles = LockAuthCtrl.getRoles();
        if(lockInfoDao.selectBylockCode(cameraDevice.getLockCode(),roles) == null){
            log.warn("所更新的锁编号在锁信息表中不存在");
        }
        UpdateWrapper<CameraDevice> updateWrapper = new UpdateWrapper<>();
        return cameraDeviceDao.update(cameraDevice, updateWrapper.eq("device_ip", deviceIp)
        .or().eq("dev_id", devId));
    }

    /**
     * 根据设备id地址删除
     * @param devId 设备id
     * @return 不为-1则为成功
     */
    @Override
    public int delDeviceByDevId(String devId) {
        if(devId == null){
            log.error("设备id地址为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<CameraDevice> wrapper = new UpdateWrapper<>();
        return cameraDeviceDao.delete(wrapper.eq("dev_id", devId));
    }

    /**
     * 根据设备ip地址删除
     * @param deviceIp 设备ip地址
     * @return 不为-1则为成功
     */
    @Override
    public int delDeviceByIpAddress(String deviceIp) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<CameraDevice> wrapper = new UpdateWrapper<>();
        return cameraDeviceDao.delete(wrapper.eq("device_ip", deviceIp));
    }

    /**
     * 根据摄像头设备id查询唯一确定设备
     * @param devId 设备id
     * @return 设备对象
     */
    @Override
    public CameraDevice selectDeviceById(String devId) {
        if(devId == null){
            log.error("设备id为空");
            return null;
        }
        return cameraDeviceDao.selectDeviceById(devId);
    }

    /**
     * 根据摄像头设备ip地址查询唯一确定设备
     * @param deviceIp 设备ip地址
     * @return 设备对象
     */
    @Override
    public CameraDevice selectDeviceByIpAddress(String deviceIp) {
        if(deviceIp == null){
            log.error("设备ip地址为空");
            return null;
        }
        CameraDevice cameraDevice = new CameraDevice();
        cameraDevice.setDeviceIp(deviceIp);
        List<CameraDevice> devices = cameraDeviceDao.selectCondition(cameraDevice, null);
        if(devices == null || devices.isEmpty()){
            return null;

        }
        return devices.get(0);
    }

    /**
     * 根据锁编号查询多个设备
     * @param lockCode 锁编号
     * @param page 分页对象
     * @return 设备列表
     */
    @Override
    public List<CameraDevice> selectDeviceByLockCode(String lockCode, Page page) {
        if(lockCode == null){
            log.error("锁编号为空");
            return null;
        }
        PageConverter.convertPage(page);
        CameraDevice cameraDevice = new CameraDevice();
        cameraDevice.setLockCode(lockCode);
        return cameraDeviceDao.selectCondition(cameraDevice, page);
    }

    /**
     * 条件查询多个设备
     * @param cameraDevice 设备对象
     * @param page 分页对象
     * @return 设备列表
     */
    @Override
    public List<CameraDevice> selectCondition(CameraDevice cameraDevice, Page page) {
        PageConverter.convertPage(page);
        if(cameraDevice == null){
            log.error("设备信息为空");
            return null;
        }
        return cameraDeviceDao.selectCondition(cameraDevice, page);
    }

    /**
     * 分页查询所有设备
     * @param page 分页对象
     * @return 设备列表
     */
    @Override
    public List<CameraDevice> selectAll(Page page) {
        PageConverter.convertPage(page);
        return cameraDeviceDao.selectCondition(new CameraDevice(), page);
    }

}
