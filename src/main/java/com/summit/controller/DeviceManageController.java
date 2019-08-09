package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.service.CameraDeviceService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceManageController {

    @Autowired
    private CameraDeviceService cameraDeviceService;

    public RestfulEntityBySummit<CameraDeviceService> selectDeviceById(){

        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "",null);
    }
}
