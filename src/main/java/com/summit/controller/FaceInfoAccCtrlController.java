package com.summit.controller;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.FaceInfoAccCtrl;
import com.summit.service.FaceInfoAccCtrlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/8/29.
 */
@Slf4j
@Api(tags = "人脸门禁授权管理接口")
@RestController
@RequestMapping("/faceInfoAccCtrl")
public class FaceInfoAccCtrlController {

    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;

    @ApiOperation(value = "批量刷新指定人脸关联的门禁",notes = "为指定的人脸信息更新门禁权限，所传的人脸信息之前没有关联某门禁且所传列表中有添加，之前已关联过门禁而所传列表中有则不添加，之前已关联过门禁而所传列表中没有则删除")
    @PostMapping("/authorityFaceInfoAccCtrl")
    public RestfulEntityBySummit<String> authorityFaceInfoAccCtrl(@ApiParam(value = "门禁id",required = true) @RequestParam(value = "accessControlId")String accessControlId,
                                                                  @ApiParam(value = "人脸id列表",required = true) @RequestParam(value = "faceids") List<String> faceids){
        if(faceids==null){
            log.error("人脸信息id列表为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸id列表为空",null);
        }
        if(accessControlId==null){
            log.error("门禁信息id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁信息id为空",null);
        }
        int result=faceInfoAccCtrlService.authorityFaceInfoAccCtrl(accessControlId,faceids);
        if(result== CommonConstants.UPDATE_ERROR){
            log.error("人脸门禁授权失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"人脸门禁授权失败",null);
        }
        log.error("人脸门禁授权成功");
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"人脸门禁授权成功",null);
    }

    @ApiOperation(value = "根据门禁id查询已经授权的人脸信息列表",notes = "查询已经和门禁关联的人脸信息列表")
    @GetMapping(value = "/selectFaceInfoByAccCtrlId")
    public RestfulEntityBySummit<List<String>> selectFaceInfoByAccCtrlId(@ApiParam(value = "门禁id")@RequestParam(value = "accCtrlId",required = false)String accCtrlId){
        if(accCtrlId==null){
            log.error("门禁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"门禁id为空",null);
        }
        List<String> ids=new ArrayList<>();
        try {
            List<FaceInfoAccCtrl> faceInfoAccCtrls= faceInfoAccCtrlService.selectFaceInfoAccCtrlByActrlId(accCtrlId);
            if(faceInfoAccCtrls!=null){
                for(FaceInfoAccCtrl faceInfoAccCtrl:faceInfoAccCtrls){
                    ids.add(faceInfoAccCtrl.getFaceid());
                }
            }
        } catch (Exception e) {
           log.error("查询人脸信息列表失败");
           return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询人脸信息列表失败",ids);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询人脸信息列表成功",ids);
    }
}

