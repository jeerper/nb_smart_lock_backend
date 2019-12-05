package com.summit.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.baidu.aip.face.AipFace;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.NetworkInterface;
import java.util.Enumeration;

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


    public AipFace getBaiduSdkApi() {
        return client;
    }
}
