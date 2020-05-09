package com.summit.util;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.summit.exception.ErrorMsgException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author cy
 * @date 2020年04月15日 8:54
 * Version 1.0
 */
@Slf4j
public class EasyExcelUtil {

    private static String EXCEL_TYPE_XLS = ".xls";
    private static String EXCEL_TYPE_XLSX = ".xlsx";

    /**
     * Description:   根据模板生成新的excel 只针对单个sheet,不存储任何数据,和excel 下载的
     *                区别是,可以指定 sheet 名称
     *
     * @param path:  模板路径
     * @param sheetName:  sheet 名称
     * @return byte[]
     * @author cy
     * @date  2020/4/15 9:14
     */
    public static byte[] exportSingleAccCtrlByTemplate(String path,String sheetName,String[] dataSource,int firstCol) {
        log.debug("门禁模板路径:"+path);
        File file = new File(path);
        if(!file.exists() || !file.isFile()){
            log.error("文件不存在:{}",path);
            throw new ErrorMsgException("文件不存在");
        }
        if(!path.endsWith(EXCEL_TYPE_XLS) && !path.endsWith(EXCEL_TYPE_XLSX)){
            log.error("文件格式有误");
            throw new ErrorMsgException("文件格式有误,不是excel格式");
        }
        FileInputStream in = null;
        Workbook wb = null;
        try {
            in = new FileInputStream(path);
            // 根据版本进行不同创建
            if(path.endsWith(EXCEL_TYPE_XLS)){
                 wb = new HSSFWorkbook(in);//创建excel表
            }else{
                 wb = new XSSFWorkbook(in);//创建excel表
            }
            // 设置名称
            wb.setSheetName(0,sheetName);
            Sheet sheet = wb.getSheetAt(0);
            if (dataSource !=null){
                DataValidation dataValidation = createDataValidation(sheet, dataSource, firstCol);
                sheet.addValidationData(dataValidation);
            }
            //sheet.addValidationData(data_Validation);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            log.error("excel 根据模板导入发生了错误", ex);
            throw new ErrorMsgException("excel 根据模板导入发生了错误");
        } finally{
            try {
                if(null != in){
                    in.close();
                }
            } catch (IOException ex) {
                log.error("excel 发生错误", ex);
            } finally{
                try {
                    if(null != wb){
                        wb.close();
                    }
                } catch (IOException ex) {
                    log.error("excel 发生错误", ex);
                }
            }
        }
    }

    private static HSSFDataValidation getDataValidationList4Col(HSSFSheet sheet,  String[] dataArray, int col, HSSFWorkbook wbCreat) {
        HSSFSheet hidden = wbCreat.createSheet("hidden");
        HSSFCell cell = null;
        for (int i = 0, length = dataArray.length; i < length; i++)
        {
            String name = dataArray[i];
            HSSFRow row = hidden.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(name);
        }

        Name namedCell = wbCreat.createName();
        namedCell.setNameName("hidden");
        namedCell.setRefersToFormula("hidden!$A$1:$A$" + dataArray.length);
        //加载数据,将名称为hidden的
        DVConstraint constraint = DVConstraint.createFormulaListConstraint("hidden");

        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList addressList = new CellRangeAddressList(2, 65535, col, col);
        HSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
        //将第二个sheet设置为隐藏
        wbCreat.setSheetHidden(1, true);
        if (null != validation)
        {
            sheet.addValidationData(validation);
        }
        return validation;
    }

    public static DataValidation createDataValidation(Sheet sheet, String[] dataSource, int col) {
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(2, 65535, col, col);

        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(dataSource);

        DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);


        //处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }

