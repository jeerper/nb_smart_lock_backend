package com.summit.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;
import com.summit.dao.repository.FaceInfoAccCtrlDao;
import com.summit.entity.SimFaceInfoAccCtl;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    private static final Logger logger = LoggerFactory.getLogger(FaceInfoAccCtrlController.class);
    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;
    @Autowired
    private FaceInfoAccCtrlDao faceInfoAccCtrlDao;

    @ApiOperation(value = "批量刷新指定人脸关联的门禁", notes = "为指定的人脸信息更新门禁权限，所传的人脸信息之前没有关联某门禁且所传列表中有添加，之前已关联过门禁而所传列表中有则不添加，之前已关联过门禁而所传列表中没有则删除与摄像头同步")
    @PostMapping("/authorityFaceInfoAccCtrl")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public RestfulEntityBySummit<String> authorityFaceInfoAccCtrl(@ApiParam(value = "是否授权", required = true) @RequestParam(value = "method") String method,
                                                                  @ApiParam(value = "门禁id", required = true) @RequestParam(value = "accessControlId") String accessControlId,
                                                                  @ApiParam(value = "人脸id列表", required = true) @RequestParam(value = "faceids") List<String> faceids) {

        if (StrUtil.isBlank(accessControlId)) {
            log.error("门禁信息id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "门禁信息id为空", null);
        }

        if (StrUtil.isBlank(method)) {
            log.error("门禁和人脸关联类型字段为为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "门禁和人脸关联类型字段为为空", null);
        }

        if (!CommonUtil.isEmptyList(faceids) && method.equals("auth")) {
            for (String faceId : faceids) {
                try {
                    faceInfoAccCtrlDao.insert(new FaceInfoAccCtrl(null, accessControlId, faceId));
                } catch (Exception e) {
                    log.error("人脸授权关系添加异常", e);
                }
            }
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "人脸授权成功", null);
        }
        if (method.equals("cancel_auth")) {
            //若传入集合列表为空，则需要删除所有人脸
            if (CommonUtil.isEmptyList(faceids)) {
                try {
                    faceInfoAccCtrlDao.delete(Wrappers.<FaceInfoAccCtrl>lambdaQuery()
                            .eq(FaceInfoAccCtrl::getAccessControlId, accessControlId));
                } catch (Exception e) {
                    log.error("人脸授权关系删除异常", e);
                }
            } else {
                for (String faceId : faceids) {
                    try {
                        faceInfoAccCtrlDao.delete(Wrappers.<FaceInfoAccCtrl>lambdaQuery()
                                .eq(FaceInfoAccCtrl::getAccessControlId, accessControlId)
                                .eq(FaceInfoAccCtrl::getFaceid, faceId));
                    } catch (Exception e) {
                        log.error("人脸授权关系删除异常", e);
                    }
                }
            }
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "人脸取消授权成功", null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "人脸授权操作失败", null);
    }

    @ApiOperation(value = "根据门禁id查询已经授权的人脸信息列表", notes = "查询已经和门禁关联的人脸信息列表")
    @GetMapping(value = "/selectFaceInfoByAccCtrlId")
    public RestfulEntityBySummit<List<String>> selectFaceInfoByAccCtrlId(@ApiParam(value = "门禁id") @RequestParam(value = "accCtrlId", required =
            false) String accCtrlId) {
        if (accCtrlId == null) {
            log.error("门禁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "门禁id为空", null);
        }
        List<String> ids = new ArrayList<>();
        try {
            List<FaceInfoAccCtrl> faceInfoAccCtrls = faceInfoAccCtrlService.selectFaceInfoAccCtrlByActrlId(accCtrlId);
            if (faceInfoAccCtrls != null) {
                for (FaceInfoAccCtrl faceInfoAccCtrl : faceInfoAccCtrls) {
                    ids.add(faceInfoAccCtrl.getFaceid());
                }
            }
        } catch (Exception e) {
            log.error("查询人脸信息列表失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询人脸信息列表失败", ids);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询人脸信息列表成功", ids);
    }


    @ApiOperation(value = "根据部门id查询当前以及子部门下的人脸信息")
    @GetMapping(value = "/selectAllFaceByDeptId")
    public RestfulEntityBySummit<List<FaceInfo>> selectAllFaceByDeptId(@ApiParam(value = "部门Id",required = true) @RequestParam(value = "deptIds")List<String> deptIds){
        if(CommonUtil.isEmptyList(deptIds)){
            log.error("部门id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"部门id为空",null);
        }
        List<FaceInfo> faceInfos=null;
        try{
            faceInfos=faceInfoAccCtrlService.selectAllFaceByDeptId(deptIds);
        }catch (Exception e){
            logger.error("查询人脸信息失败：", e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询人脸信息失败", faceInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "根据id查询人脸信息成功", faceInfos);
    }










    @ApiOperation(value = "根据门禁id查询人脸授权过程")
    @GetMapping(value = "/selectSimFaceInfoByAccCtrlId")
    public RestfulEntityBySummit<SimFaceInfoAccCtl> selectSimFaceInfoByAccCtrlId(@ApiParam(value = "门禁id") @RequestParam(value = "accessControlId",
            required = false) String accessControlId) {
        if (accessControlId == null) {
            log.error("门禁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "门禁id为空", null);
        }
        return ResultBuilder.buildSuccess();
    }
}

