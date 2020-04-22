package com.summit.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.summit.MainAction;
import com.summit.cbb.utils.page.Page;
import com.summit.common.util.ApplicationContextUtil;
import com.summit.dao.entity.*;
import com.summit.dao.repository.*;
import com.summit.entity.FaceInfoManagerEntity;
import com.summit.exception.ErrorMsgException;
import com.summit.service.DeptFaceService;
import com.summit.service.DeptsService;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.service.FaceInfoManagerService;
import com.summit.util.CommonUtil;
import com.summit.util.ExcelLoadData;
import com.summit.utils.BaiduSdkClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2019/8/21.
 */
@Slf4j
@Service

public class FaceInfoManagerServiceImpl implements FaceInfoManagerService {
    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;
    @Autowired
    private ProvinceDao provinceDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;
    @Autowired
    private BaiduSdkClient baiduSdkClient;
    @Autowired
    private ExcelLoadData excelLoadData;
    @Autowired
    private DeptFaceService deptFaceService;
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    private DeptFaceDao deptFaceDao;

    @Autowired
    private DeptsService deptsService;

    /**
     * 插入人脸信息
     *
     * @param faceInfoManagerEntity 人脸信息
     * @return 返回-1则为不成功
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void insertFaceInfo(FaceInfoManagerEntity faceInfoManagerEntity) throws Exception {
        String base64Str = faceInfoManagerEntity.getFaceImage();
        if (StrUtil.isBlank(base64Str)) {
            log.error("人脸图片为空");
            throw new Exception("人脸图片不能为空!");
        }
        int i = base64Str.indexOf(",");
        String subNewImageBase64 = base64Str.substring(i + 1);

        if (!baiduSdkClient.detectFace(subNewImageBase64)) {
            throw new Exception("上传的图片中没有检测到人脸!");
        }

        byte[] subNewImageBase64Byte = Base64.getDecoder().decode(subNewImageBase64);

        //判断人脸图片是否在人脸库中存在
        List<FaceInfo> faceInfoLibrary = faceInfoManagerDao.selectList(null);
        for (FaceInfo faceInfo : faceInfoLibrary) {
            if (StrUtil.isBlank(faceInfo.getFaceImage())) {
                continue;
            }
            String faceImagesAbsolutePath = SystemUtil.getUserInfo().getCurrentDir() + faceInfo.getFaceImage();
            try {
                byte[] faceImagesBase64 = FileUtil.readBytes(faceImagesAbsolutePath);
                if (Arrays.equals(subNewImageBase64Byte, faceImagesBase64)) {
                    throw new Exception("人脸添加失败,头像重复!");
                }
            } catch (IORuntimeException e) {
                log.error("本地人脸库图片丢失,图片路径：" + faceImagesAbsolutePath);
            }
        }

        //判断图片的扩展名
        String extension = "";
        if (base64Str.indexOf("data:image/png;") != -1) {
            extension = ".png";
        } else if (base64Str.indexOf("data:image/jpeg;") != -1) {
            extension = ".jpeg";
        }

        String picId = IdWorker.getIdStr();
        String facePicPath = new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(MainAction.SnapshotFileName)
                .append(File.separator)
                .append(picId)
                .append("_Face")
                .append(extension)
                .toString();
        String faceUrl = new StringBuilder()
                .append("/")
                .append(MainAction.SnapshotFileName)
                .append("/")
                .append(picId)
                .append("_Face")
                .append(extension)
                .toString();

        FaceInfo faceInfo = new FaceInfo();

        faceInfo.setFaceImage(faceUrl);
        String face_id = IdWorker.getIdStr();
        faceInfo.setFaceid(face_id);
        faceInfo.setUserName(faceInfoManagerEntity.getUserName());
        faceInfo.setGender(faceInfoManagerEntity.getGender());
        faceInfo.setProvince(faceInfoManagerEntity.getProvince());
        faceInfo.setCity(faceInfoManagerEntity.getCity());
        faceInfo.setBirthday(faceInfoManagerEntity.getBirthday());
        faceInfo.setCardType(faceInfoManagerEntity.getCardType());
        faceInfo.setCardId(faceInfoManagerEntity.getCardId());
        faceInfo.setFaceType(faceInfoManagerEntity.getFaceType());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        Date date = new Date();
        String startTime = sdf.format(date);
        Date faceStartTime = sdf.parse(startTime);
        faceInfo.setFaceStartTime(faceStartTime);
        Date faceEndTime = CommonUtil.dateFormat.get().parse(faceInfoManagerEntity.getFaceEndTime());
        faceInfo.setFaceEndTime(faceEndTime);
        faceInfo.setIsValidTime(0);//人脸录入时候这时候人脸肯定未过期
        try {
            faceInfoManagerDao.insertFaceInfo(faceInfo);
            FileUtil.writeBytes(subNewImageBase64Byte, facePicPath);

            String faceId = baiduSdkClient.searchFace(subNewImageBase64).getFaceId();
            if (StrUtil.isNotBlank(faceId)) {
                FaceInfo similarFaceInfo = faceInfoManagerDao.selectById(faceId);
                if (similarFaceInfo != null) {
                    throw new Exception("发现人脸库中有相似的人脸，名字为：" + similarFaceInfo.getUserName() + "，不能重复录入相同的人脸");
                } else {
                    throw new Exception("发现人脸库中有相似的人脸，名字为：null，不能重复录入相同的人脸");
                }

            }

            if (!baiduSdkClient.addFace(subNewImageBase64, faceInfo.getFaceid())) {
                throw new Exception("人脸录入失败");
            }
            //插入部门人脸关系数据表
            if (faceInfoManagerEntity.getDepts() != null && faceInfoManagerEntity.getDepts().length > 0){
                for (String deptId : faceInfoManagerEntity.getDepts()){
                    int result= deptFaceService.insert(deptId,face_id);
                }
            }
        } catch (Exception e) {
            FileUtil.del(facePicPath);
            log.error("人脸录入异常",e);
            if (e instanceof DuplicateKeyException) {
                throw new Exception("人脸信息录入名称已存在");
            }
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 根据id批量删除人脸信息
     *
     * @param faceInfoIds
     * @return 返回-1则为不成功
     */
    @Override

