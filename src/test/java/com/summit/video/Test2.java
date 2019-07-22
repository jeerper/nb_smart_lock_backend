package com.summit.video;

import cn.hutool.core.date.DateTime;
import com.summit.sdk.huawei.model.FaceInfo;
import com.sun.jna.NativeLong;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class Test2 {
    private static final ConcurrentHashMap<String, NativeLong> DEVICE_MAP = new ConcurrentHashMap<String, NativeLong>();
    @Test
    public void haha(){

        DEVICE_MAP.put("192.168.141.200",new NativeLong(13));
        DEVICE_MAP.put("192.168.141.201",new NativeLong(12));
        DEVICE_MAP.values().remove(new NativeLong(12));
        System.out.println(DEVICE_MAP);
    }
    @Test
    public void haha1(){
        DateTime time = new DateTime(new Date( 1563357855 * 1000L));
        String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(1563357855*1000L));
        log.debug("抓怕时间:" + time.toString("yyyy-MM-dd HH:mm:ss"));
        log.debug("抓怕时间:" +date);
    }


    @Test
    public void haha2(){
        FaceInfo faceInfo=null;
        faceInfo= create(faceInfo);
        log.debug(""+faceInfo);

    }
    public FaceInfo  create(FaceInfo faceInfo){
       return  new FaceInfo();
    }
}
