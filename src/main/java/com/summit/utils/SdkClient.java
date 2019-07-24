package com.summit.utils;

import com.summit.sdk.huawei.api.HuaWeiSdkApi;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class SdkClient {
    @Autowired
    ClientFaceInfoCallback clientFaceInfoCallback;
    private HuaWeiSdkApi huaWeiSdkApi;
    @Value("${sdk.port}")
    private int port;
    @Value("${sdk.userName}")
    private String userName;
    @Value("${sdk.password}")
    private String password;
    @Value("${sdk.localhost}")
    private String localhost;

    @PostConstruct
    public void init() {
        try {
            huaWeiSdkApi = new HuaWeiSdkApi(port, userName, password, localhost, clientFaceInfoCallback);
            huaWeiSdkApi.init();
        } catch (Exception e) {
            log.error("错误", e);
        }

    }

    @PreDestroy
    public void destroy() {
        huaWeiSdkApi.destroy();
    }

    public HuaWeiSdkApi getHuaWeiSdkApi() {
        return huaWeiSdkApi;
    }
}
