package com.summit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan("com.summit.dao")
public class MainAction {

    public static final String SnapshotFileName = "snapshot";
    public static final String FaceRecognitionFileName="face-recognition";
    public static final String FACE_AUTH_CACHE_PREFIX = "face_auth:";
    public static final String FACE_ID = "face_id";
    public static final String CHANGE_LOCK_PSW_LOCK_PREFIX="change_lock_password_lock:";
    public static final String FaceTemplateZip = "faceTemplateZip";
    //public static final String AccCtrlExportTemplate="accCtrlExportTemplate";

    public static void main(String[] args) {
        SpringApplication.run(MainAction.class, args);
    }


}
