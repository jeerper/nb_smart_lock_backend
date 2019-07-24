package com.summit.utils;

import com.summit.sdk.huawei.api.HuaWeiSdkApi;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
        huaWeiSdkApi = new HuaWeiSdkApi(port, userName, password, localhost,clientFaceInfoCallback);
        huaWeiSdkApi.init();
    }

    @PreDestroy
    public void destroy() {
        huaWeiSdkApi.destroy();
    }

    public HuaWeiSdkApi getHuaWeiSdkApi() {
        return huaWeiSdkApi;
    }
}
