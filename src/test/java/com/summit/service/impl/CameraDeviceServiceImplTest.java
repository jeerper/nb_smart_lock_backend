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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CameraDeviceServiceImplTest {

    @Autowired
    private CameraDeviceService cameraDeviceService;
    Page page = new Page(0, 3);
    @Test
    public void insertDevice() {
        CameraDevice cameraDevice = new CameraDevice("did01","devip01","lc01",2);

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
        CameraDevice cameraDevice = cameraDeviceService.selectDeviceByIpAddress("devip01");
        System.out.println(cameraDevice);
    }

    @Test
    public void selectDeviceByLockCode() {
    }

    @Test
    public void selectCondition() {
    }
}