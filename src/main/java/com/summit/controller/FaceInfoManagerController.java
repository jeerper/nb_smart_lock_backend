package com.summit.controller;

import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSONObject;
import com.summit.MainAction;
import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.DeptBean;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.City;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.Province;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.entity.FaceInfoManagerEntity;
import com.summit.entity.FaceUploadZipInfo;
import com.summit.entity.SimpleFaceInfo;
import com.summit.exception.ErrorMsgException;
import com.summit.service.DeptsService;
import com.summit.service.FaceInfoManagerService;
import com.summit.service.ICbbUserAuthService;
import com.summit.util.EasyExcelUtil;
import com.summit.util.ExcelExportUtil;
import com.summit.util.ExcelLoadData;
import com.summit.util.ZipUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/8/21.
 */
@Slf4j
@Api(tags = "人脸信息管理接口")
@RestController
@RequestMapping("/faceInfoManager")
public class FaceInfoManagerController {
    @Autowired
    private FaceInfoManagerService faceInfoManagerService;
    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;
    @Autowired
    private ExcelLoadData excelLoadData;
    @Autowired
    private ICbbUserAuthService iCbbUserAuthService;
    private String filePath;
    @Autowired
    private DeptsService deptsService;
    @Autowired
    RedisTemplate<String, Object> genericRedisTemplate;

