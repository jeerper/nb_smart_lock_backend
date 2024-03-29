package com.summit.video;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.summit.sdk.huawei.model.FaceInfo;
import com.sun.jna.NativeLong;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Test2 {
    private static final ConcurrentHashMap<String, NativeLong> DEVICE_MAP = new ConcurrentHashMap<String, NativeLong>();

    @Test
    public void haha() {

        DEVICE_MAP.put("192.168.141.200", new NativeLong(13));
        DEVICE_MAP.put("192.168.141.201", new NativeLong(12));
        DEVICE_MAP.values().remove(new NativeLong(12));
        System.out.println(DEVICE_MAP);
    }

    @Test
    public void haha1() {
        DateTime time = new DateTime(new Date(1563357855 * 1000L));
        String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(1563357855 * 1000L));
        log.debug("抓怕时间:" + time.toString("yyyy-MM-dd HH:mm:ss"));
        log.debug("抓怕时间:" + date);
    }


    @Test
    public void haha2() {
        FaceInfo faceInfo = null;
        faceInfo = create(faceInfo);
        log.debug("" + faceInfo);

    }

    @Test
    public void haha3() {

        log.debug(new DateTime(2019, 7, 25, 8, 15, 0).plusHours(8).toString("yyyy-MM-dd HH:mm:ss"));
    }

    public FaceInfo create(FaceInfo faceInfo) {
        return new FaceInfo();
    }


    @Test
    public void haha4(){
        long differMinute= DateUtil.between(new DateTime(2019, 9, 24, 16, 34, 50).toDate(), new Date(), DateUnit.MINUTE,false);
        log.debug("{}",differMinute);
    }
    @Test
    public void haha5(){
        log.debug(RandomUtil.randomStringUpper(6));
    }
}
