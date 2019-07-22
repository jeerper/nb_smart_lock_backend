package com.summit.video;

import cn.hutool.core.util.StrUtil;
import com.summit.sdk.huawei.HWPuSDKLibrary;
import com.summit.sdk.huawei.PU_DEVICE_REGISTER_RSP;
import com.summit.sdk.huawei.PU_EVENT_COMMON;
import com.summit.sdk.huawei.PU_EVENT_REGISTER;
import com.summit.sdk.huawei.PU_SYSTEM_TIME;
import com.summit.sdk.huawei.PU_TIME;
import com.summit.sdk.huawei.api.HuaWeiSdkApi;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class Test1 {
    private static final ConcurrentHashMap<String, NativeLong> DEVICE_MAP = new ConcurrentHashMap<String, NativeLong>();
    private long sdkPort = 6060;
    private String sdkUserName = "admin";
    private String sdkPassword = "HuaWei123";
    public HWPuSDKLibrary.pfGetEventInfoCallBack pfGetEventInfoCallBack = new HWPuSDKLibrary.pfGetEventInfoCallBack() {
        @Override
        public void apply(PU_EVENT_COMMON arg) {
            if (arg.enEventType == 2) {
                log.debug("设备主动注册,发送注册消息");
                PU_EVENT_REGISTER registerEvent = new PU_EVENT_REGISTER(arg.getPointer());
                String deviceType = StrUtil.str(registerEvent.szDeviceType, "").trim();
                String deviceId = StrUtil.str(registerEvent.szDeviceId, "").trim().substring(0, 16);
                String deviceIp = StrUtil.str(registerEvent.szDeviceIp, "").trim();
                log.debug("设备类型:" + deviceType);
                log.debug("设备ID:" + deviceId);
                log.debug("设备IP地址:" + deviceIp);
                boolean responseRegisterStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_ResponseDeviceRegister(arg.ulIdentifyID,
                        new PU_DEVICE_REGISTER_RSP(0, new NativeLong(sdkPort)));
                log.debug("响应设备主动注册:" + responseRegisterStatus);
                if (!responseRegisterStatus) {
                    HuaWeiSdkApi.printReturnMsg();
                    return;
                }
                boolean loginStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_LoginByID(arg.ulIdentifyID, sdkUserName, sdkPassword);
                log.debug("设备登录状态:" + loginStatus);
                if (!loginStatus) {
                    HuaWeiSdkApi.printReturnMsg();
                    return;
                }
                //boolean callBackBindStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_AlarmInfoStatesCallBack(pfGetAlarmInfoCallBack);

                DEVICE_MAP.put(deviceIp, arg.ulIdentifyID);
                PU_SYSTEM_TIME time = new PU_SYSTEM_TIME();
                boolean timeGetStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_GetDeviceTime(arg.ulIdentifyID, time);
                log.debug("时间获取状态:" + timeGetStatus);
                if (!timeGetStatus) {
                    HuaWeiSdkApi.printReturnMsg();
                } else {
                    PU_TIME timeBytes = time.stSystime;
                    int year = Integer.valueOf(StrUtil.str(timeBytes.szYear, "").trim());
                    int month = Integer.valueOf(StrUtil.str(timeBytes.szMonth, "").trim());
                    int day = Integer.valueOf(StrUtil.str(timeBytes.szDay, "").trim());
                    int hour = Integer.valueOf(StrUtil.str(timeBytes.szHour, "").trim());
                    int minute = Integer.valueOf(StrUtil.str(timeBytes.szMinute, "").trim());
                    int second = Integer.valueOf(StrUtil.str(timeBytes.szSecond, "").trim());
                    int timeZone = time.lTimeZone.intValue();
                    String timeString = new DateTime(year, month, day, hour + timeZone, minute, second).toString("yyyy-MM-dd HH:mm:ss");
                    log.debug("设备时间:" + timeString);
                }

            }
        }
    };


    @Before
    public void init() {
        System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.debug_load.jna", "true");
        boolean initStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_Init(new NativeLong(3), (String) null, new NativeLong(sdkPort));
        log.debug("SDK加载状态:" + initStatus);
        NativeLongByReference longNative = new NativeLongByReference();
        HWPuSDKLibrary.INSTANCE.IVS_PU_GetVersion(longNative);
        log.debug("SDK版本号:" + longNative.getValue().longValue());
    }

    @Test
    public void test1() {
        boolean callBackBindStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_EventStatesCallBack(pfGetEventInfoCallBack);
        log.debug("回调函数绑定状态:" + callBackBindStatus);

        NativeLong ulIdentifyID = HWPuSDKLibrary.INSTANCE.IVS_PU_Login("192.168.141.141", new NativeLong(sdkPort), sdkUserName, sdkPassword);
        log.debug("用户ID号:" + ulIdentifyID.longValue());
        PU_SYSTEM_TIME time = new PU_SYSTEM_TIME();
        boolean timeGetStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_GetDeviceTime(ulIdentifyID, time);
        log.debug("时间获取状态:" + timeGetStatus);
        if (!timeGetStatus) {
            HuaWeiSdkApi.printReturnMsg();
        } else {
            PU_TIME timeBytes = time.stSystime;
            int year = Integer.valueOf(StrUtil.str(timeBytes.szYear, "").trim());
            int month = Integer.valueOf(StrUtil.str(timeBytes.szMonth, "").trim());
            int day = Integer.valueOf(StrUtil.str(timeBytes.szDay, "").trim());
            int hour = Integer.valueOf(StrUtil.str(timeBytes.szHour, "").trim());
            int minute = Integer.valueOf(StrUtil.str(timeBytes.szMinute, "").trim());
            int second = Integer.valueOf(StrUtil.str(timeBytes.szSecond, "").trim());
            int timeZone = time.lTimeZone.intValue();
            String timeString = new DateTime(year, month, day, hour + timeZone, minute, second).toString("yyyy-MM-dd HH:mm:ss");
            log.debug("设备时间:" + timeString);
        }

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            log.error("执行失败:", e);
        }
    }

    @After
    public void destroy() {
//        HWPuSDKLibrary.INSTANCE.IVS_PU_Logout(deviceIdNative);
        HWPuSDKLibrary.INSTANCE.IVS_PU_Cleanup();
        HuaWeiSdkApi.printReturnMsg();
    }
}
