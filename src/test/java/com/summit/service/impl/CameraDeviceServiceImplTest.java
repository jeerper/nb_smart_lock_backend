package com.summit.service.impl;

import com.summit.dao.entity.CameraDevice;
import com.summit.dao.entity.Page;
import com.summit.service.CameraDeviceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CameraDeviceServiceImplTest {

    @Autowired
    private CameraDeviceService cameraDeviceService;
    Page page = new Page(1, 3);
    @Test
    public void insertDevice() {
        CameraDevice cameraDevice = new CameraDevice("did02","devip02","lid04","lc04",1);

        cameraDeviceService.insertDevice(cameraDevice);

    }

    @Test
    public void updateDevice() {
    }

    @Test
    public void delDeviceByDevId() {
    }

    @Test
    public void delDeviceByIpAddress() {

    }

    @Test
    public void selectDeviceById() {
    }

    @Test
    public void selectAll() {
        List<CameraDevice> cameraDevices = cameraDeviceService.selectAll(page);
        System.out.println(cameraDevices);

    }

    @Test
    public void selectDeviceByIpAddress() {
        CameraDevice cameraDevice = cameraDeviceService.selectDeviceByIpAddress("192.168.141.141");
        System.out.println(cameraDevice);
    }

    @Test
    public void selectDeviceByLockCode() {
    }

    @Test
    public void selectCondition() {
    }
}