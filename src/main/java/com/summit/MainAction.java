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

//    @Autowired
//    SimpMessagingTemplate messagingTemplate;
    public static final String SnapshotFileName="snapshot";
    public static final String ExitFaceLib="exitfaceLib";
    public static final String EntryFaceLib="entryFaceLib";
    public static final String ExitNewFaceLib="exitNewFaceLib";
    public static final String EntryNewFaceLib="entryNewFaceLib";
    public static final String ExitFaceInfo="exitfaceInfo";
    public static final String EntrytFaceInfo="entryfaceInfo";
    public static final String UpdateAddFaceLib="updateAddFaceLib";
    public static final String UpdateFaceLib="updateFaceLib";
    public static final String UpdateFaceInfo="updatefaceInfo";
    public static final String DelExitFaceInfo="delExitfaceInfo";
    public static final String DelEntryFaceInfo="delEntryfaceInfo";
    public static final String DelExitFaceLib="delExitFaceLib";
    public static final String DelEntryFaceLib="delEntryFaceLib";
    public static final String TemporaryEntryFaceLib="temporaryEntryFaceLib";//临时入口人脸库
    public static final String TemporaryExitFaceLib="temporaryExitFaceLib";//临时出口人脸库
    public static final String InnerEntryFaceLib="innerEntryFaceLib";//内部的入口人脸库
    public static final String InnerExitFaceLib="innerExitFaceLib";//内部的出口人脸库
    public static final String TemporaryEntryRealFace="temporaryEntryRealFace";//临时入口人脸信息
    public static final String TemporaryExitRealFace="temporaryExitRealFace";//临时出口人脸信息
    public static final String InnerEntryRealFaceInfo="innerEntryRealFaceInfo";//内部入口人脸信息
    public static final String InnerExitRealFaceInfo="innerExitRealFaceInfo";//内部出口人脸信息
    public static void main(String[] args) {
        SpringApplication.run(MainAction.class, args);
    }

//    @Scheduled(fixedDelay = 1000L)
//    public void time() {
//        messagingTemplate.convertAndSend("/topic/realTimeInfoList", "haha");
//    }
//    @MessageMapping("/realTimeInfo")
    public void realTimeInfo(String message){
        System.out.println(message);
    }
}
