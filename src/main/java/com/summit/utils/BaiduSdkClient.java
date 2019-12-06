package com.summit.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.baidu.aip.face.AipFace;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;

@Slf4j
@Component
public class BaiduSdkClient {

    @Value("${sdk.appId}")
    private String APP_ID;
    @Value("${sdk.apiKey}")
    private String API_KEY;
    @Value("${sdk.secretKey}")
    private String SECRET_KEY;

    private String groupId = null;

    private AipFace client;


    @PostConstruct
    public void init() {
        //初始化一个AipFace
        client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        String macAddress = null;
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface inter = networks.nextElement();
                byte[] mac = inter.getHardwareAddress();
                if (mac != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        stringBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    macAddress = stringBuilder.toString();
                    break;
                }
            }
            if (StrUtil.isBlank(macAddress)) {
                throw new Exception("主机没有找到mac地址");
            }
            String ipAddress = SystemUtil.getHostInfo().getAddress();
            log.debug("主机Mac地址:" + macAddress);
            log.debug("主机IP地址:" + ipAddress);
            groupId = StrUtil.replace(ipAddress, ".", "_") + "_" + StrUtil.replace(macAddress, "-", "_");

            JSONObject res = client.groupAdd(groupId, null);
            log.debug(res.toString(2));
        } catch (Exception e) {
            log.error("SDK初始化异常", e);
        }

    }

    public String getGroupId() {
        return groupId;
    }

    public AipFace getBaiduSdkApi() {
        return client;
    }

    /**
     * 人脸图片有效性检测
     *
     * @param base64Str
     * @return
     * @throws Exception
     */
    public boolean detectFace(String base64Str) throws Exception {
        //人脸有效性检测
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "1");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");
        JSONObject res = client.detect(base64Str, "BASE64", options);

        if (StrUtil.isBlank(JSONUtil.parseObj(res.toString()).getStr("result"))) {
            log.debug("没有检测到人脸");
            return false;
        }
        JSONObject result = res.getJSONObject("result");
        int faceNum = Integer.parseInt(result.getString("face_num"));
        if (faceNum != 1) {
            log.error("图片人脸数量不符合要求");
            return false;
        }
        //人脸置信度
        double faceProbability = result.getJSONArray("face_list").getJSONObject(0).getDouble("face_probability");
        if (faceProbability < 0.9d) {
            log.error("图片人脸不符合要求");
            return false;
        }
        return true;
    }

    /**
     * 搜索人脸库中的人脸
     *
     * @param base64Str
     * @return 人脸ID
     */
    public String searchFace(String base64Str) {
        try {
            //在已有人脸库中查找显示的人脸，如果相似率高达90%以上则不能录入
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("max_face_num", "1");
            //人脸匹配阈值为90%
            options.put("match_threshold", "90");
            options.put("quality_control", "NORMAL");
            options.put("liveness_control", "LOW");
            options.put("max_user_num", "1");
            // 人脸搜索
            JSONObject res = client.search(base64Str, "BASE64", groupId, options);
            if (StrUtil.isBlank(JSONUtil.parseObj(res.toString()).getStr("result"))) {
                log.debug("没有检测到人脸");
                return null;
            }
            JSONObject result = res.getJSONObject("result");
            String faceId = result.getJSONArray("user_list").getJSONObject(0).getString("user_id");
            return faceId;
        } catch (Exception e) {
            log.error("人脸搜索失败", e);
            return null;
        }
    }

    /**
     * 人脸录入
     *
     * @param base64Str
     * @param faceId
     * @return ture:录入成功 false:录入失败
     */
    public boolean addFace(String base64Str, String faceId) {
        try {
            // 传入可选参数调用接口
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("quality_control", "NORMAL");
            options.put("liveness_control", "LOW");
            options.put("action_type", "REPLACE");
            JSONObject res = client.addUser(base64Str, "BASE64", groupId, faceId, options);
            log.debug(res.toString(2));
            return true;
        } catch (Exception e) {
            log.error("人脸录入失败", e);
            return false;
        }
    }
    /**
     * 人脸删除
     * @param faceId 人脸ID
     */
    public boolean deleteFace(String faceId){
        try{
            // 删除用户
            client.deleteUser(groupId, faceId, null);
            return true;
        }catch (Exception e){
            log.error("图片删除异常",e);
            return false;
        }
    }


}