    @ApiOperation(value = "录入人脸信息", notes = "返回不是-1则为成功")
    @PostMapping(value = "insertFaceInfo")
    public RestfulEntityBySummit<Integer> insertFaceInfo(@RequestBody FaceInfoManagerEntity faceInfoManagerEntity) {
        try {
            faceInfoManagerService.insertFaceInfo(faceInfoManagerEntity);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, e.getMessage(), CommonConstants.UPDATE_ERROR);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "录入人脸信息成功", 1);
    }

    @ApiOperation(value = "根据所传一个或多个条件组合分页查询人脸信息记录", notes = "各字段都为空则查询全部。分页参数为空则查全部，current和pageSize有一个为null则查询不到结果，current<=0则置为1，pageSize<=0" +
            "则查不到结果")
    @GetMapping(value = "/selectFaceInfoByPage")
    public RestfulEntityBySummit<Page<FaceInfo>> selectFaceInfoByPage( @ApiParam(value = "姓名、省份、城市、证件号") @RequestParam(value = "pubquery", required = false, defaultValue = "") String pubquery,
                                                                      @ApiParam(value = "性别，0：男，1：女，2：未知") @RequestParam(value = "gender", required = false) Integer gender,
                                                                      @ApiParam(value = "证件类型，0：身份证，1：护照，2：军官证，3：驾驶证，4：未知") @RequestParam(value = "cardType", required = false) Integer cardType,
                                                                      @ApiParam(value = "人脸类型，0:内部人员，1:临时人员") @RequestParam(value = "faceType",required = false) Integer faceType,
                                                                      @ApiParam(value = "人脸过期，0:没有过期，1:已经过期") @RequestParam(value = "isValidTime",required = false) Integer isValidTime,
                                                                      @ApiParam(value = "部门id(多个部门用,隔开)") @RequestParam(value = "deptId", required = false,defaultValue = "") String deptId,
                                                                      @ApiParam(value = "当前页，大于等于1") @RequestParam(value = "current", required = false)Integer current,
                                                                      @ApiParam(value = "每页条数，大于等于0") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Page<FaceInfo> faceInfoPage = null;
        try {
            FaceInfoManagerEntity faceInfoManagerEntity = new FaceInfoManagerEntity();
            faceInfoManagerEntity.setPubquery(pubquery);
            faceInfoManagerEntity.setGender(gender);
            faceInfoManagerEntity.setCardType(cardType);
            faceInfoManagerEntity.setFaceType(faceType);
            faceInfoManagerEntity.setIsValidTime(isValidTime);
            faceInfoPage = faceInfoManagerService.selectFaceInfoByPage(faceInfoManagerEntity,deptId, current,pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("分页全部查询人脸信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "分页全部查询人脸信息失败", faceInfoPage);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "分页全部查询人脸信息成功", faceInfoPage);
    }

    @ApiOperation(value = "更新人脸信息")
    @PutMapping(value = "/updateFaceInfo")
    public RestfulEntityBySummit<String> updateFaceInfo(@ApiParam(value = "包含人脸信息") @RequestBody FaceInfo faceInfo) {
        try {
            faceInfoManagerService.updateFaceInfo(faceInfo);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, e.getMessage(), null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "更新人脸信息成功", null);
    }


    @ApiOperation(value = "删除人脸信息，参数为id数组", notes = "根据人脸id删除人脸信息")
    @DeleteMapping(value = "/delfaceInfoByIdBatch")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public RestfulEntityBySummit<String> delFaceInfo(@ApiParam(value = "人脸信息的id", required = true)
                                                     @RequestParam(value = "faceInfoIds") List<String> faceInfoIds) {
        try {
            faceInfoManagerService.delFaceInfoByIds(faceInfoIds);
        } catch (Exception e) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, e.getMessage(), null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "删除人脸信息成功", null);
    }

    @ApiOperation(value = "根据id查询人脸信息", notes = "faceid不能为空，查询唯一一条人脸信息")
    @GetMapping(value = "/queryFaceInfoById")
    public RestfulEntityBySummit<FaceInfo> queryFaceInfoById(@ApiParam(value = "人脸信息id", required = true) @RequestParam(value = "faceid") String faceid) {
        if (faceid == null) {
            log.error("人脸信息id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993, "人脸信息id为空", null);
        }
        FaceInfo faceInfo = null;
        try {
            faceInfo = faceInfoManagerService.selectFaceInfoByID(faceid);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询人脸信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "根据id查询人脸信息失败", faceInfo);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "根据id查询人脸信息成功", faceInfo);
    }


    @ApiOperation(value = "查询全部人脸信息，包括人脸信息的id和name以及人脸图片,人脸是否过期", notes = "无论有无门禁权限都全部查询")
    @GetMapping(value = "/selectAllFaceInfo")
    public RestfulEntityBySummit<List<SimpleFaceInfo>> selectAllFaceInfo() {
        List<SimpleFaceInfo> simpleFaceInfos = new ArrayList<>();
        try {
            List<FaceInfo> faceInfos = faceInfoManagerDao.selectList(null);
            if (faceInfos != null) {
                for (FaceInfo faceInfo : faceInfos) {
                    simpleFaceInfos.add(new SimpleFaceInfo(faceInfo.getFaceid(), faceInfo.getUserName(), faceInfo.getFaceImage(),
                            faceInfo.getIsValidTime(),faceInfo.getAuthStatus()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询全部的人脸信息失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询全部人脸信息失败", simpleFaceInfos);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询全部人脸信息成功", simpleFaceInfos);
    }

    @ApiOperation(value = "查询全部的省份")
    @GetMapping(value = "/selectProvince")
    public RestfulEntityBySummit<List<Province>> selectProvince() {
        List<Province> provinces = null;
        try {
            provinces = faceInfoManagerService.selectProvince(null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询全部的省份失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询全部的省份失败", provinces);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询全部的省份成功", provinces);
    }

    @ApiOperation(value = "根据省份的编号查询省份所对应的所有的城市")
    @GetMapping(value = "/selectCityByProvinceId")
    public RestfulEntityBySummit<List<String>> selectCityByProvinceId(@ApiParam(value = "省份编号") @RequestParam(value = "provinceId", required =
            false) String provinceId) {
        if (provinceId == null) {
            log.error("省份编号为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "省份编号为空", null);
        }
        List<String> cityNames = new ArrayList<>();
        try {
            List<City> cities = faceInfoManagerService.selectCityByProvinceId(provinceId);
            if (cities != null) {
                for (City city : cities) {
                    cityNames.add(city.getCityName());
                }
            }
        } catch (Exception e) {
            log.error("查询城市信息列表失败");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, "查询城市信息列表失败", cityNames);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000, "查询城市信息列表成功", cityNames);
    }


    @ApiOperation(value = "人脸信息导出excel模板")
    @RequestMapping(value = "/faceInfoExport", method = RequestMethod.GET)
    public RestfulEntityBySummit<String> faceInfoExport() {
        String fileName=null;
        try{
          List<FaceInfo>  faceInfos=faceInfoManagerService.faceInfoExport();
          if (faceInfos !=null && faceInfos.size()>0){
              List<JSONObject> dataList= new ArrayList<>();
              for(FaceInfo faceInfo:faceInfos){
                  JSONObject jsonObject=new JSONObject();
                  jsonObject.put("userName",faceInfo.getUserName());
                  jsonObject.put("gender",faceInfo.getGender());
                  jsonObject.put("birthday",faceInfo.getBirthday());
                  jsonObject.put("province",faceInfo.getProvince());
                  jsonObject.put("city",faceInfo.getCity());
                  jsonObject.put("cardType",faceInfo.getCardType());
                  jsonObject.put("cardId",faceInfo.getCardId());
                  jsonObject.put("faceMatchrate",faceInfo.getFaceMatchrate());
                  jsonObject.put("faceImage",faceInfo.getFaceImage());
                  jsonObject.put("faceType",faceInfo.getFaceType());
                  jsonObject.put("faceStartTime",faceInfo.getFaceStartTime());
                  jsonObject.put("faceEndTime","2020-10-24");
                  dataList.add(jsonObject);
              }

              filePath = new StringBuilder()
                      .append(SystemUtil.getUserInfo().getCurrentDir())
                      .append(File.separator)
                      .append(MainAction.SnapshotFileName)
                      .append(File.separator)
                      .toString();
              fileName =System.currentTimeMillis()+"FaceInfo.xls";
              String [] title = new String[]{"姓名","性别:(0男，1女，2未知)","生日","省份","城市","证件类型:(证件类型。0：身份证，1：护照，2：军官证，3：驾驶证，4：未知)","身份证号","人脸类型:(0表示内部人员，1表示临时人员)","人脸有效期时间"};  //设置表格表头字段
              String [] properties = new String[]{"userName","gender","birthday","province","city","cardType","cardId","faceType","faceEndTime"};  // 查询对应的字段
              ExcelExportUtil excelExport2 = new ExcelExportUtil();
              excelExport2.setData(dataList);
              excelExport2.setHeadKey(properties);
              excelExport2.setFontSize(20);
              excelExport2.setSheetName("人脸信息导出模板");
              excelExport2.setTitle("人脸信息导出模板");
              excelExport2.setHeadList(title);
              excelExport2.setResponseInfo(filePath,fileName);
          }
        }catch (Exception e){
            log.error("人脸信息批量导出excel模板失败",e);
            e.printStackTrace();
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"人脸信息批量导出excel模板失败",fileName);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"人脸信息批量导出excel模板成功",fileName);
    }


    @ApiOperation(value = "获取人脸数据导入模板")
    @RequestMapping(value = "/getFaceInfoTemplate",method = RequestMethod.GET)
    public void getFaceInfoTemplate(HttpServletResponse response) throws Exception {
        String sheetName="人脸数据导入模板";
        String[] faceType=new String[]{"内部人员","临时人员"};
        String[] sex=new String[]{"男","女"};
        String[] cardType=new String[]{"身份证号","护照","军官证","驾驶证","其他"};
        List<Province> provinces = faceInfoManagerService.selectProvince(null);
        List<String> province_names=new ArrayList<>();
        for (Province province:provinces){
            province_names.add(province.getProvince());
        }
        String[] provinceNames = province_names.toArray(new String[province_names.size()]);
        RestfulEntityBySummit<List<DeptBean>> allDept = iCbbUserAuthService.queryAllDept();
        String currentDeptService = deptsService.getCurrentDeptService();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("pdept",currentDeptService);
        List<String> userDeptIds = deptsService.getDeptsByPdept(jsonObject);
        List<String> deptNames=new ArrayList<>();
        for (DeptBean deptBean:allDept.getData()){
            if (userDeptIds.contains(deptBean.getId())){
                deptNames.add(deptBean.getDeptName()+"("+deptBean.getDeptCode()+")");
//                deptNames.add(deptBean.getDeptCode());
            }
        }
       /* String[] dept_names = deptNames.toArray(new String[deptNames.size()]);*/
        String[] dept_names = deptNames.toArray(new String[0]);
        response.setCharacterEncoding("UTF-8");
        response.reset();
        response.setContentType("application/x-download;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + sheetName);
        ServletOutputStream outputStream = response.getOutputStream();
        String path = getClass().getResource("/").getPath();
        Map<Integer,String[]> map=new HashMap<>();
        map.put(0,faceType);
        map.put(1,sex);
        map.put(2,cardType);
        map.put(3,provinceNames);
        map.put(4,dept_names);
        byte[] bytes = EasyExcelUtil.exportSingleFaceInfoByTemplate(path + File.separator + "template" + File.separator + "Face_template.xls", map, sheetName);
        outputStream.write(bytes);
        outputStream.flush();

    }

    @ApiOperation(value = "人脸批量导入zip")
    @RequestMapping(value = "/uploadZip", method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public RestfulEntityBySummit<String> uploadZip(@ApiParam(value = "压缩文件夹", required = true)
                                                   @RequestPart("faceZip") MultipartFile faceZip) throws IOException {
        JSONObject filesName = null;
        String zipId=null;
        if (faceZip !=null){
            try{
                String zipPath=new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.SnapshotFileName)
                        .append(File.separator)
                        .append(MainAction.FaceTemplateZip)
                        .append(File.separator)
                        .toString();
                filesName = com.summit.util.FileUtil.uploadFile(zipPath, faceZip);
                String fileName = filesName.getString("fileName");
                String zipFileName = fileName.substring(0,fileName.indexOf("."));
                ZipUtil.unzip(zipPath+fileName,zipPath+zipFileName);
                zipId = excelLoadData.loadFaceZip(zipPath + zipFileName);
            }catch (Exception e){
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, e.getMessage(), null);
            }
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"人脸信息批量导入成功",zipId);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9991,"导入压缩文件夹为空",null);
    }

    @ApiOperation(value = "根据人脸上传id查询人脸上传结果")
    @GetMapping(value = "/getFaceUploadResultById")
    public RestfulEntityBySummit<FaceUploadZipInfo> getFaceUploadResultById(@ApiParam(value = "人脸上传Id",required = true)  @RequestParam(value = "uploadId") String uploadId){
        if(uploadId == null){
            log.error("人脸上传Id为空");
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993,"人脸上传Id为空",null);
        }
        FaceUploadZipInfo faceUploadZipInfo=null;
        try{
            boolean isExists=genericRedisTemplate.hasKey(uploadId);
            if (isExists){
                BoundValueOperations<String,Object> boundValueOperations=genericRedisTemplate.boundValueOps(uploadId);
                faceUploadZipInfo =(FaceUploadZipInfo)boundValueOperations.get();
            }
        }catch (Exception e){
            log.error("根据人脸上传id查询人脸上传结果失败",e);
            e.printStackTrace();
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999,"根据人脸上传id查询人脸上传结果失败",faceUploadZipInfo);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"根据人脸上传id查询人脸上传结果成功",faceUploadZipInfo);
    }



    @ApiOperation(value = "人脸信息批量导入excel")
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public RestfulEntityBySummit<String> batchImport(@ApiParam(value = "人脸图片", required = true)
                                                     @RequestPart("faceExcel") MultipartFile faceExcel) throws IOException {
        JSONObject filesName = null;
        String msg = "人脸信息批量导入excel失败";
        if(faceExcel!=null){
            try{
                filePath = new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.SnapshotFileName)
                        .append(File.separator)
                        .toString();
                filesName = com.summit.util.FileUtil.uploadFile(filePath, faceExcel);
                String orginFileName = filesName.getString("fileName");
                MultipartFile mulFileByPath = excelLoadData.getMulFileByPath(filePath+orginFileName);
                boolean b= faceInfoManagerService.batchImport(mulFileByPath);
            }catch (Exception e){
                log.error(msg,e);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, e.getMessage(), null);
            }
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_0000,"人脸信息批量导入excel成功",null);
        }
        return ResultBuilder.buildError(ResponseCodeEnum.CODE_9991,"导入文件为空",null);

    }

    private String getErrorMsg(String msg, Exception e) {
        if(e instanceof ErrorMsgException){
            return ((ErrorMsgException) e).getErrorMsg();
        }
        return msg;
    }

}