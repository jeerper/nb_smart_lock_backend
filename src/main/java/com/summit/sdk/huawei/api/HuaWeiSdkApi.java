package com.summit.sdk.huawei.api;

import com.summit.sdk.huawei.HWPuSDKLibrary;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import com.summit.sdk.huawei.callback.EventInfoCallBack;
import com.summit.sdk.huawei.model.DeviceInfo;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HuaWeiSdkApi {

    private static final ConcurrentHashMap<String, DeviceInfo> DEVICE_MAP = new ConcurrentHashMap<>();

    private long sdkPort;

    private String sdkUserName;

    private String sdkPassword;
    private String  sdkLocalhost;
    private HWPuSDKLibrary.pfGetEventInfoCallBack pfGetEventInfoCallBack;
    private ClientFaceInfoCallback clientFaceInfoCallback;

    public HuaWeiSdkApi(long sdkPort, String sdkUserName, String sdkPassword,String sdkLocalhost, ClientFaceInfoCallback clientFaceInfoCallback) {
        this.sdkPort = sdkPort;
        this.sdkUserName = sdkUserName;
        this.sdkPassword = sdkPassword;
        this.sdkLocalhost=sdkLocalhost;
        this.clientFaceInfoCallback = clientFaceInfoCallback;
    }

    public static void printReturnMsg() {
        NativeLong errorCode = HWPuSDKLibrary.INSTANCE.IVS_PU_GetLastError();
        String errorMsg = HWPuSDKLibrary.INSTANCE.IVS_PU_GetErrorMsg(errorCode);
        log.debug("返回码:{},返回信息:{}", errorCode.longValue(), errorMsg);
    }

    /**
     * SDK初始化
     */
    public void init() {
        System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.debug_load.jna", "true");
        boolean initStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_Init(new NativeLong(3), (String) null, new NativeLong(sdkPort));
        log.debug("SDK加载状态:" + initStatus);
        if (!initStatus) {
            printReturnMsg();
        }
        NativeLongByReference longNative = new NativeLongByReference();
        HWPuSDKLibrary.INSTANCE.IVS_PU_GetVersion(longNative);
        log.debug("SDK版本号:" + longNative.getValue().longValue());
        //SDK注册事件回调
        pfGetEventInfoCallBack = new EventInfoCallBack(sdkPort, sdkUserName, sdkPassword, sdkLocalhost,clientFaceInfoCallback, DEVICE_MAP);
        boolean callBackBindStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_EventStatesCallBack(pfGetEventInfoCallBack);
        log.debug("注册事件回调函数绑定:" + callBackBindStatus);
        if (!callBackBindStatus) {
            printReturnMsg();
        }

    }

    /**
     * SDK销毁
     */
    public void destroy() {
        Iterator<Map.Entry<String, DeviceInfo>> iter = DEVICE_MAP.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, DeviceInfo> entry = iter.next();
            NativeLong ulIdentifyId = entry.getValue().getUlIdentifyId();
            HWPuSDKLibrary.INSTANCE.IVS_PU_StopAllRealPlay(ulIdentifyId);
            HWPuSDKLibrary.INSTANCE.IVS_PU_Logout(ulIdentifyId);
        }
        HWPuSDKLibrary.INSTANCE.IVS_PU_Cleanup();
        printReturnMsg();
    }

    public boolean rebootCamera(String cameraIp) {
        DeviceInfo deviceInfo = DEVICE_MAP.get(cameraIp);
        if (deviceInfo == null) {
            log.debug("设备没有上线");
            return false;
        }
        boolean rebootStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_Reboot(deviceInfo.getUlIdentifyId());
        log.debug("设备重启状态:" + rebootStatus);
        if (!rebootStatus) {
            printReturnMsg();
        }
        DEVICE_MAP.remove(cameraIp);
        return rebootStatus;
    }
}
