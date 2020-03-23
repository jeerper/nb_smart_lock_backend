package com.summit.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FileUtil {
    private static Log log = LogFactory.getLog(FileUtil.class);

    @Value("${localhost.uploadfile.url}")
    private static  String uploadFilePath;

    public static String upload(String filePath, MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            String orginFileName = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "" + orginFileName.substring(orginFileName.lastIndexOf("."));
            File saveFile = new File(filePath + fileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));) {
                out.write(file.getBytes());
                out.flush();
                out.close();
                return saveFile.getName();
            } catch (Exception e) {
                log.error("文件未找到！");
                throw new FileNotFoundException("日期格式有误");
            }
        }
        return "";
    }

    /**
     * 多文件上传
     *
     * @param filePath
     * @return
     */

    public static String uploadFiles(String filePath, MultipartHttpServletRequest request) throws IOException {
        File savePath = new File(filePath);
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        List<MultipartFile> files = request.getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    String orginFileName = file.getOriginalFilename();
                    String fileName = UUID.randomUUID() + "" + orginFileName.substring(orginFileName.lastIndexOf("."));
                    File saveFile = new File(savePath, fileName);
                    stream = new BufferedOutputStream(new FileOutputStream(saveFile));
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    if (stream != null) {
                        stream.close();
                        stream = null;
                    }
                    return "第 " + i + " 个文件上传有错误" + e.getMessage();
                }
            } else {
                return "第 " + i + " 个文件为空";
            }
        }
        return "所有文件上传成功";
    }

    public static List<JSONObject> uploadFilesList(String filePath, MultipartFile[] uploadFile) throws IOException {

        File savePath = new File(filePath);
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        List<JSONObject> list = new ArrayList<JSONObject>();
        JSONObject json = null;
        if (uploadFile != null && uploadFile.length > 0) {
            for (int i = 0; i < uploadFile.length; ++i) {
                file = uploadFile[i];
                if (!file.isEmpty()) {
                    json = new JSONObject();
                    try {
                        byte[] bytes = file.getBytes();
                        String orginFileName = file.getOriginalFilename();
                        String key = UUID.randomUUID().toString().replace("-", "");
                        String fileName = key + "" + orginFileName.substring(orginFileName.lastIndexOf("."));
                        // System.out.println("savePath:"+savePath);
                        File saveFile = new File(savePath, fileName);
                        log.error("上传文件路径："+saveFile.getPath());
                        stream = new BufferedOutputStream(new FileOutputStream(saveFile));
                        stream.write(bytes);
                        stream.close();

                        json.put("idFileName", fileName);
                        json.put("orginFileName", orginFileName);
                        list.add(json);
                    } catch (Exception e) {
                        if (stream != null) {
                            stream.close();
                            stream = null;
                        }

                    }
                }
            }
        }

        return list;
    }

    /*
     * 上传图片
     */
    public static String uploadImgs(String filePath, MultipartFile file, HttpServletRequest request) throws IOException {
        //File savePath = new File(request.getSession().getServletContext().getRealPath("/upload/"));
        File savePath = new File(filePath);
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file2 = null;
        BufferedOutputStream stream = null;
        if (files.size() > 10) {// 限制10张图片
            return "";
        }
        StringBuffer sb = new StringBuffer();
        String fileName;
        for (int i = 0; i < files.size(); ++i) {
            file2 = files.get(i);
            if (!file2.isEmpty()) {
                try {
                    byte[] bytes = file2.getBytes();
                    String orginFileName = file2.getOriginalFilename();
                    fileName = UUID.randomUUID().toString().replace("-", "") + "" + orginFileName.substring(orginFileName.lastIndexOf("."));
                    File saveFile = new File(savePath, fileName);
                    stream = new BufferedOutputStream(new FileOutputStream(saveFile));
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    if (stream != null) {
                        stream.close();
                        stream = null;
                    }
                    return "";
                }
            } else {
                return "";
            }
            sb.append(fileName);
            sb.append(",");
        }
        String imgName = sb.substring(0, sb.length() - 1);// 去掉最后一个,
        return imgName;
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }


    /**
     * 单个文件上传
     * @param filePath
     * @param uploadFile
     * @return
     * @throws IOException
     */
    public static JSONObject uploadFile(String filePath, MultipartFile uploadFile) throws IOException {
        // 获取原始名字
        String orginFileName = uploadFile.getOriginalFilename();
        // 文件重命名，防止重复
        String key = UUID.randomUUID().toString().replace("-", "");
        String fileName = key + "" + orginFileName.substring(orginFileName.lastIndexOf("."));
        fileName = filePath + fileName;
        String fileUrl=uploadFilePath+filePath;
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("fileName",fileName);
        jsonObject.put("fileUrl",fileUrl);
        jsonObject.put("orginFileName",orginFileName);
        // 文件对象
        File dest = new File(fileName);
        // 判断路径是否存在，如果不存在则创建
        if(!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            // 保存到服务器中
            uploadFile.transferTo(dest);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