        dataValidation.setEmptyCellAllowed(true);
        dataValidation.setShowPromptBox(true);
        dataValidation.createPromptBox("提示", "只能选择下拉框里面的数据");
        return dataValidation;
    }


    /**
     * Description:   根据模板生成新的excel 只针对单个sheet,并填充数据
     *
     * @param list:  数据集合 是按照对象字段反射出来执行的
     * @param path:  模板路径
     * @param sheetName:  sheet 名称
     * @param startRow:  从第几行开始
     * @return byte[]
     * @author cy
     * @date  2020/4/15 9:14
     */
    public static byte[] exportSingleExcelDataByTemplate(List list,Class objClass,String path, String sheetName,int startRow) throws Exception {
        File file = new File(path);
        if(!file.exists() || !file.isFile()){
            log.error("文件不存在:{}",path);
            throw new ErrorMsgException("文件不存在");
        }
        if(!path.endsWith(EXCEL_TYPE_XLS) && !path.endsWith(EXCEL_TYPE_XLSX)){
            log.error("文件格式有误");
            throw new ErrorMsgException("文件格式有误,不是excel格式");
        }
        FileInputStream in = new FileInputStream(path);
        // 根据版本进行不同创建
        Workbook wb;
        if(path.endsWith(EXCEL_TYPE_XLS)){
            wb = new HSSFWorkbook(in);//创建excel表
        }else{
            wb = new XSSFWorkbook(in);//创建excel表
        }
        // 设置名称
        wb.setSheetName(0,sheetName);
        Sheet sheet = wb.getSheetAt(0);
        Cell hc;
        ByteArrayOutputStream out = null;
        try {
            if(list != null){
                // 反射出来字段
                Field[] fields = objClass.getDeclaredFields();
                int i = startRow;
                for (Object obj : list) {
                    Row rowBody = sheet.createRow(i);
                    int j = 0;
                    for (Field f : fields) {
                        f.setAccessible(true);
                        Object va = f.get(obj);
                        if (null == va) {
                            va = "";
                        }
                        hc = rowBody.createCell(j);
                        hc.setCellValue(va + "");
                        j++;
                    }
                    i++;
                }
            }
            out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            log.error("excel 根据模板导入发生了错误", ex);
            throw new ErrorMsgException("excel 根据模板导入发生了错误");
        } finally{
            try {
                if(null != out){
                    out.close();
                }
            } catch (IOException ex) {
                log.error("excel 发生错误", ex);
            } finally{
                try {
                    if(null != wb){
                        wb.close();
                    }
                } catch (IOException ex) {
                    log.error("excel 发生错误", ex);
                }
            }
        }
    }


    /**
     * Description: 类上必须要有  @Excel(name = "") 注解 name 的名称就是excel头部名称不能省略
     *
     * @param skipHead: 跳过多少行
     * @param clazz:  类
     * @param bye:
     * @return java.util.List<org.apache.poi.ss.formula.functions.T>
     * @author cy
     * @date  2020/4/15 16:00
     */
    public static List importExcelByAnnotation(int skipHead,Class clazz,byte[] bye) throws Exception {
        ByteArrayInputStream byteArrayInputStream=null;
        List list=null;
        try{
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(skipHead);
            byteArrayInputStream = new ByteArrayInputStream(bye);
            list = ExcelImportUtil.importExcel(byteArrayInputStream, clazz, params);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
           if (null!= byteArrayInputStream){
               byteArrayInputStream.close();
           }
        }
        return list;

    }

    public static byte[] exportSingleFaceInfoByTemplate(String path, Map<Integer, String[]> map,String sheetName) {
        log.debug("门禁模板路径:"+path);
        File file = new File(path);
        if(!file.exists() || !file.isFile()){
            log.error("文件不存在:{}",path);
            throw new ErrorMsgException("文件不存在");
        }
        if(!path.endsWith(EXCEL_TYPE_XLS) && !path.endsWith(EXCEL_TYPE_XLSX)){
            log.error("文件格式有误");
            throw new ErrorMsgException("文件格式有误,不是excel格式");
        }
        FileInputStream in = null;
        Workbook wb = null;
        try {
            in = new FileInputStream(path);
            // 根据版本进行不同创建
            if(path.endsWith(EXCEL_TYPE_XLS)){
                wb = new HSSFWorkbook(in);//创建excel表
            }else{
                wb = new XSSFWorkbook(in);//创建excel表
            }
            // 设置名称
            wb.setSheetName(0,sheetName);
            Sheet sheet = wb.getSheetAt(0);
            String[] faceType = map.get(0);
            DataValidation faceType_Validation = createDataValidation(sheet, faceType, 0);
            String[] sex = map.get(1);
            DataValidation sex_Validation = createDataValidation(sheet, sex, 4);
            sheet.addValidationData(faceType_Validation);
            sheet.addValidationData(sex_Validation);
            String[] cardType = map.get(2);
            DataValidation cardType_Validation = createDataValidation(sheet, cardType, 2);
            sheet.addValidationData(cardType_Validation);
            String[] provinces = map.get(3);
            DataValidation provinces_Validation = createDataValidation(sheet, provinces, 6);
            sheet.addValidationData(provinces_Validation);
            String[] deptNames = map.get(4);
            DataValidation deptNames_Validation = createDataValidation(sheet, deptNames, 9);
            sheet.addValidationData(deptNames_Validation);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            log.error("excel 根据模板导入发生了错误", ex);
            throw new ErrorMsgException("excel 根据模板导入发生了错误");
        } finally{
            try {
                if(null != in){
                    in.close();
                }
            } catch (IOException ex) {
                log.error("excel 发生错误", ex);
            } finally{
                try {
                    if(null != wb){
                        wb.close();
                    }
                } catch (IOException ex) {
                    log.error("excel 发生错误", ex);
                }
            }
        }
    }

   /* public static void main(String[] args) throws Exception {
        byte[] export = exportSingleByTemplate("D:\\a1.xls","1");
        List list = importExcelByAnnotation(1, MyTest.class, export);

        System.out.println(list.toString());
    }*/


}
