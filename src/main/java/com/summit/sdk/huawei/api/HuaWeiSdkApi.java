package com.summit.sdk.huawei.api;

import cn.hutool.core.io.FileUtil;
import com.summit.sdk.huawei.HWPuSDKLibrary;
import com.summit.sdk.huawei.HWPuSDKLinuxLibrary;
import com.summit.sdk.huawei.PU_CERT_FILE_PATH_PARA;
import com.summit.sdk.huawei.callback.ClientFaceInfoCallback;
import com.summit.sdk.huawei.callback.EventInfoCallBack;
import com.summit.sdk.huawei.callback.linux.EventInfoLinuxCallBack;
import com.summit.sdk.huawei.model.DeviceInfo;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.ptr.NativeLongByReference;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HuaWeiSdkApi {
    public HuaWeiSdkApi(){}
    public static final ConcurrentHashMap<String, DeviceInfo> DEVICE_MAP = new ConcurrentHashMap<>();

    private long sdkPort;

    private String sdkUserName;

    private String sdkPassword;
    private String sdkLocalhost;
    private HWPuSDKLibrary.pfGetEventInfoCallBack pfGetEventInfoCallBack;
    private HWPuSDKLinuxLibrary.pfGetEventInfoCallBack  pfGetEventInfoLinuxCallBack;
    private ClientFaceInfoCallback clientFaceInfoCallback;

    public HuaWeiSdkApi(long sdkPort, String sdkUserName, String sdkPassword, String sdkLocalhost, ClientFaceInfoCallback clientFaceInfoCallback) {
        this.sdkPort = sdkPort;
        this.sdkUserName = sdkUserName;
        this.sdkPassword = sdkPassword;
        this.sdkLocalhost = sdkLocalhost;
        this.clientFaceInfoCallback = clientFaceInfoCallback;
    }

    public static long printReturnMsg() {
        if (Platform.isWindows()) {
            NativeLong errorCode = HWPuSDKLibrary.INSTANCE.IVS_PU_GetLastError();
            String errorMsg = HWPuSDKLibrary.INSTANCE.IVS_PU_GetErrorMsg(errorCode);
            log.debug("返回码:{},返回信息:{}", errorCode.longValue(), errorMsg);
            return errorCode.longValue();
        } else {
            NativeLong errorCode = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetLastError();
            String errorMsg = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetErrorMsg(errorCode);
            log.debug("返回码:{},返回信息:{}", errorCode.longValue(), errorMsg);
            return errorCode.longValue();
        }
    }

    /**
     * SDK初始化
     */
    public void init() throws IOException {
        System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.debug_load.jna", "true");
        boolean initStatus;
        PU_CERT_FILE_PATH_PARA pstCertFilePath=new PU_CERT_FILE_PATH_PARA();
        String cerabsolutePath = FileUtil.getAbsolutePath("cert/cacert.cer");
        String pemabsolutePath = FileUtil.getAbsolutePath("cert/cert.pem");
        String keyabsolutePath = FileUtil.getAbsolutePath("cert/key.pem");
        pstCertFilePath.szCACertFilePath=Arrays.copyOf(cerabsolutePath.getBytes(),512);
        pstCertFilePath.szCertFilePath=Arrays.copyOf(pemabsolutePath.getBytes(),512);
        pstCertFilePath.szKeyFilePath=Arrays.copyOf(keyabsolutePath.getBytes(),512);
        pstCertFilePath.szKeyPasswd="715AO1FEC11AD58A".getBytes();
        if (Platform.isWindows()) {
            initStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_InitEx(new NativeLong(3), (ByteBuffer) null,
                    new NativeLong(6060),new NativeLong(sdkPort),pstCertFilePath);
        } else {
            initStatus = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_InitEx(new NativeLong(3), (ByteBuffer) null,
                    new NativeLong(6060),new NativeLong(sdkPort),pstCertFilePath);
        }
        log.debug("SDK加载状态:" + initStatus);
        if (!initStatus) {
            printReturnMsg();
        }
        NativeLongByReference longNative = new NativeLongByReference();
        if (Platform.isWindows()) {
            HWPuSDKLibrary.INSTANCE.IVS_PU_GetVersion(longNative);
        } else {
            HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_GetVersion(longNative);
        }
        log.debug("SDK版本号:" + longNative.getValue().longValue());
        //SDK注册事件回调
        boolean callBackBindStatus;
        if(Platform.isWindows()){
            pfGetEventInfoCallBack = new EventInfoCallBack(sdkPort, sdkUserName, sdkPassword, sdkLocalhost, clientFaceInfoCallback, DEVICE_MAP);
            callBackBindStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_EventStatesCallBack(pfGetEventInfoCallBack);
        }else{
            pfGetEventInfoLinuxCallBack = new EventInfoLinuxCallBack(sdkPort, sdkUserName, sdkPassword, sdkLocalhost, clientFaceInfoCallback, DEVICE_MAP);
            callBackBindStatus = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_EventStatesCallBack(pfGetEventInfoLinuxCallBack);
        }
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
            if (Platform.isWindows()) {
                HWPuSDKLibrary.INSTANCE.IVS_PU_StopAllRealPlay(ulIdentifyId);
                HWPuSDKLibrary.INSTANCE.IVS_PU_Logout(ulIdentifyId);
            } else {
                HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_StopAllRealPlay(ulIdentifyId);
                HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_Logout(ulIdentifyId);
            }
        }
        if (Platform.isWindows()) {
            HWPuSDKLibrary.INSTANCE.IVS_PU_Cleanup();
        } else {
            HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_Cleanup();
        }
        printReturnMsg();
    }

    public boolean rebootCamera(String cameraIp) {
        DeviceInfo deviceInfo = DEVICE_MAP.get(cameraIp);
        if (deviceInfo == null) {
            log.debug("设备没有上线");
            return false;
        }
        boolean rebootStatus;
        if (Platform.isWindows()) {
            rebootStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_Reboot(deviceInfo.getUlIdentifyId());
        } else {
            rebootStatus = HWPuSDKLinuxLibrary.INSTANCE.IVS_PU_Reboot(deviceInfo.getUlIdentifyId());
        }
        log.debug("设备重启状态:" + rebootStatus);

        if (!rebootStatus) {
            printReturnMsg();
        }
        DEVICE_MAP.remove(cameraIp);
        return rebootStatus;
    }


}