    public void delFaceInfoByIds(List<String> faceInfoIds) throws Exception {
        if (faceInfoIds == null || faceInfoIds.size() == 0) {
            throw new Exception("人脸信息id为空");
        }
        try {
            for (String faceId : faceInfoIds) {
                ApplicationContextUtil.getBean(FaceInfoManagerServiceImpl.class).delFaceInfoById(faceId);
            }
        } catch (Exception e) {
            throw new Exception("人脸删除异常");
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delFaceInfoById(String faceId) throws Exception {
        if (!baiduSdkClient.deleteFace(faceId)) {
            throw new Exception("图片删除失败");
        }
        FaceInfo faceInfo = faceInfoManagerDao.selectById(faceId);
        if (StrUtil.isNotBlank(faceInfo.getFaceImage())) {
            FileUtil.del(SystemUtil.getUserInfo().getCurrentDir() + faceInfo.getFaceImage());
        }
        faceInfoAccCtrlService.deleteFaceAccCtrlByFaceId(faceId);
        faceInfoManagerDao.deleteById(faceId);
        deptFaceService.delDeptFaceByFaceIdBatch(faceId);
    }

    /**
     * 分页查询全部人脸信息
     *
     * @param faceInfoManagerEntity 人脸对象
     * @param page                  分页对象
     * @return 人脸信息列表
     */
    @Override
    public Page<FaceInfo> selectFaceInfoByPage(FaceInfoManagerEntity faceInfoManagerEntity,String deptIds,Integer current, Integer pageSize) throws Exception {
        if (faceInfoManagerEntity == null) {
            log.error("人脸信息对象为空");
            return null;
        }
        Page<FaceInfo> pageParam = null;
        if (current != null && pageSize != null) {
            pageParam = new Page<>(current, pageSize);
        }
        List<String> dept_ids =new ArrayList<>();
        if (StrUtil.isNotBlank(deptIds)){
            if (deptIds.contains(",")){//多个部门
                String[] list = deptIds.split(",");
                List<String> deptIdList = Arrays.asList(list);
                for (String deptId:deptIdList){
                    JSONObject paramJson=new JSONObject();
                    paramJson.put("pdept",deptId);
                    List<String> depts = deptsService.getDeptsByPdept(paramJson);
                    if (!CommonUtil.isEmptyList(depts)){
                        for (String dept_id:depts){
                            dept_ids.add(dept_id);
                        }
                    }
                }
            }else {//一个部门
                JSONObject paramJson=new JSONObject();
                paramJson.put("pdept",deptIds);
                List<String> depts = deptsService.getDeptsByPdept(paramJson);
                if (!CommonUtil.isEmptyList(depts)){
                    for (String dept_id:depts){
                        dept_ids.add(dept_id);
                    }
                }
            }
        }else {
            String currentDeptService = deptsService.getCurrentDeptService();
            JSONObject paramJson=new JSONObject();
            paramJson.put("pdept",currentDeptService);
            List<String> depts = deptsService.getDeptsByPdept(paramJson);
            if (!CommonUtil.isEmptyList(depts)){
                for (String dept_id:depts){
                    dept_ids.add(dept_id);
                }
            }
        }
        CommonUtil.removeDuplicate(dept_ids);//去重
        if (!CommonUtil.isEmptyList(dept_ids)){
            List<FaceInfo> faceInfos = faceInfoManagerDao.selectFaceInfoByPage(faceInfoManagerEntity, pageParam,dept_ids);
            for (FaceInfo faceInfo:faceInfos){
                String deptId = faceInfo.getDeptId();
                if (StrUtil.isNotBlank(deptId)){
                    String[] dept_Ids = deptId.split(",");
                    faceInfo.setDepts(dept_Ids);
                }
                if (StrUtil.isNotBlank(faceInfo.getDeptNames())){
                    List<String> parentDeptNames = deptsService.getParentDeptNamesByCurrentDept(null);
                    List<String> deptNames=new ArrayList<>();
                    String[] dept_names = faceInfo.getDeptNames().split(",");
                    List<String> sonDeptNames = Arrays.asList(dept_names);
                    for (String sonDeptName :sonDeptNames){
                        if (!parentDeptNames.contains(sonDeptName)){
                            deptNames.add(sonDeptName);
                        }
                    }
                    if (!CommonUtil.isEmptyList(deptNames)){
                        String deptName = String.join("," , deptNames);
                        faceInfo.setDeptNames(deptName);//需要过滤当前用户以上的部门名称
                    }
                }
            }
            if (pageParam != null) {
                pageParam.setRecords(faceInfos);
                return pageParam;
            }
            return new Page<>(faceInfos, null);
        }
        return null;
    }

    /**
     * 更新人脸信息
     *
     * @param faceInfo 需要修改的人脸信息对象
     * @return 不为-1则为修改成功
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void updateFaceInfo(FaceInfo faceInfo) throws Exception {
        if (faceInfo == null) {
            log.error("人脸信息参数为空");
            throw new Exception("人脸信息参数为空");
        }
        boolean isExpire=DateTime.of(faceInfo.getFaceEndTime()).isAfterOrEquals(new Date());
        if(isExpire){
            faceInfo.setIsValidTime(0);
        }else{
            //过期
            faceInfo.setIsValidTime(1);
        }
        String base64Str = faceInfo.getFaceImage();
        // 修改所属部门
        //String deptFaceSql = " delete from dept_face_auth where face_id  IN ('" + faceInfo.getFaceid() + "') ";
        //jdbcTemplate.update(deptFaceSql);
        //deptFaceDao.delete(Wrappers.<FaceDept>lambdaQuery().eq(FaceDept::getFaceId,faceInfo.getFaceid()));
        if (faceInfo.getDepts() !=null && faceInfo.getDepts().length>0){
            List<FaceDept> faceDepts=selectFaceDeptByFaceId(faceInfo.getFaceid());
            List<String> needDelIds=new ArrayList<>();
            if (!CommonUtil.isEmptyList(faceDepts)){
                for (FaceDept faceDept:faceDepts){
                    needDelIds.add(faceDept.getId());
                }
            }
            deptFaceDao.deleteBatchIds(needDelIds);
            for (String deptId:faceInfo.getDepts()){
                deptFaceService.insert(deptId,faceInfo.getFaceid());
            }
        }
        if (StrUtil.isBlank(base64Str)) {
            faceInfoManagerDao.updateById(faceInfo);
            return;
        }
        int i = base64Str.indexOf(",");
        String subNewImageBase64 = base64Str.substring(i + 1);
        if (!baiduSdkClient.detectFace(subNewImageBase64)) {
            throw new Exception("没有检测到人脸");
        }

        byte[] subNewImageBase64Byte = Base64.getDecoder().decode(subNewImageBase64);

        String faceId = baiduSdkClient.searchFace(subNewImageBase64).getFaceId();
        if (StrUtil.isNotBlank(faceId) && (!StrUtil.equals(faceInfo.getFaceid(), faceId))) {
            FaceInfo similarFaceInfo = faceInfoManagerDao.selectById(faceId);
            if (similarFaceInfo != null) {
                throw new Exception("发现人脸库中有相似的人脸，名字为：" + similarFaceInfo.getUserName() + "，不能重复录入相同的人脸");
            } else {
                throw new Exception("发现人脸库中有相似的人脸，名字为：null，不能重复录入相同的人脸");
            }
        }

        if (!baiduSdkClient.updateFace(subNewImageBase64, faceInfo.getFaceid())) {
            throw new Exception("人脸更新失败");
        }

        try {
            //判断新图片的扩展名
            String extension = "";
            if (base64Str.indexOf("data:image/png;") != -1) {
                extension = ".png";
            } else if (base64Str.indexOf("data:image/jpeg;") != -1) {
                extension = ".jpeg";
            }


            FaceInfo dbFaceInfo = faceInfoManagerDao.selectById(faceInfo.getFaceid());
            String newImageUrl = "";
            String oldImagePath = "";
            String newImagePath = "";
            if (StrUtil.isNotBlank(dbFaceInfo.getFaceImage())) {
                String faceImageUrl = dbFaceInfo.getFaceImage();
                int oldExtensionIndex = faceImageUrl.lastIndexOf(".");
                String oldExtension = faceImageUrl.substring(oldExtensionIndex);
                newImageUrl = StrUtil.replace(faceImageUrl, oldExtensionIndex, oldExtension, extension, false);
                newImagePath = SystemUtil.getUserInfo().getCurrentDir() + newImageUrl;
                oldImagePath = SystemUtil.getUserInfo().getCurrentDir() + dbFaceInfo.getFaceImage();
            }else{
                String picId = IdWorker.getIdStr();
                newImagePath = new StringBuilder()
                        .append(SystemUtil.getUserInfo().getCurrentDir())
                        .append(File.separator)
                        .append(MainAction.SnapshotFileName)
                        .append(File.separator)
                        .append(picId)
                        .append("_Face")
                        .append(extension)
                        .toString();
                newImageUrl = new StringBuilder()
                        .append("/")
                        .append(MainAction.SnapshotFileName)
                        .append("/")
                        .append(picId)
                        .append("_Face")
                        .append(extension)
                        .toString();
            }

            faceInfo.setFaceImage(newImageUrl);

            faceInfoManagerDao.updateById(faceInfo);
            if (StrUtil.isNotBlank(oldImagePath)) {
                FileUtil.del(oldImagePath);
            }
            FileUtil.writeBytes(subNewImageBase64Byte, newImagePath);
        } catch (Exception e) {
            throw new Exception("人脸更新失败");
        }

    }

    private List<FaceDept> selectFaceDeptByFaceId(String faceid) {
        if(faceid == null){
            log.error("人脸id未空");
            return null;
        }
        QueryWrapper<FaceDept> wrapper = new QueryWrapper<>();
        return deptFaceDao.selectList(wrapper.eq("face_id",faceid));

    }

    /**
     * 根据人脸id查询唯一的人脸信息记录
     *
     * @param faceid
     * @return 确定唯一的人脸信息记录
     */
    @Override
    public FaceInfo selectFaceInfoByID(String faceid) {
        if (faceid == null) {
            log.error("人脸信息的id为空");
            return null;
        }
        FaceInfo faceInfo = faceInfoManagerDao.selectFaceInfoByID(faceid);
        String deptId = faceInfo.getDeptId();
        if (StrUtil.isNotBlank(deptId)){
            List<String> parentDeptIds = deptsService.getParentDeptsByCurrentDept(null);
            List<String> deptIds=new ArrayList<>();
            String[] dept_Ids = deptId.split(",");
            List<String> sonDeptIds = Arrays.asList(dept_Ids);
            for (String sonDeptId :sonDeptIds){
                if (!parentDeptIds.contains(sonDeptId)){
                    deptIds.add(sonDeptId);
                }
            }
            if (!CommonUtil.isEmptyList(deptIds)){
                String[] deptList = deptIds.toArray(new String[deptIds.size()]);
                faceInfo.setDepts(deptList);//需要过滤当前用户以上的部门id
            }
            //faceInfo.setDepts(dept_Ids);
        }
        return faceInfo;
    }



    /**
     * 查询所有的省份
     *
     * @param page
     * @return 省份信息列表
     */
    @Override
    public List<Province> selectProvince(SimplePage page) {
        QueryWrapper<Province> wrapper = new QueryWrapper<>();
        return provinceDao.selectList(wrapper);
    }

    @Override
    public List<City> selectCityByProvinceId(String provinceId) {
        if (provinceId == null) {
            log.error("省份编号为空");
            return null;
        }
        QueryWrapper<City> wrapper = new QueryWrapper<>();
        return cityDao.selectList(wrapper.eq("provinceid", provinceId));
    }

    @Override
    public FaceInfo selectFaceInfoByUserNameAndCardId(String userName, String cardId) {
        if (userName == null) {
            log.error("用户名为空");
            return null;
        }
        QueryWrapper<FaceInfo> wrapper = new QueryWrapper<>();
        FaceInfo faceInfo = faceInfoManagerDao.selectOne(wrapper.eq("user_name", userName).eq("card_id", cardId));
        return faceInfo;
    }

    @Override
    public List<FaceInfo> faceInfoExport() {
        List<FaceInfo> faceInfos= faceInfoManagerDao.faceInfoExport();
        return faceInfos;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = {Exception.class} )
    public boolean batchImport(MultipartFile file) throws Exception {
        Map<String, Object> stringObjectMap = excelLoadData.loadFaceExcel(file);
        List<FaceInfo> faceInfos = (List<FaceInfo>)stringObjectMap.get("faceInfos");
        try{
            faceInfoManagerDao.insertFaceInfos(faceInfos);
        }catch (Exception e){
            log.error("批量插入数据失败");
            throw new ErrorMsgException("批量插入数据失败");
        }
        return true;
    }


}
