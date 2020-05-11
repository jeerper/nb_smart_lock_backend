package com.summit.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
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
import com.summit.exception.ErrorMsgException;
import com.summit.sdk.huawei.model.FaceZipUploadStatus;
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
            throw new ErrorMsgException("文件不是Excel文件");
        }
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum();//指定行数。一共多少+
        if(rows==0) {
            throw new ErrorMsgException("请填写行数");
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
                    throw new ErrorMsgException("门禁名称不能为空!");
                }
                accessControlInfo.setAccessControlName(access_control_name);
                lockInfo.setLockId(IdWorker.getIdStr());
                accessControlInfo.setLockId(lockInfo.getLockId());
                String lock_code = getCellValue(row.getCell(1));
                if (StrUtil.isBlank(lock_code)){
                    throw new ErrorMsgException("锁编号不能为空!");
                }
                LockInfo lock = lockInfoService.selectBylockCode(lock_code);
                if(lock != null){
                    log.error("录入锁信息失败，锁{}已存在且已属于其他门禁", lock.getLockCode());
                    throw new ErrorMsgException("录入锁信息失败，锁" + lock.getLockCode() + "已存在且已属于其他门禁");
                }
                lockInfo.setLockCode(lock_code);
                lockInfo.setStatus(2);
                UserInfo uerInfo = UserContextHolder.getUserInfo();
                if(uerInfo != null){
                    lockInfo.setCreateby(uerInfo.getName());
                    accessControlInfo.setCreateby(uerInfo.getName());
                }
                accessControlInfo.setLockCode(lock_code);
                String deptNames = getCellValue(row.getCell(2));
                if (StrUtil.isBlank(deptNames)){
                    throw new ErrorMsgException("所属部门!");
                }
                RestfulEntityBySummit<List<DeptBean>> queryAllDept = iCbbUserAuthService.queryAllDept();
                List<DeptBean> deptBeans = queryAllDept.getData();
                if (CommonUtil.isEmptyList(deptBeans)){
                    throw new ErrorMsgException("部门管理列表为空!");
                }
                /*List<String> deptIds=getDeptIdsByDeptCode(deptNames,deptBeans);*/
                List<String> deptIds = getDeptIdsByNameAndCode(deptNames, deptBeans);
                if (CommonUtil.isEmptyList(deptIds)){
                    throw new ErrorMsgException("所属部门不匹配!");
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
                DecimalFormat df = new DecimalFormat("0.00");
                String longitude = df.format(row.getCell(3).getNumericCellValue());
                if (StrUtil.isBlank(longitude)){
                    throw new ErrorMsgException("经度不能为空!");
                }
                accessControlInfo.setLongitude(longitude);
                String latitude = df.format(row.getCell(4).getNumericCellValue());
                if (StrUtil.isBlank(latitude)){
                    throw new ErrorMsgException("纬度不能为空!");
                }
                accessControlInfo.setLatitude(latitude);
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

    private List<String> getDeptIdsByNameAndCode(String deptNames, List<DeptBean> deptBeans) {
        List<String> deptIds=new ArrayList<>();
        String deptcode = deptNames.substring(deptNames.indexOf("(")+1,  deptNames.indexOf(")"));
        for (DeptBean deptBean:deptBeans){
            if (deptcode.equalsIgnoreCase(deptBean.getDeptCode())){
                deptIds.add(deptBean.getId());
                break;
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
                if (StrUtil.isBlank(faceType)){
                    throw new Exception("人脸类型不能为空!");
                }
                if (faceType.equalsIgnoreCase("内部人员")){
                    faceInfo.setFaceType(0);
                }else if (faceType.equalsIgnoreCase("临时人员")){
                    faceInfo.setFaceType(1);
                }
                String userName = getCellValue(row.getCell(1));//人脸名
                if (StrUtil.isBlank(userName)){
                    throw new Exception("人脸名不能为空!");
                }
                faceInfo.setUserName(userName);
                String cardType = getCellValue(row.getCell(2));//证件类型
                if (StrUtil.isBlank(cardType)){
                    throw new Exception("证件类型不能为空!");
                }
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
                String cardId = getCellValue(row.getCell(3));//证件号
                if (StrUtil.isBlank(cardId)){
                    throw new Exception("证件号不能为空!");
                }
                faceInfo.setCardId(cardId);
                String gender = getCellValue(row.getCell(4));//性别
                if (StrUtil.isBlank(gender)){
                    throw new Exception("性别不能为空!");
                }
                if (gender.equalsIgnoreCase("男")){
                    faceInfo.setGender(0);
                }else if (gender.equalsIgnoreCase("女")){
                    faceInfo.setGender(1);
                }
                String birthday = getCellValue(row.getCell(5));//生日
                if (StrUtil.isBlank(birthday)){
                    throw new Exception("生日不能为空!");
                }
                faceInfo.setBirthday(birthday);
                String province = getCellValue(row.getCell(6));//省份
                if (StrUtil.isBlank(province)){
                    throw new Exception("省份不能为空!");
                }
                faceInfo.setProvince(province);
                String city = getCellValue(row.getCell(7));//城市
                if (StrUtil.isBlank(city)){
                    throw new Exception("城市不能为空!");
                }
                faceInfo.setCity(city);
                String faceEndTime = getCellValue(row.getCell(8));//有效日期
                if (StrUtil.isBlank(faceEndTime)){
                    throw new Exception("有效日期不能为空!");
                }
                faceInfo.setFaceEndTime(DateUtil.stringToDate(faceEndTime,"yyyy-MM-dd"));
                String setDeptNames = getCellValue(row.getCell(9));//所属机构编码
                if (StrUtil.isBlank(setDeptNames)){
                    throw new Exception("所属机构为空!");
                }
                RestfulEntityBySummit<List<DeptBean>> allDept = iCbbUserAuthService.queryAllDept();
                List<String> deptCodes=new ArrayList<>();
                for (DeptBean deptBean:allDept.getData()){
                    deptCodes.add(deptBean.getDeptCode());
                }
               String deptCode = setDeptNames.substring(setDeptNames.indexOf("(")+1, setDeptNames.lastIndexOf(")"));
                if (!deptCodes.contains(deptCode)){
                    throw new Exception("所属机构不匹配: "+setDeptNames);
                }
                faceInfo.setDeptNames(setDeptNames);
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
        try
        {
            FileInputStream fis = new FileInputStream(newfile);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192))
                    != -1)
            {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
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
                        }else {
                            throw new Exception("图片格式不对!");
                        }
                    }
                }
            }
        }else {
            throw new Exception("execl文件不存在!");
        }
        if (!CommonUtil.isEmptyList(faceInfos)){
            for (FaceInfo faceInfo:faceInfos){
                if (StrUtil.isBlank(faceInfo.getFaceImage())) {
                    log.error("人脸图片命名和身份证号不匹配!");
                    throw new Exception("人脸图片命名和身份证号不匹配，名字为："+faceInfo.getUserName());
                }
            }
        }
        String zipId = IdWorker.getIdStr();
        Observable.just(faceInfos)
                    .observeOn(Schedulers.io())
                    .subscribe(new Action1<List<FaceInfo>>() {
                        @Override
                        public void call(List<FaceInfo> faceInfos) {
                            if (!CommonUtil.isEmptyList(faceInfos)){
                                List<FaceInfo> faceInfoLibrary = faceInfoManagerDao.selectList(null);
                                FaceUploadZipInfo faceUploadZipInfo=new FaceUploadZipInfo();
                                for (FaceInfo faceInfo:faceInfos){
                                    String faceImagesAbsolutePath = faceInfo.getFaceImage();
                                    String subNewImageBase64 = com.summit.util.FileUtil.imageToBase64Str(faceImagesAbsolutePath);
                                    byte[] subNewImageBase64Byte = FileUtil.readBytes(faceImagesAbsolutePath);
                                    try {
                                        if (!baiduSdkClient.detectFace(subNewImageBase64)) {
                                            faceUploadZipInfo.setUpstate(FaceZipUploadStatus.FaceNoDetected.getCode());
                                            faceUploadZipInfo.setUpDescription(FaceZipUploadStatus.FaceNoDetected.getDescription());
                                            faceUploadZipInfo.setFaceName(faceInfo.getUserName());
                                            genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
                                            return;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    for (FaceInfo face : faceInfoLibrary) {
                                        if (StrUtil.isBlank(face.getFaceImage())) {
                                            continue;
                                        }
                                        String faceImageAbsolutePath = SystemUtil.getUserInfo().getCurrentDir() + face.getFaceImage();
                                        try {
                                            byte[] faceImageBase64 = FileUtil.readBytes(faceImageAbsolutePath);
                                            if (Arrays.equals(subNewImageBase64Byte, faceImageBase64)) {
                                                faceUploadZipInfo.setUpstate(FaceZipUploadStatus.FaceRepeat.getCode());
                                                faceUploadZipInfo.setUpDescription(FaceZipUploadStatus.FaceRepeat.getDescription());
                                                faceUploadZipInfo.setFaceName(faceInfo.getUserName());
                                                genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
                                                return;
                                            }
                                        } catch (IORuntimeException e) {
                                            e.printStackTrace();
                                            log.error("本地人脸库图片丢失,图片路径：" + faceImagesAbsolutePath);
                                        }
                                    }
                                    String faceId = baiduSdkClient.searchFace(subNewImageBase64).getFaceId();
                                    if (StrUtil.isNotBlank(faceId)) {
                                        FaceInfo similarFaceInfo = faceInfoManagerDao.selectById(faceId);
                                        if (similarFaceInfo != null) {
                                            faceUploadZipInfo.setUpstate(FaceZipUploadStatus.FaceSimilar.getCode());
                                            faceUploadZipInfo.setUpDescription(FaceZipUploadStatus.FaceSimilar.getDescription());
                                            faceUploadZipInfo.setFaceName(faceInfo.getUserName());
                                            genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
                                            return;
                                        } else {
                                            faceUploadZipInfo.setUpstate(FaceZipUploadStatus.FaceSimilar.getCode());
                                            faceUploadZipInfo.setUpDescription(FaceZipUploadStatus.FaceSimilar.getDescription());
                                            faceUploadZipInfo.setFaceName(faceInfo.getUserName());
                                            genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
                                            return;
                                        }
                                    }
                                    faceUploadZipInfo.setUpstate(FaceZipUploadStatus.FaceUploading.getCode());
                                    faceUploadZipInfo.setUpDescription(FaceZipUploadStatus.FaceUploading.getDescription());
                                    //faceUploadZipInfo.setFaceName(faceInfo.getUserName());
                                    genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
                                }
                                for (FaceInfo faceInfo:faceInfos){
                                    try {
                                        faceInfoManagerService.insertFaceInfoByExcel(faceInfo);
                                        faceUploadZipInfo.setUpstate(FaceZipUploadStatus.FaceUploading.getCode());
                                        faceUploadZipInfo.setUpDescription(FaceZipUploadStatus.FaceUploading.getDescription());
                                        //faceUploadZipInfo.setFaceName(faceInfo.getUserName());
                                        genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                faceUploadZipInfo.setUpstate(FaceZipUploadStatus.FaceUploadSucess.getCode());
                                faceUploadZipInfo.setUpDescription(FaceZipUploadStatus.FaceUploadSucess.getDescription());
                                faceUploadZipInfo.setFaceName("全部人脸");
                                genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.SECONDS);
                            }
                        }
                    });

            return zipId;

    }
}
