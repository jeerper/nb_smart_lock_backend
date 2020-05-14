package com.summit.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtil {

    private static final int CACHE = 10 * 1024;

    public static void main(String[] args) throws IOException {
        String filename = "D:\\faceInfo.zip";
        String path = "D:\\";
        //ZipUtil.unZip(filename, path);
    }


    /*public static void unZip(String sourceFilename, String targetDir) throws IOException {
        unZip2(new File(sourceFilename), targetDir);
    }*/



    /**
     * @param sourcefiles 源文件(服务器上的zip包存放地址)
     * @param decompreDirectory 解压缩后文件存放的目录
     * @throws IOException IO异常
     */
    public static void unzip(String sourcefiles, String decompreDirectory) throws IOException {
        ZipFile readfile = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            //readfile =new ZipFile(sourcefiles);
            readfile = new ZipFile(sourcefiles, Charset.forName("GBK"));
            Enumeration<?> entries = readfile.entries();
            ZipEntry zipEntry = null;
            File credirectory = new File(decompreDirectory);
            credirectory.mkdirs();
            while (entries.hasMoreElements()) {
                zipEntry = (ZipEntry) entries.nextElement();
                String entryName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    String name = zipEntry.getName();
                    name = name.substring(0, name.length() - 1);
                    File  createDirectory = new File(decompreDirectory+ File.separator + name);
                    createDirectory.mkdirs();
                } else {
                    int index = entryName.lastIndexOf("\\");
                    if (index != -1) {
                        File createDirectory = new File(decompreDirectory+ File.separator+ entryName.substring(0, index));
                        createDirectory.mkdirs();
                    }
                    index = entryName.lastIndexOf("/");
                    if (index != -1) {
                        File createDirectory = new File(decompreDirectory + File.separator + entryName.substring(0, index));
                        createDirectory.mkdirs();
                    }
                    File unpackfile = new File(decompreDirectory + File.separator + zipEntry.getName());
                    in = readfile.getInputStream(zipEntry);
                    out = new FileOutputStream(unpackfile);
                    int c;
                    byte[] by = new byte[1024];
                    while ((c = in.read(by)) != -1) {
                        out.write(by, 0, c);
                    }
                    out.flush();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in!=null){
                    in.close();
                }
                if (out!=null){
                    out.close();
                }
                if (readfile != null) {
                    readfile.close();
                }
            } catch (IOException e) {
                log.warn("文件流关闭异常",e);
            }

        }
    }
    /**
     * 将sourceFile解压到targetDir
     * @param sourceFile
     * @param targetDir
     * @throws RuntimeException
     */
    public static void unZip2(File sourceFile, String targetDir) throws IOException {
        long start = System.currentTimeMillis();
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("cannot find the file = " + sourceFile.getPath());
        }
        ZipFile zipFile = null;
        try{
            zipFile = new ZipFile(sourceFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if (entry.isDirectory()) {
                    String dirPath = targetDir + "/" + entry.getName();
                    createDirIfNotExist(dirPath);
                } else {
                    File targetFile = new File(targetDir + "/" + entry.getName());
                    createFileIfNotExist(targetFile);
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = zipFile.getInputStream(entry);
                        fos = new FileOutputStream(targetFile);
                        int len;
                        byte[] buf = new byte[1024];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                    }finally {
                        try{
                            fos.close();
                        }catch (Exception e){
                            log.warn("close FileOutputStream exception", e);
                        }
                        try{
                            is.close();
                        }catch (Exception e){
                            log.warn("close InputStream exception", e);
                        }
                    }
                }
            }
            log.info("解压完成，耗时：" + (System.currentTimeMillis() - start) +" ms");
        } finally {
            if(zipFile != null){
                try {
                    zipFile.close();
                } catch (IOException e) {
                    log.warn("close zipFile exception", e);
                }
            }
        }
    }
    public static void createDirIfNotExist(String path){
        File file = new File(path);
        createDirIfNotExist(file);
    }

    public static void createDirIfNotExist(File file){
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public static void createFileIfNotExist(File file) throws IOException {
        createParentDirIfNotExist(file);
        file.createNewFile();
    }



    public static void createParentDirIfNotExist(File file){
        createDirIfNotExist(file.getParentFile());
    }


    public static void createFile(byte[] bfile, String filePath, String fileName)
    {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try
        {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory())
            {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }
    public static String getDowloadZipPath(String mkdirName){
        String path = ZipUtil.class.getResource("/").getPath();
        path = path + File.separator + mkdirName + File.separator;
        return path;
    }
    public static void downloadFile(String filePath, String dowloadFileName, HttpServletResponse response) {
        //文件的路径
        File file = new File(filePath);
        if(file.exists() && file.isFile()){
            InputStream ins = null;
            // 获取文件输出IO流
            OutputStream outs = null;
            BufferedInputStream bins = null;
            BufferedOutputStream bouts = null;
            try {
                ins = new FileInputStream(filePath);
                outs = response.getOutputStream();
                // 放到缓冲流里面
                bins = new BufferedInputStream(ins);
                bouts = new BufferedOutputStream(outs);
                response.reset();
                // 指定下载的文件名--设置响应头
                response.setHeader("Content-Disposition", "attachment;Filename=" + URLEncoder.encode(dowloadFileName, "UTF-8"));
                response.setContentType("application/x-download;charset=UTF-8");
                int bytesRead = 0;
                byte[] buffer = new byte[CACHE];
                // 开始向网络传输文件流
                while ((bytesRead = bins.read(buffer, 0, CACHE)) != -1) {
                    bouts.write(buffer, 0, bytesRead);
                }
                bouts.flush();
            } catch (IOException e) {
                log.error("文件流出现异常",e);
            }finally {
                if(ins != null){
                    try {
                        ins.close();
                    } catch (IOException e) {
                        log.warn("文件流关闭异常",e);
                    }
                }
                if(bins != null){
                    try {
                        bins.close();
                    } catch (IOException e) {
                        log.warn("文件流关闭异常",e);
                    }
                }
                if(outs != null){
                    try {
                        outs.close();
                    } catch (IOException e) {
                        log.warn("文件流关闭异常",e);
                    }
                }
                if(bouts != null){
                    try {
                        bouts.close();
                    } catch (IOException e) {
                        log.warn("文件流关闭异常",e);
                    }
                }

            }
        }else {
            log.error("下载文件不存在");
        }
    }
    public static boolean createZip(String sourcePath, String zipPath,Boolean isDrop) {
        boolean flag = true;
        try (
                FileOutputStream fos = new FileOutputStream(zipPath);
                ZipOutputStream zos = new ZipOutputStream(fos);
        ){
            writeZip(new File(sourcePath), "", zos,isDrop);
        } catch (Exception e) {
            flag = false;
            log.error("创建ZIP文件失败",e);
        }
        return flag;
    }
    private static void writeZip(File file, String parentPath, ZipOutputStream zos, Boolean isDrop) {
        if(file.exists()){
            // 处理文件夹
            if(file.isDirectory()){
                parentPath+=file.getName()+File.separator;
                File [] files=file.listFiles();
                if(files.length != 0)
                {
                    for(File f:files){
                        writeZip(f, parentPath, zos,isDrop);
                    }
                }
                else
                {       //空目录则创建当前目录
                    try {
                        zos.putNextEntry(new ZipEntry(parentPath));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }else{
                FileInputStream fis=null;
                try {
                    fis=new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte [] content=new byte[1024];
                    int len;
                    while((len=fis.read(content))!=-1){
                        zos.write(content,0,len);
                        zos.flush();
                    }

                } catch (FileNotFoundException e) {
                    log.error("创建ZIP文件失败",e);
                } catch (IOException e) {
                    log.error("创建ZIP文件失败",e);
                }finally{
                    try {
                        if(fis!=null){
                            fis.close();
                        }
                        if(isDrop){
                            clean(file);
                        }
                    }catch(IOException e){
                        log.error("创建ZIP文件失败",e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 清空文件和文件目录
     *
     * @param f
     */
    public static void clean(File f)  {
        String[] cs = f.list();
        if (cs == null || cs.length <= 0) {
            log.debug("delFile:[ " + f + " ]");
            boolean isDelete = f.delete();
            if (!isDelete) {
                log.error("delFile:[ " + f.getName() + "文件删除失败！" + " ]");
                throw new com.summit.exception.ErrorMsgException(f.getName() + "文件删除失败！");
            }
        } else {
            for (int i = 0; i < cs.length; i++) {
                String cn = cs[i];
                String cp = f.getPath() + File.separator + cn;
                File f2 = new File(cp);
                if (f2.exists() && f2.isFile()) {
                    log.error("delFile:[ " + f2 + " ]");
                    boolean isDelete = f2.delete();
                    if (!isDelete) {
                        System.out.println("delFile:[ " + f2.getName() + "文件删除失败！" + " ]");
                        throw new com.summit.exception.ErrorMsgException(f2.getName() + "文件删除失败！");
                    }
                } else if (f2.exists() && f2.isDirectory()) {
                    clean(f2);
                }
            }
            System.out.println("delFile:[ " + f + " ]");
            boolean isDelete = f.delete();
            if (!isDelete) {
                System.out.println("delFile:[ " + f.getName() + "文件删除失败！" + " ]");
                throw new com.summit.exception.ErrorMsgException(f.getName() + "文件删除失败！");
            }
        }
    }
}
