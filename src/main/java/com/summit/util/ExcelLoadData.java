package com.summit.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.summit.common.entity.DeptBean;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.repository.AccCtrlDeptDao;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.entity.AccCtrlDept;
import com.summit.entity.FaceUploadZipInfo;
import com.summit.sdk.huawei.model.FaceZipUploadStatus;
import com.summit.service.DeptsService;
import com.summit.service.FaceInfoManagerService;
import com.summit.service.ICbbUserAuthService;
import com.summit.service.LockInfoService;
import com.summit.utils.BaiduSdkClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ExcelLoadData {
    private static final String XLS = "xls";
    private static final String XLSK = "xlsx";
    private static final String JPG = "jpg";
    private static final String JPEG = "jpeg";
    private static final String PNG = "png";
    @Autowired
    private LockInfoService lockInfoService;
    @Autowired
    ICbbUserAuthService iCbbUserAuthService;
    @Autowired
    private AccCtrlDeptDao accCtrlDeptDao;
    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;
    @Autowired
    private FaceInfoManagerService faceInfoManagerService;
    @Autowired
    private BaiduSdkClient baiduSdkClient;
    @Autowired
    RedisTemplate<String, Object> genericRedisTemplate;
    @Autowired
    private DeptsService deptsService;

    public Map<String,Object> loadExcel(MultipartFile file) throws Exception{

        Map<String,Object> map=new HashMap<>();
        List<AccessControlInfo> accessControlInfos = new ArrayList<>();
        List<LockInfo> lockInfos = new ArrayList<>();
        Workbook workbook = null;
        String fileName = file.getOriginalFilename();
        if(fileName.endsWith(XLS)) {
            //2003
            try {
                workbook = new HSSFWorkbook(file.getInputStream());
            } catch (Exception e) {
                e.printStackTrace( );
            }

        }else if(fileName.endsWith(XLSK)) {
            try {
                //2007
                workbook = new XSSFWorkbook(file.getInputStream());
            } catch (Exception e) {
                e.printStackTrace( );
            }
        }else {
            throw new Exception("文件不是Excel文件");
        }
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum();//指定行数。一共多少+
        if(rows==0) {
            throw new Exception("请填写行数");
        }
        for (int i = 2; i < rows+1; i++) {
            Row row = sheet.getRow(i);
            if(row != null) {
                AccessControlInfo accessControlInfo=new AccessControlInfo();
                LockInfo lockInfo=new LockInfo();
                //读取cell
                accessControlInfo.setAccessControlId(IdWorker.getIdStr());
                String access_control_name = getCellValue(row.getCell(0));
                if (StrUtil.isBlank(access_control_name)){
                    throw new Exception("门禁名称不能为空!");
                }
                accessControlInfo.setAccessControlName(access_control_name);
                lockInfo.setLockId(IdWorker.getIdStr());
                accessControlInfo.setLockId(lockInfo.getLockId());
                String lock_code = getCellValue(row.getCell(1));
                if (StrUtil.isBlank(lock_code)){
                    throw new Exception("锁编号不能为空!");
                }
                LockInfo lock = lockInfoService.selectBylockCode(lock_code);
                if(lock != null){
                    log.error("录入锁信息失败，锁{}已存在且已属于其他门禁", lock.getLockCode());
                    throw new Exception("录入锁信息失败，锁" + lock.getLockCode() + "已存在且已属于其他门禁");
                }
                lockInfo.setLockCode(lock_code);
                lockInfo.setStatus(2);
                UserInfo uerInfo = UserContextHolder.getUserInfo();
                if(uerInfo != null){
                    lockInfo.setCreateby(uerInfo.getName());
                    accessControlInfo.setCreateby(uerInfo.getName());
                }
                accessControlInfo.setLockCode(lock_code);
                String insertdeptName = getCellValue(row.getCell(2));//机构名称+机构编码
                if (StrUtil.isBlank(insertdeptName)){
                    throw new Exception("所属机构为空!");
                }
                /*List<String> deptIds=getDeptIdsByDeptCode(deptNames,deptBeans);*/
                List<String> deptIds = getDeptIdsByNameAndCode(insertdeptName);
                if (CommonUtil.isEmptyList(deptIds)){
                    throw new Exception("所属机构不匹配!");
                }
                for (String deptId:deptIds){
                    accCtrlDeptDao.insert(new AccCtrlDept(null,deptId,accessControlInfo.getAccessControlId()));
                }
                accessControlInfo.setStatus(2);
                accessControlInfo.setWorkStatus(0);
                accessControlInfo.setCreatetime(new Date());
                lockInfo.setCreatetime(new Date());
                accessControlInfo.setUpdatetime(new Date());
                lockInfo.setUpdatetime(new Date());
                String longitude = getCellValue(row.getCell(3));//经度
                if (StrUtil.isBlank(longitude)){
                    throw new Exception("经度不能为空!");
                }
                DecimalFormat df = new DecimalFormat("0.00");
                String insertLongitude = df.format(row.getCell(3).getNumericCellValue());
                accessControlInfo.setLongitude(insertLongitude);
                String latitude = getCellValue(row.getCell(4));//维度
                if (StrUtil.isBlank(latitude)){
                    throw new Exception("纬度不能为空!");
                }
                String insertLatitude = df.format(row.getCell(4).getNumericCellValue());
                accessControlInfo.setLatitude(insertLatitude);
                lockInfo.setCurrentPassword("123456");
                lockInfo.setNewPassword(RandomUtil.randomStringUpper(6));
                accessControlInfos.add(accessControlInfo);
                lockInfos.add(lockInfo);
            }
        }
        map.put("accessControlInfos",accessControlInfos);
        map.put("lockInfos",lockInfos);
        return map;
    }

    /**
     * 根据当前的机构名称+机构编码获取当前以及子部门的部门id
     * @param insertdeptName
     * @return
     */
    private List<String> getDeptIdsByNameAndCode(String insertdeptName) {
        RestfulEntityBySummit<List<DeptBean>> queryAllDept = iCbbUserAuthService.queryAllDept();
        List<String> deptNames=new ArrayList<>();
        List<String> deptIds=null;
        for (DeptBean deptBean:queryAllDept.getData()){
            deptNames.add(deptBean.getDeptName()+"("+deptBean.getDeptCode()+")");
        }
        if (deptNames.contains(insertdeptName)){
            String deptcode = insertdeptName.substring(insertdeptName.indexOf("(")+1,  insertdeptName.indexOf(")"));
            for (DeptBean deptBean:queryAllDept.getData()){
                if (deptcode.equals(deptBean.getDeptCode())){
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("pdept",deptBean.getId());
                    deptIds = deptsService.getDeptsByPdept(jsonObject);
                }
            }
        }
        return deptIds;
    }
    private List<String> getDeptIdsByDeptCode(String deptNames, List<DeptBean> deptBeans) {
        List<String> deptIds=new ArrayList<>();
       /* String deptcode = deptNames.substring(deptNames.indexOf("(")+1,  deptNames.indexOf(")"));*/
        for (DeptBean deptBean:deptBeans){
            if (deptNames.equalsIgnoreCase(deptBean.getDeptCode())){
                deptIds.add(deptBean.getId());
                break;
            }
        }
        return deptIds;
    }
    private List<String> getDeptIds(String deptNames,List<DeptBean> deptBeans ) {
        List<String> deptIds=new ArrayList<>();
        if (deptNames.contains(",") ){//多个部门名称
            String[] list = deptNames.split(",");
            List<String> deptNameList = Arrays.asList(list);
            for (String deptName:deptNameList){
                for (DeptBean deptBean:deptBeans){
                    if (deptName.equalsIgnoreCase(deptBean.getDeptName())){
                        deptIds.add(deptBean.getId());
                    }
                }
            }
        }else if (deptNames.contains("，")){//多个部门名称
            String[] list = deptNames.split("，");
            List<String> deptNameList = Arrays.asList(list);
            for (String deptName:deptNameList){
                for (DeptBean deptBean:deptBeans){
                    if (deptName.equalsIgnoreCase(deptBean.getDeptName())){
                        deptIds.add(deptBean.getId());
                        break;
                    }
                }
            }
        }else {
            for (DeptBean deptBean:deptBeans){//一个部门名称
                if (deptNames.equalsIgnoreCase(deptBean.getDeptName())){
                    deptIds.add(deptBean.getId());
                }
            }
        }
        return deptIds;
    }


    public Map<String, Object> loadFaceExcel(MultipartFile file) throws Exception {
        Map<String,Object> map=new HashMap<>();
        List<FaceInfo> faceInfos = new ArrayList<>();
        Workbook workbook = null;
        String fileName = file.getOriginalFilename();
        if(fileName.endsWith(XLS)) {
            //2003
            try {
                workbook = new HSSFWorkbook(file.getInputStream());
            } catch (Exception e) {
                e.printStackTrace( );
            }

        }else if(fileName.endsWith(XLSK)) {
            try {
                //2007
                workbook = new XSSFWorkbook(file.getInputStream());
            } catch (Exception e) {
                e.printStackTrace( );
            }
        }else {
            throw new Exception("文件不是Excel文件");
        }
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum();//指定行数。一共多少行
        if(rows==0) {
            throw new Exception("请填写行数");
        }
        for (int i = 2; i < rows+1; i++) {
            Row row = sheet.getRow(i);
            if(row != null) {
                FaceInfo faceInfo=new FaceInfo();
                //读取cell
                faceInfo.setFaceid(IdWorker.getIdStr());
                String faceType = getCellValue(row.getCell(0));//人脸类型
                if (StrUtil.isNotBlank(faceType)){
                    /*throw new Exception("人脸类型不能为空!");*/
                    if (faceType.equals("内部人员")){
                        faceInfo.setFaceType(0);
                    }else if (faceType.equals("临时人员")){
                        faceInfo.setFaceType(1);
                    }
                }
                String userName = getCellValue(row.getCell(1));//人脸名
                if (StrUtil.isNotBlank(userName)){
                    /*throw new Exception("人脸名不能为空!");*/
                    faceInfo.setUserName(userName);
                }
                String cardType = getCellValue(row.getCell(2));//证件类型
                if (StrUtil.isNotBlank(cardType)){
                    /*throw new Exception("证件类型不能为空!");*/
                    if (cardType.equalsIgnoreCase("身份证号")){
                        faceInfo.setCardType(0);
                    }else if (cardType.equalsIgnoreCase("护照")){
                        faceInfo.setCardType(1);
                    }else if (cardType.equalsIgnoreCase("军官证")){
                        faceInfo.setCardType(2);
                    }else if (cardType.equalsIgnoreCase("驾驶证")){
                        faceInfo.setCardType(3);
                    }else if (cardType.equalsIgnoreCase("其他")){
                        faceInfo.setCardType(4);
                    }
                }
                String cardId = getCellValue(row.getCell(3));//证件号
                if (StrUtil.isNotBlank(cardId)){
                   /* throw new Exception("证件号不能为空!");*/
                    faceInfo.setCardId(cardId);
                }
                String gender = getCellValue(row.getCell(4));//性别
                if (StrUtil.isNotBlank(gender)){
                   /* throw new Exception("性别不能为空!");*/
                    if (gender.equalsIgnoreCase("男")){
                        faceInfo.setGender(0);
                    }else if (gender.equalsIgnoreCase("女")){
                        faceInfo.setGender(1);
                    }
                }
                String birthday = getCellValue(row.getCell(5));//生日
                if (StrUtil.isNotBlank(birthday)){
                    /*throw new Exception("生日不能为空!");*/
                    Date date = DateUtil.stringToDate(birthday, "yyyy-MM-dd");
                    if (date!=null){
                        faceInfo.setBirthday(birthday);
                    }
                }
                String province = getCellValue(row.getCell(6));//省份
                if (StrUtil.isNotBlank(province)){
                   /* throw new Exception("省份不能为空!");*/
                    faceInfo.setProvince(province);
                }
                String city = getCellValue(row.getCell(7));//城市
                if (StrUtil.isNotBlank(city)){
                    /*throw new Exception("城市不能为空!");*/
                    faceInfo.setCity(city);
                }
                try{
                    String faceEndTime = getCellValue(row.getCell(8));//有效日期
                    if (StrUtil.isNotBlank(faceEndTime)){
                        /*throw new Exception("有效日期不能为空!");*/
                        Date date = DateUtil.stringToDate(faceEndTime, "yyyy-MM-dd");
                        if (date!=null){
                            faceInfo.setFaceEndTime(date);
                        }
                    }
                }catch (Exception e){
                    throw new Exception("有效日期格式错误"+faceInfo.getUserName());
                }
                String insertDeptName = getCellValue(row.getCell(9));//所属机构名称+编码
                try{
                    if (StrUtil.isNotBlank(insertDeptName)){
                        RestfulEntityBySummit<List<DeptBean>> allDept = iCbbUserAuthService.queryAllDept();
                        List<String> deptNames=new ArrayList<>();
                        for (DeptBean deptBean:allDept.getData()){
                            deptNames.add(deptBean.getDeptName()+"("+deptBean.getDeptCode()+")");
                            //deptCodes.add(deptBean.getDeptCode());
                        }
                        //String deptCode = insertDeptName.substring(insertDeptName.indexOf("(")+1, insertDeptName.lastIndexOf(")"));
                        if (deptNames.contains(insertDeptName)){
                            faceInfo.setDeptNames(insertDeptName);
                        }
                    }
                }catch (Exception e){
                    log.error("所属机构格式有问题");
                }
                faceInfo.setIsValidTime(0);
                faceInfo.setFaceStartTime(DateUtil.getDatePattern("yyyy-MM-dd-HH-mm-ss"));
                faceInfos.add(faceInfo);
            }
        }
        map.put("faceInfos",faceInfos);
        return map;
    }






    //获取Cell内容
    public  String getCellValue(Cell cell) {
        String value = "";
        if(cell != null) {
            //以下是判断数据的类型
            switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_NUMERIC://数字
                    value = cell.getNumericCellValue() + "";
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        if(date != null) {
                            value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                        }else {
                            value = "";
                        }
                    }else {
                        value = new DecimalFormat("0").format(cell.getNumericCellValue());
                    }
                    break;
                case HSSFCell.CELL_TYPE_STRING: //字符串
                    value = cell.getStringCellValue();
                    break;
                case HSSFCell.CELL_TYPE_BOOLEAN: //boolean
                    value = cell.getBooleanCellValue() + "";
                    break;
                case HSSFCell.CELL_TYPE_FORMULA: //公式
                    value = cell.getCellFormula() + "";
                    break;
                case HSSFCell.CELL_TYPE_BLANK: //空值
                    value = "";
                    break;
                case HSSFCell.CELL_TYPE_ERROR: //故障
                    value = "非法字符";
                    break;
                default:
                    value = "未知类型";
                    break;
            }
        }
        return value.trim();
    }



    public  MultipartFile getMulFileByPath(String picPath) {
        FileItem fileItem = createFileItem(picPath);
        MultipartFile mfile = new CommonsMultipartFile(fileItem);
        return mfile;
    }
    public   FileItem createFileItem(String filePath) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "textField";
        int num = filePath.lastIndexOf(".");
        String extFile = filePath.substring(num);
        FileItem item = factory.createItem(textFieldName, "text/plain", true,
                "MyFileName" + extFile);
        File newfile = new File(filePath);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try (
                FileInputStream fis = new FileInputStream(newfile);
                OutputStream os = item.getOutputStream();
                ){

            while ((bytesRead = fis.read(buffer, 0, 8192))
                    != -1)
            {
                os.write(buffer, 0, bytesRead);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return item;
    }

    public String loadFaceZip(String path) throws Exception {
        List<File> files = FileUtil.loopFiles(path);
        List<FaceInfo> faceInfos=null;
        for (File file:files){
            String extName = FileUtil.extName(file.getName());//扩展名
            if(extName.equals(XLS) || extName.equals(XLSK)) {
                MultipartFile multipartFile = getMulFileByPath(file.getAbsolutePath());
                Map<String, Object> stringObjectMap = loadFaceExcel(multipartFile);
                faceInfos = (List<FaceInfo>)stringObjectMap.get("faceInfos");
            }
        }
        if (!CommonUtil.isEmptyList(faceInfos)){
            for (FaceInfo faceInfo:faceInfos){
                for (File file:files){
                    String fileName = file.getName();
                    String extName = FileUtil.extName(fileName);//扩展名
                    String absolutePath = file.getAbsolutePath();
                    String name = fileName.substring(0,fileName.lastIndexOf("."));
                    if (name.equalsIgnoreCase(faceInfo.getCardId())){
                        if (extName.equals(JPG)|| extName.equals(PNG) || extName.equals(JPEG)){
                            faceInfo.setFaceImage(absolutePath);
                        }
                    }
                }
            }
        }else {
            throw new Exception("Excel文件不存在!");
        }
        String zipId = IdWorker.getIdStr();
        Observable.just(faceInfos)
                    .observeOn(Schedulers.io())
                    .subscribe(new Action1<List<FaceInfo>>() {
                        @Override
                        public void call(List<FaceInfo> faceInfos) {
                            if (!CommonUtil.isEmptyList(faceInfos)){
                                List<String> errorList=new ArrayList<>();
                                FaceUploadZipInfo faceUploadZipInfo=new FaceUploadZipInfo();
                                faceUploadZipInfo.setUpstate("faceUploading");
                                genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
                                for (FaceInfo faceInfo:faceInfos){
                                    try {
                                        String faceImagesAbsolutePath = faceInfo.getFaceImage();
                                        if (StrUtil.isBlank(faceInfo.getUserName())) {//人脸名称
                                            errorList.add(FaceZipUploadStatus.UserName.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        if (StrUtil.isBlank(faceImagesAbsolutePath)) {//图片
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceUploadImageNameWrong.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        if (faceInfo.getFaceType()==null) {//人脸类型
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceType.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        if (faceInfo.getCardType()==null) {//证件类型
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.CardType.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }

                                        if (StrUtil.isBlank(faceInfo.getCardId())){//证件号
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.CardId.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        if (faceInfo.getGender()==null){//性别
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.Gender.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        if (StrUtil.isBlank(faceInfo.getBirthday())){//生日
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.Birthday.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        if (StrUtil.isBlank(faceInfo.getProvince())){//省份
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.Province.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        if (StrUtil.isBlank(faceInfo.getCity())){//城市
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.City.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        if (faceInfo.getFaceEndTime()==null){//有效日期
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceEndTime.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        if (StrUtil.isBlank(faceInfo.getDeptNames())){//所属机构
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.DeptNames.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        String subNewImageBase64 = com.summit.util.FileUtil.imageToBase64Str(faceImagesAbsolutePath);
                                        byte[] subNewImageBase64Byte = FileUtil.readBytes(faceImagesAbsolutePath);
                                        if (!baiduSdkClient.detectFace(subNewImageBase64)) {
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceNoDetected.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        //判断人脸图片是否在人脸库中存在
                                        List<FaceInfo> faceRepeatLibrary=new ArrayList<>();
                                        List<FaceInfo> faceInfoLibrary = faceInfoManagerDao.selectList(null);
                                        for (FaceInfo face : faceInfoLibrary) {
                                            if (StrUtil.isBlank(face.getFaceImage())) {
                                                continue;
                                            }
                                            String faceImageAbsolutePath = SystemUtil.getUserInfo().getCurrentDir() + face.getFaceImage();
                                            try {
                                                byte[] faceImageBase64 = FileUtil.readBytes(faceImageAbsolutePath);
                                                if (Arrays.equals(subNewImageBase64Byte, faceImageBase64)) {
                                                    faceRepeatLibrary.add(faceInfo);
                                                }
                                            } catch (IORuntimeException e) {
                                                e.printStackTrace();
                                                log.error("本地人脸库图片丢失,图片路径：" + faceImagesAbsolutePath);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (!CommonUtil.isEmptyList(faceRepeatLibrary)){
                                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceRepeat.getDescription());
                                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                            continue;
                                        }
                                        String faceId = baiduSdkClient.searchFace(subNewImageBase64).getFaceId();
                                        log.debug("查询百度人脸库中相似的人脸");
                                        if (StrUtil.isNotBlank(faceId)) {
                                            FaceInfo similarFaceInfo = faceInfoManagerDao.selectById(faceId);
                                            if (similarFaceInfo != null) {
                                                errorList.add("发现人脸库中有相似的人脸，名字为：" + similarFaceInfo.getUserName() + "，不能重复录入相同的人脸");
                                                genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                                continue;
                                            } else {
                                                errorList.add("发现人脸库中有相似的人脸，名字为：null，不能重复录入相同的人脸");
                                                genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                                                continue;
                                            }
                                        }
                                        faceInfoManagerService.insertFaceInfoByExcel(faceInfo);
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                }
                                if (!CommonUtil.isEmptyList(errorList)){
                                    faceUploadZipInfo.setUpstate("FaceUploadError");
                                    faceUploadZipInfo.setErrorList(errorList);
                                }else {
                                    faceUploadZipInfo.setUpstate("FaceUploadSuccess");
                                }
                                genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
                            }
                        }
                    });
            return zipId;

    }
}
