package com.summit.util;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.LockInfo;
import com.summit.exception.ErrorMsgException;
import com.summit.service.LockInfoService;
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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class ExcelUtil {
    private static final String XLS = "xls";
    private static final String XLSK = "xlsx";
    @Autowired
    private LockInfoService lockInfoService;


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
        Sheet sheet = workbook.getSheet("门禁信息导出模板");
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
                //String access_control_id = getCellValue(row.getCell(0));
                accessControlInfo.setAccessControlId(IdWorker.getIdStr());
                String access_control_name = getCellValue(row.getCell(1));
                accessControlInfo.setAccessControlName(access_control_name);
                //String lock_id = getCellValue(row.getCell(2));
                lockInfo.setLockId(IdWorker.getIdStr());
                accessControlInfo.setLockId(lockInfo.getLockId());
                String lock_code = getCellValue(row.getCell(3));
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
                }
                accessControlInfo.setLockCode(lock_code);
                String entry_camera_id = getCellValue(row.getCell(4));
                accessControlInfo.setEntryCameraId(entry_camera_id);
                String entry_camera_ip = getCellValue(row.getCell(5));
                accessControlInfo.setEntryCameraIp(entry_camera_ip);
                String exit_camera_id = getCellValue(row.getCell(6));
                accessControlInfo.setExitCameraId(exit_camera_id);
                String exit_camera_ip = getCellValue(row.getCell(7));
                accessControlInfo.setExitCameraIp(exit_camera_ip);
                String status = getCellValue(row.getCell(8));
                accessControlInfo.setStatus(Integer.parseInt(status));
                String createby = getCellValue(row.getCell(9));
                accessControlInfo.setCreateby(createby);
                String createtime = getCellValue(row.getCell(10));
                accessControlInfo.setCreatetime(DateUtil.stringToDate(createtime));
                lockInfo.setCreatetime(DateUtil.stringToDate(createtime));
                String updatetime = getCellValue(row.getCell(11));
                accessControlInfo.setUpdatetime(DateUtil.stringToDate(updatetime));
                lockInfo.setUpdatetime(DateUtil.stringToDate(updatetime));
                String longitude = getCellValue(row.getCell(12));
                accessControlInfo.setLongitude(longitude);
                String latitude = getCellValue(row.getCell(13));
                accessControlInfo.setLatitude(latitude);
                lockInfo.setCurrentPassword("111111");
                lockInfo.setNewPassword(RandomUtil.randomStringUpper(6));
                accessControlInfos.add(accessControlInfo);
                lockInfos.add(lockInfo);
            }
        }
        map.put("accessControlInfos",accessControlInfos);
        map.put("lockInfos",lockInfos);
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
}
