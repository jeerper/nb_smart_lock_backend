package com.summit.utils;

import com.baidu.aip.face.AipFace;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class BaiduSdkClient {

    @Value("${sdk.appId}")
    private String APP_ID;
    @Value("${sdk.apiKey}")
    private String API_KEY;
    @Value("${sdk.secretKey}")
    private String SECRET_KEY;
    @Value("${sdk.groupId}")
    private String groupId;

    private AipFace client;



    @PostConstruct
    public void init() {
        //初始化一个AipFace
        client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        try {
            JSONObject res = client.groupAdd(groupId, null);
            log.debug(res.toString(2));
        }catch (Exception e){
            log.error("SDK初始化异常",e);
        }

    }


    public AipFace getBaiduSdkApi() {
        return client;
    }
}
