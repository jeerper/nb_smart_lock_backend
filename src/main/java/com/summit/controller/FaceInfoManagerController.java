package com.summit.controller;

import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSONObject;
import com.summit.MainAction;
import com.summit.cbb.utils.page.Page;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.City;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.Province;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.entity.FaceInfoManagerEntity;
import com.summit.entity.SimpleFaceInfo;
import com.summit.exception.ErrorMsgException;
import com.summit.service.FaceInfoManagerService;
import com.summit.util.ExcelExportUtil;
import com.summit.util.ExcelLoadData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


    private String filePath;

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
                msg = getErrorMsg(msg, e);
                log.error(msg);
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9999, msg,null);
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