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
        String s1 = new String(new File(".").getCanonicalPath() + "\\facelib\\a.json");
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
        BufferedReader reader=null;
        String lastStr="";
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader=new BufferedReader(inputStreamReader);
            String tempString=null;
            while ((tempString = reader.readLine()) != null){
                lastStr+=tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastStr;
    }
}
