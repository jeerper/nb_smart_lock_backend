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
    public static final String FaceLib="facelib";
    public static final String FaceInfo="faceInfo";
    public static final String RealFaceInfo="realfaceInfo";
    public static final String RealFace="realface";
    public static final String UpdateFaceInfo="updatefaceInfo";
    public static final String DelFaceInfo="delfaceInfo";
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
