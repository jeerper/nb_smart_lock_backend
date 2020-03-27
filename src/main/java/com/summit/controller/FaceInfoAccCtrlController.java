package com.summit.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;
import com.summit.dao.repository.FaceInfoAccCtrlDao;
import com.summit.entity.SimFaceInfoAccCtl;
import com.summit.entity.SimpleFaceInfo;
import com.summit.exception.ErrorMsgException;
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
                                                                  @ApiParam(value = "门禁id列表", required = true) @RequestParam(value = "accessControlIds") List<String> accessControlIds,
                                                                  @ApiParam(value = "人脸id列表", required = true) @RequestParam(value = "faceids") List<String> faceids) throws Exception {

        if (CommonUtil.isEmptyList(accessControlIds)) {
            log.error("门禁信息id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "门禁信息id为空", null);
        }
        if (CommonUtil.isEmptyList(faceids)) {
            log.error("人脸id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "人脸id为空", null);
        }
        if (StrUtil.isBlank(method)) {
            log.error("门禁和人脸关联类型字段为为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "门禁和人脸关联类型字段为为空", null);
        }
        if (method.equals("auth")) {
            try{
                faceInfoAccCtrlService.refreshAccCtrlFaceBatch(accessControlIds,faceids);
            }catch (Exception e){
                log.error("人脸授权关系添加异常", e);
            }
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "人脸授权成功", null);
        }
        if (method.equals("cancel_auth")) {
            for (String accessControlId:accessControlIds) {
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

    @ApiOperation(value = "根据所传门禁Id查询已经授权的人脸信息列表", notes = "查询已经和门禁关联的人脸信息列表")
    @GetMapping(value = "/selectFaceInfoByAccCtrlId")
    public RestfulEntityBySummit<List<SimpleFaceInfo>> selectFaceInfoByAccCtrlId(@ApiParam(value = "门禁id") @RequestParam(value = "accCtrlIds", required =
            false) List<String> accCtrlIds) {
        if (CommonUtil.isEmptyList(accCtrlIds)) {
            log.error("门禁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "门禁id为空", null);
        }
        List<SimpleFaceInfo> simpleFaceInfos = new ArrayList<>();
        try {
            List<FaceInfo> faceInfos = faceInfoAccCtrlService.selectFaceInfoAccCtrlByActrlIds(accCtrlIds);
            if (faceInfos != null) {
                for (FaceInfo faceInfo : faceInfos) {
                    simpleFaceInfos.add(new SimpleFaceInfo(faceInfo.getFaceid(), faceInfo.getUserName(), faceInfo.getFaceImage(),
                            faceInfo.getIsValidTime(),faceInfo.getAuthStatus()));
                }
            }
        } catch (Exception e) {
            log.error("查询人脸信息列表失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询人脸信息列表失败", simpleFaceInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询人脸信息列表成功", simpleFaceInfos);
    }


    @ApiOperation(value = "根据所传部门Id查询当前以及子部门下的人脸信息")
    @GetMapping(value = "/selectAllFaceByDeptId")
    public RestfulEntityBySummit<List<SimpleFaceInfo>> selectAllFaceByDeptId(@ApiParam(value = "部门Id",required = true) @RequestParam(value = "deptIds")List<String> deptIds){
        if(CommonUtil.isEmptyList(deptIds)){
            log.error("部门id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"部门id为空",null);
        }
        List<SimpleFaceInfo> simpleFaceInfos = new ArrayList<>();
        try{
            List<FaceInfo> faceInfos =faceInfoAccCtrlService.selectAllFaceByDeptId(deptIds);
            if (faceInfos != null) {
                for (FaceInfo faceInfo : faceInfos) {
                    simpleFaceInfos.add(new SimpleFaceInfo(faceInfo.getFaceid(), faceInfo.getUserName(), faceInfo.getFaceImage(),
                            faceInfo.getIsValidTime(),faceInfo.getAuthStatus()));
                }
            }
        }catch (Exception e){
            logger.error("查询人脸信息失败：", e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询人脸信息失败", simpleFaceInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询人脸信息成功", simpleFaceInfos);
    }

    @ApiOperation(value = "根据所传部门Id查询当前以及子部门下的门禁信息")
    @GetMapping(value = "/selectAllAccCtrlByDeptId")
    public RestfulEntityBySummit<List<AccessControlInfo>> selectAllAccCtrlByDeptId(@ApiParam(value = "部门Id",required = true) @RequestParam(value = "deptIds")List<String> deptIds){
        if(CommonUtil.isEmptyList(deptIds)){
            log.error("部门id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"部门id为空",null);
        }
        List<AccessControlInfo> accessControlInfos=null;
        try{
            accessControlInfos=faceInfoAccCtrlService.selectAllAccCtrlByDeptId(deptIds);
        }catch (Exception e){
            logger.error("查询人脸信息失败：", e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询人脸信息失败", accessControlInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询人脸信息成功", accessControlInfos);
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

    private String getErrorMsg(String msg, Exception e) {
        if(e instanceof ErrorMsgException){
            return ((ErrorMsgException) e).getErrorMsg();
        }
        return msg;
    }
}

