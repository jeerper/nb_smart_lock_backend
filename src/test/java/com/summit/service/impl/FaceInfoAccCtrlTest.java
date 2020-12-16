package com.summit.service.impl;

import com.summit.service.FaceInfoAccCtrlService;
import com.summit.service.FaceInfoManagerService;
import com.summit.util.ExcelLoadData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class FaceInfoAccCtrlTest {
    @Autowired
    private FaceInfoManagerService faceInfoManagerService;
    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;

    @Autowired
    private ExcelLoadData excelLoadData;
    @Autowired
   // FaceAccCtrlCache faceAccCtrlCache;
    @Test
    public void selectFaceInfoByuserName() {
        /*FaceInfo faceInfo = faceInfoManagerService.selectFaceInfoByUserNameAndCardId("ewqe","3242");
        System.out.println(faceInfo);
        FaceInfoAccCtrl faceInfoAccCtrl = faceInfoAccCtrlService.seleAccCtrlInfoByFaceIdAndAccCtlId("1189447681089474561", "ac02");
        System.out.println(faceInfoAccCtrl);
       // faceAccCtrlCache.setSimFaceInfoAccCtrlTime("ac02");*/
        for(int i=0;i<100;i++){
            System.out.println(new Random().nextInt(2));
        }

    }
    @Test
    public void test() throws Exception {
        MultipartFile mulFileByPath = getMulFileByPath("D:\\qianyy\\nb_smart_lock_backend\\snapshot\\1232571339395223554_AccCtrlExportTemplate.xls");
        Map<String, Object> stringObjectMap = excelLoadData.loadExcel(mulFileByPath);
        System.out.println(stringObjectMap);
    }



    private  MultipartFile getMulFileByPath(String picPath) {
        FileItem fileItem = createFileItem(picPath);
        MultipartFile mfile = new CommonsMultipartFile(fileItem);
        return mfile;
    }
    private  FileItem createFileItem(String filePath)
    {
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
