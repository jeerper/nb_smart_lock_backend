package com.summit.sdk.huawei.api;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.summit.sdk.huawei.model.FaceLib;
import springfox.documentation.spring.web.json.Json;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Administrator on 2019/9/3.
 */
public class TestPath {



    public static void main(String[] args) throws IOException {
        ArrayList<FaceLib> students=new ArrayList<>();
        String s1 = new String(new File(".").getCanonicalPath() + "\\entryFaceLib\\entryFaceLib.json");
        String s = readFile(s1);
        JSONObject object=new JSONObject(s);
        JSONArray faceListsArry = object.getJSONArray("FaceListsArry");
        System.out.println(faceListsArry+"aaa");
        for(int i=0;i<faceListsArry.size();i++){
            JSONObject info=faceListsArry.getJSONObject(i);
            System.out.println(info);
            String faceLibName=info.getStr("FaceListName");
            System.out.println(faceLibName);
        }


    }
    public static String readFile(String path){
        BufferedReader  breader=null;
        StringBuffer sbf = new StringBuffer();
        FileInputStream fileInputStream=null;
        InputStreamReader inputStreamReader =null;
        try {
            fileInputStream = new FileInputStream(path);
            inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            breader=new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = breader.readLine()) != null){
                sbf.append(tempString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (breader !=null){
                    breader.close();
                }
                if (inputStreamReader !=null){
                    inputStreamReader.close();
                }
                if (fileInputStream !=null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sbf.toString();
    }
}
