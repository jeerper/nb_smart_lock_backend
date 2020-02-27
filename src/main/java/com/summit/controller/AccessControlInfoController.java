package com.summit.controller;

import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.summit.MainAction;
import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.util.ResultBuilder;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.dao.entity.AccCtrlRole;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.AddAccCtrlprocess;
import com.summit.dao.entity.SimpleAccCtrlInfo;
import com.summit.exception.ErrorMsgException;
import com.summit.service.AccCtrlRoleService;
import com.summit.service.AccessControlService;
import com.summit.service.AddAccCtrlprocessService;
import com.summit.util.CommonUtil;
import com.summit.util.ExcelExportUtil;
import com.summit.util.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "门禁信息接口")
@RestController
@RequestMapping("/accessControlInfo")
public class AccessControlInfoController {

    @Autowired
    private AccessControlService accessControlService;
    @Autowired
    private AccCtrlRoleService accCtrlRoleService;
    @Autowired
    private AddAccCtrlprocessService addAccCtrlprocessService;
    @Autowired
    private ExcelUtil excelUtil;

    private String filePath;



    @ApiOperation(value = "分页条件查询门禁信息", notes = "分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/selectAccCtrlByPage")
    public RestfulEntityBySummit<Page<AccessControlInfo>> selectAccCtrlByPage(@ApiParam(value = "门禁名")  @RequestParam(value = "accessControlName",required = false) String accessControlName,
                                                                              @ApiParam(value = "创建人")  @RequestParam(value = "createby",required = false) String createby,
                                                                              @ApiParam(value = "锁编号")  @RequestParam(value = "lockCode",required = false) String lockCode,
                                                                              @ApiParam(value = "入口摄像头ip")  @RequestParam(value = "入口摄像头",required = false) String entryCameraIp,
                                                                              @ApiParam(value = "出口摄像头ip")  @RequestParam(value = "出口摄像头",required = false) String exitCameraIp,
                                                                              @ApiParam(value = "门禁状态")  @RequestParam(value = "status",required = false) Integer status,
                                                                              @ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                              @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize",required = false) Integer pageSize){

        AccessControlInfo accessControlInfo = new AccessControlInfo(accessControlName,createby,lockCode,entryCameraIp,exitCameraIp,status);
        try {
            Page<AccessControlInfo> controlInfoPage  = accessControlService.selectAccCtrlByPage(accessControlInfo, current, pageSize);
            return ResultBuilder.buildSuccess(controlInfoPage);
        } catch (Exception e) {
            log.error("分页查询全部门禁信息失败",e);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999);
        }
    }

    @ApiOperation(value = "录入门禁信息", notes = "录入门禁信息时同时录入锁信息和设备信息")
    @PostMapping(value = "/insertAccessControl")
    public RestfulEntityBySummit<String> insertAccessControl(@ApiParam(value = "包含锁信息和摄像头信息的门禁信息")  @RequestBody AccessControlInfo accessControlInfo){
        if(accessControlInfo == null){
            log.error("门禁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁信息为空",null);
        }
        UserInfo uerInfo = UserContextHolder.getUserInfo();
        String msg = "录入门禁信息失败";
        try {
            accessControlService.insertAccCtrl(accessControlInfo);
            //录入后立即给当前用户授权改门禁
            if(uerInfo != null){
                String[] roles = uerInfo.getRoles();
                //暂时取第一个角色
                if(!CommonUtil.isEmptyArr(roles))
                    accCtrlRoleService.insertAccCtrlRole(new AccCtrlRole(null,null,roles[0],accessControlInfo.getAccessControlId()));
            }
        } catch (Exception e) {
            msg = getErrorMsg(msg, e);
            log.error(msg);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, msg, null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"录入门禁信息成功", null);
    }

    @ApiOperation(value = "更新门禁信息", notes = "更新门禁信息时同时更新锁信息和设备信息")
    @PutMapping(value = "/updateAccessControl")
    public RestfulEntityBySummit<String> updateAccessControl(@ApiParam(value = "包含锁信息和摄像头信息的门禁信息")  @RequestBody AccessControlInfo accessControlInfo){
        if(accessControlInfo == null){
            log.error("门禁信息为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁信息为空",null);
        }
        String msg = "更新门禁信息失败";
        try {
            accessControlService.updateAccCtrl(accessControlInfo);
        } catch (Exception e) {
            msg = getErrorMsg(msg, e);
            log.error(msg);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, msg, null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "更新门禁信息成功", null);
    }

    @ApiOperation(value = "删除门禁信息，参数为id数组", notes = "根据门禁id删除门禁信息，时同删除锁关联的锁信息和设备信息")
    @DeleteMapping(value = "/delAccessControlBatch")
    public RestfulEntityBySummit<String> delAccessControlBatch(@ApiParam(value = "门禁id",required = true)  @RequestParam(value = "accessControlIds",required = false) List<String> accessControlIds){
        if(CommonUtil.isEmptyList(accessControlIds)){
            log.error("门禁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁id为空",null);
        }
        /**
         * 删除门禁的时候,统计分析也要删除相应的门禁
         */
        List<String> needDeladdAccCtrlprocessIds=new ArrayList<>();
        for (String accessControlId:accessControlIds){
            AddAccCtrlprocess addAccCtrlprocess = addAccCtrlprocessService.selectAddAccCtrlByAccCtrlID(accessControlId);
            if (addAccCtrlprocess !=null){
                needDeladdAccCtrlprocessIds.add(addAccCtrlprocess.getId());
            }
        }
        String msg1 = "删除统计信息表中对应的门禁信息失败";
        if (!CommonUtil.isEmptyList(needDeladdAccCtrlprocessIds)){
            try {
                addAccCtrlprocessService.deladdAccCtrlprocessByAccCtrlId(needDeladdAccCtrlprocessIds);
            } catch (Exception e) {
                msg1 = getErrorMsg(msg1, e);
                log.error(msg1);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, msg1, null);
            }
        }

        String msg = "删除门禁信息失败";
        try {
            accessControlService.delBatchAccCtrlByAccCtrlId(accessControlIds);
        } catch (Exception e) {
            msg = getErrorMsg(msg, e);
            log.error(msg);
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, msg, null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "删除门禁信息成功", null);
    }


    @ApiOperation(value = "根据角色code查询已授权的门禁id列表", notes = "查询角色关联的已授权的门禁id列表")
    @GetMapping(value = "/selectAccCtrlIdsByRoleCode")
    public RestfulEntityBySummit<List<String>> selectAccCtrlIdsByRoleCode(@ApiParam(value = "角色code")  @RequestParam(value = "roleCode", required = false) String roleCode){
        if(roleCode == null){
            log.error("角色code为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"角色code为空",null);
        }
        List<String> ids = new ArrayList<>();
        try {
            List<AccCtrlRole> accCtrlRoles = accCtrlRoleService.selectAccCtrlRolesByRoleCode(roleCode);
            if(accCtrlRoles != null){
                for(AccCtrlRole role : accCtrlRoles) {
                    ids.add(role.getAccessControlId());
                }
            }
        } catch (Exception e) {
            log.error("查询门禁id列表失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询门禁id列表失败",ids);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询门禁id列表成功",ids);
    }

    @ApiOperation(value = "根据用户名查询所关联的门禁信息列表", notes = "根据用户名查询所关联的门禁信息列表")
    @GetMapping(value = "/selectAccCtrlInfosByUserName")
    public RestfulEntityBySummit<List<AccessControlInfo>> selectAccCtrlInfosByUserName(@ApiParam(value = "登录名",required = true)  @RequestParam(value = "userName") String userName) {
        if(userName == null){
            log.error("用户名userName为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"用户名userName为空",null);
        }
        List<AccessControlInfo> accessControlInfos=null;
        try{
            accessControlInfos=accessControlService.selectAccCtrlInfosByUserName(userName);
        }catch (Exception e){
            log.error("查询门禁信息列表失败",e);
            e.printStackTrace();
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询门禁信息列表失败",accessControlInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询门禁信息列表成功",accessControlInfos);
    }

    @ApiOperation(value = "门禁信息批量导入excel")
    @RequestMapping(value = "/batchImport", method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public RestfulEntityBySummit<String> batchImport(@ApiParam(value = "门禁模板excel", required = true) @RequestParam("file") MultipartFile file){
        if(file !=null){
            log.error("上传文件为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"上传文件为空",null);
        }
        try{
          //  MultipartFile mulFileByPath = excelUtil.getMulFileByPath("D:\\qianyy\\nb_smart_lock_backend\\snapshot\\1232571339395223554_AccCtrlExportTemplate.xls");
            boolean b= accessControlService.batchImport(file);
        }catch (Exception e){
            log.error("批量导入失败",e);
            e.printStackTrace();
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"门禁信息批量导入excel失败",null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"门禁信息批量导入excel成功",null);
    }

    @ApiOperation(value="门禁信息导出excel模板")
    @RequestMapping(value = "/loginAccessControlInfoExport", method = RequestMethod.GET)
    public RestfulEntityBySummit<String> loginAccessControlInfoExport( @ApiParam(value = "门禁名")  @RequestParam(value = "accessControlName",required = false) String accessControlName,
                                                                       @ApiParam(value = "创建人")  @RequestParam(value = "createby",required = false) String createby,
                                                                       @ApiParam(value = "锁编号")  @RequestParam(value = "lockCode",required = false) String lockCode) {
        AccessControlInfo accessControlInfo = new AccessControlInfo(accessControlName,createby,lockCode);
        String fileName=null;
        try{
            List<AccessControlInfo> accessControlInfos= accessControlService.loginAccessControlInfoExport(accessControlInfo);
            if(accessControlInfos!=null && accessControlInfos.size()>0){
                List<JSONObject> dataList= new ArrayList<>();
                for (AccessControlInfo accCtrl:accessControlInfos){
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("accessControlId",accCtrl.getAccessControlId());
                    jsonObject.put("accessControlName",accCtrl.getAccessControlName());
                    jsonObject.put("accessControlId",accCtrl.getAccessControlId());
                    jsonObject.put("lockId",accCtrl.getLockId());
                    jsonObject.put("lockCode",accCtrl.getLockCode());
                    jsonObject.put("entryCameraId",accCtrl.getEntryCameraId());
                    jsonObject.put("entryCameraIp",accCtrl.getEntryCameraIp());
                    jsonObject.put("exitCameraId",accCtrl.getExitCameraId());
                    jsonObject.put("exitCameraIp",accCtrl.getExitCameraIp());
                    jsonObject.put("status",accCtrl.getStatus());
                    jsonObject.put("createby",accCtrl.getCreateby());
                    jsonObject.put("createtime",accCtrl.getCreatetime());
                    jsonObject.put("updatetime",accCtrl.getUpdatetime());
                    jsonObject.put("longitude",accCtrl.getLongitude());
                    jsonObject.put("latitude",accCtrl.getLatitude());
                    dataList.add(jsonObject);
                }
                String picId = IdWorker.getIdStr();
                filePath = new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.SnapshotFileName)
                        .append(File.separator)
                        .append(picId)
                        .append("_AccCtrlExportTemplate")
                        .toString();
                fileName =System.currentTimeMillis()+".xls";
                String [] title = new String[]{"门禁id","门禁名称","锁id","锁编号","入口摄像头id","入口摄像头ip地址","出口摄像头id","出口摄像头ip地址","门禁状态","创建人","创建时间","更新时间","经度","纬度"};  //设置表格表头字段
                String [] properties = new String[]{"accessControlId","accessControlName","lockId","lockCode","entryCameraId","entryCameraIp","exitCameraId","exitCameraIp","status","createby","createtime","updatetime","longitude","latitude"};  // 查询对应的字段
                ExcelExportUtil excelExport2 = new ExcelExportUtil();
                excelExport2.setData(dataList);
                excelExport2.setHeadKey(properties);
                excelExport2.setFontSize(20);
                excelExport2.setSheetName("门禁信息导出模板");
                excelExport2.setTitle("门禁信息导出模板");
                excelExport2.setHeadList(title);
                excelExport2.setResponseInfo(filePath,fileName);
            }
        }catch (Exception e){
            log.error("门禁信息批量导出excel模板失败",e);
            e.printStackTrace();
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"门禁信息批量导出excel模板失败",fileName);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"门禁信息批量导出excel模板成功",fileName);
    }




    @ApiOperation(value = "查询全部门禁信息，包括门禁id和name", notes = "无论有无门禁权限都查询全部")
    @GetMapping(value = "/selectAllAccessControl")
    public RestfulEntityBySummit<List<SimpleAccCtrlInfo>> selectAllAccessControl(){
        List<SimpleAccCtrlInfo> simpleAccCtrlInfos = new ArrayList<>();
        try {
            List<AccessControlInfo> accessControlInfos = accessControlService.selectAllAccessControl(null);
            if(accessControlInfos != null){
                for(AccessControlInfo acInfo : accessControlInfos) {
                    simpleAccCtrlInfos.add(new SimpleAccCtrlInfo(acInfo.getAccessControlId(),acInfo.getAccessControlName()));
                }
            }
        } catch (Exception e) {
            log.error("分页查询全部门禁信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"查询全部门禁信息失败", simpleAccCtrlInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"查询全部门禁信息成功", simpleAccCtrlInfos);
    }

    private String getErrorMsg(String msg, Exception e) {
        if(e instanceof ErrorMsgException){
            return ((ErrorMsgException) e).getErrorMsg();
        }
        return msg;
    }

}
