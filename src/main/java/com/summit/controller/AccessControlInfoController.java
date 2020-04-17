package com.summit.controller;

import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSONObject;
import com.summit.MainAction;
import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.DeptBean;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.util.ResultBuilder;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.dao.entity.AccCtrlRole;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.SimpleAccCtrlInfo;
import com.summit.exception.ErrorMsgException;
import com.summit.service.AccCtrlDeptService;
import com.summit.service.AccCtrlRoleService;
import com.summit.service.AccessControlService;
import com.summit.service.ICbbUserAuthService;
import com.summit.util.CommonUtil;
import com.summit.util.EasyExcelUtil;
import com.summit.util.ExcelExportUtil;
import com.summit.util.ExcelUtil;
import com.sun.deploy.net.URLEncoder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    private ExcelUtil excelUtil;
    @Autowired
    private AccCtrlDeptService accCtrlDeptService;
    @Autowired
    private ICbbUserAuthService iCbbUserAuthService;

    private String filePath;



    @ApiOperation(value = "分页条件查询门禁信息", notes = "分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0则查不到结果")
    @GetMapping(value = "/selectAccCtrlByPage")
    public RestfulEntityBySummit<Page<AccessControlInfo>> selectAccCtrlByPage(@ApiParam(value = "门禁名")  @RequestParam(value = "accessControlName",required = false) String accessControlName,
                                                                              @ApiParam(value = "创建人")  @RequestParam(value = "createby",required = false) String createby,
                                                                              @ApiParam(value = "锁编号")  @RequestParam(value = "lockCode",required = false) String lockCode,
                                                                              @ApiParam(value = "多个部门Id")  @RequestParam(value = "deptIds",required = false) String deptIds,
                                                                              @ApiParam(value = "入口摄像头ip")  @RequestParam(value = "入口摄像头",required = false) String entryCameraIp,
                                                                              @ApiParam(value = "出口摄像头ip")  @RequestParam(value = "出口摄像头",required = false) String exitCameraIp,
                                                                              @ApiParam(value = "门禁状态")  @RequestParam(value = "status",required = false) Integer status,
                                                                              @ApiParam(value = "当前页，大于等于1")  @RequestParam(value = "current", required = false) Integer current,
                                                                              @ApiParam(value = "每页条数，大于等于0")  @RequestParam(value = "pageSize",required = false) Integer pageSize){

        AccessControlInfo accessControlInfo = new AccessControlInfo(accessControlName,createby,lockCode,entryCameraIp,exitCameraIp,status);
        try {
            Page<AccessControlInfo> controlInfoPage  = accessControlService.selectAccCtrlByPage(accessControlInfo, current, pageSize,deptIds);
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
          /*  if(uerInfo != null){
                String[] depts = uerInfo.getDepts();
                //暂时取第一个角色
                if(!CommonUtil.isEmptyArr(depts))
                  //  accCtrlRoleService.insertAccCtrlRole(new AccCtrlRole(null,null,roles[0],accessControlInfo.getAccessControlId()));
                    accCtrlDeptService.insertAccCtrlDept(new AccCtrlDept(null,depts[0],accessControlInfo.getAccessControlId()));
            }*/
        } catch (Exception e) {
            msg = getErrorMsg(msg, e);
            log.error(msg,e);
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
        String msg = "删除门禁信息失败";
        try {
            accessControlService.delBatchAccCtrlByAccCtrlId(accessControlIds);
        } catch (Exception e) {
            msg = getErrorMsg(msg, e);
            log.error(msg,e);
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

    @ApiOperation(value = "根据门禁id查询门禁详细信息", notes = "根据门禁id查询门禁详细信息")
    @GetMapping(value = "/selectAccCtrlInfosById")
    public RestfulEntityBySummit<AccessControlInfo> selectAccCtrlInfosById(@ApiParam(value = "门禁id",required = true)  @RequestParam(value = "accCtrlId") String accCtrlId) {
        if(accCtrlId == null){
            log.error("门禁id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"门禁id为空",null);
        }
        AccessControlInfo accessControlInfo=null;
        try{
            accessControlInfo=accessControlService.selectAccCtrlById(accCtrlId);
        }catch (Exception e){
            log.error("根据门禁id查询门禁详细信息失败",e);
            e.printStackTrace();
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"根据门禁id查询门禁详细信息失败",accessControlInfo);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"根据门禁id查询门禁详细信息成功",accessControlInfo);
    }

    @ApiOperation(value = "门禁信息批量导入excel")
    @RequestMapping(value = "/batchImport", method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public RestfulEntityBySummit<String> batchImport(@ApiParam(value = "门禁模板excel", required = true) @RequestPart("accCtrlExcel") MultipartFile accCtrlExcel){
        JSONObject filesName = null;
        String msg = "门禁信息批量导入excel失败";
        if(accCtrlExcel!=null){
            try{
                filePath = new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.SnapshotFileName)
                        .append(File.separator)
                        .toString();
                filesName = com.summit.util.FileUtil.uploadFile(filePath, accCtrlExcel);
                String orginFileName = filesName.getString("fileName");
                MultipartFile mulFileByPath = excelUtil.getMulFileByPath(filePath+orginFileName);
                boolean b= accessControlService.batchImport(mulFileByPath);
            }catch (Exception e){
                msg = getErrorMsg(msg, e);
                log.error(msg,e);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, msg,null);
            }
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"门禁信息批量导入excel成功",null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9991,"导入文件为空",null);
    }

    @ApiOperation(value="门禁信息导出excel模板")
    @RequestMapping(value = "/loginAccessControlInfoExport", method = RequestMethod.GET)
    public RestfulEntityBySummit<String> loginAccessControlInfoExport( @ApiParam(value = "门禁名")  @RequestParam(value = "accessControlName",required = false) String accessControlName,
                                                                       @ApiParam(value = "创建人")  @RequestParam(value = "createby",required = false) String createby,
                                                                       @ApiParam(value = "锁编号")  @RequestParam(value = "lockCode",required = false) String lockCode) {
        String fileName=null;
        try{
            /*AccessControlInfo accessControlInfo = new AccessControlInfo(accessControlName,createby,lockCode);
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
            }*/
            filePath = new StringBuilder()
                    .append(SystemUtil.getUserInfo().getCurrentDir())
                    .append(File.separator)
                    .append(MainAction.SnapshotFileName)
                    .append(File.separator)
                    .toString();
            //fileName =System.currentTimeMillis()+"AccCtrl.xls";
            fileName ="门禁信息模板.xls";
            String [] title = new String[]{"门禁名称","锁编号(不能重复)","所属机构(多个部门用逗号隔开)","经度","纬度"};  //设置表格表头字段
            String [] properties = new String[]{"accessControlName","depts","lockCode","longitude","latitude"};  // 查询对应的字段
            ExcelExportUtil excelExport = new ExcelExportUtil();
            //excelExport.setData(dataList);
            excelExport.setHeadKey(properties);
            excelExport.setFontSize(10);
            excelExport.setSheetName("门禁信息模板");
            excelExport.setTitle("门禁信息模板");
            excelExport.setHeadList(title);
            excelExport.setResponseInfo(filePath,fileName);
        }catch (Exception e){
            log.error("门禁信息批量导出excel模板失败",e);
            e.printStackTrace();
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"门禁信息批量导出excel模板失败",fileName);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"门禁信息批量导出excel模板成功",fileName);
    }

    @ApiOperation(value="获取门禁信息导入模板")
    @RequestMapping(value = "/getAccCtrlTemplate", method = RequestMethod.GET)
    public void getAccCtrlTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sheetName = "门禁数据导入模板";
        RestfulEntityBySummit<List<DeptBean>> allDept = iCbbUserAuthService.queryAllDept();
        List<String> deptNames=new ArrayList<>();
        for (DeptBean deptBean:allDept.getData()){
            deptNames.add(deptBean.getDeptName()+"("+deptBean.getDeptCode()+")");
        }
        String[] dept_names = deptNames.toArray(new String[deptNames.size()]);
        response.setCharacterEncoding("UTF-8");
        response.reset();
        String name = URLEncoder.encode(sheetName +".xls", "UTF-8");
        response.setContentType("application/x-download;charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment;Filename=" + name);
        ServletOutputStream outputStream = response.getOutputStream();
        String path = getClass().getResource("/").getPath();
        outputStream.write(EasyExcelUtil.exportSingleByTemplate(path + File.separator +"template"+File.separator+"AccCtrl_template.xls",sheetName,dept_names,2,3));
        outputStream.flush();
    }




    @RequestMapping(value = "/fileDownload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ApiOperation(value = "文件下载")
    public RestfulEntityBySummit<String>  fileDownload(@RequestBody String filepath, HttpServletResponse response) {
        JSONObject paramJson=JSONObject.parseObject(filepath);
        String fileName =paramJson.getString("filepath") ;//获取附件路径
        filePath = new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(MainAction.SnapshotFileName)
                .append(File.separator)
                .toString();
        File file = new File(filePath+fileName); //1.获取要下载的文件的绝对路径
        if (file.exists()) { // 判断文件父目录是否存在
            // response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName); // 3.设置content-disposition响应头控制浏览器以下载的形式打开文件
            byte[] buff = new byte[1024]; // 5.创建数据缓冲区
            BufferedInputStream bis = null;
            OutputStream os = null;//
            try {
                os = response.getOutputStream(); // 6.通过response对象获取OutputStream流
                bis = new BufferedInputStream(new FileInputStream(file)); // 4.根据文件路径获取要下载的文件输入流
                int i = bis.read(buff); // 7.将FileInputStream流写入到buffer缓冲区
                while (i != -1) {
                    os.write(buff, 0, buff.length); // 8.使用将OutputStream缓冲区的数据输出到客户端浏览器
                    os.flush();
                    i = bis.read(buff);
                }
                bis.close();
            }catch (IOException e) {
                log.error("文件下载失败",e);
                e.printStackTrace();
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"文件下载失败",fileName);
            }
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"文件下载成功",fileName);
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
