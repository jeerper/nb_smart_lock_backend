package com.summit.sdk.huawei.callback;

import cn.hutool.core.util.StrUtil;
import com.summit.sdk.huawei.HWPuSDKLibrary;
import com.summit.sdk.huawei.PU_DEVICE_REGISTER_RSP;
import com.summit.sdk.huawei.PU_EVENT_COMMON;
import com.summit.sdk.huawei.PU_EVENT_REGISTER;
import com.summit.sdk.huawei.PU_REAL_PLAY_INFO;
import com.summit.sdk.huawei.PU_SYSTEM_TIME;
import com.summit.sdk.huawei.PU_TIME;
import com.summit.sdk.huawei.api.HuaWeiSdkApi;
import com.summit.sdk.huawei.model.DeviceInfo;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class EventInfoCallBack implements HWPuSDKLibrary.pfGetEventInfoCallBack {

    private long sdkPort;
    private String sdkUserName;
    private String sdkPassword;
    private String sdkLocalhost;
    private ConcurrentHashMap<String, DeviceInfo> deviceMap;
    private HWPuSDKLibrary.pfGetAlarmInfoCallBack pfGetAlarmInfoCallBack;
    private HWPuSDKLibrary.pfRealDataCallBack pfRealDataCallBack;


    public EventInfoCallBack(long sdkPort, String sdkUserName, String sdkPassword, String sdkLocalhost,
                             ClientFaceInfoCallback clientFaceInfoCallback, ConcurrentHashMap<String, DeviceInfo> deviceMap) {
        this.sdkPort = sdkPort;
        this.sdkUserName = sdkUserName;
        this.sdkPassword = sdkPassword;
        this.sdkLocalhost = sdkLocalhost;
        this.deviceMap = deviceMap;
        this.pfGetAlarmInfoCallBack = new AlarmInfoCallBack();
        this.pfRealDataCallBack = new RealDataCallBack(clientFaceInfoCallback);
    }

    @Override
    public void apply(PU_EVENT_COMMON arg) {
        switch (arg.enEventType) {
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_CONNCET:
                log.debug("设备主动连接，网络连接上");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_REGISTER:
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
                    break;
                }
                boolean loginStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_LoginByID(arg.ulIdentifyID, sdkUserName, sdkPassword);
                log.debug("设备登录状态:" + loginStatus);
                if (!loginStatus) {
                    HuaWeiSdkApi.printReturnMsg();
                    break;
                }


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

                Pointer deviceIpPointer = new Memory(1024);
                deviceIpPointer.setString(0, deviceIp);
                deviceMap.put(deviceIp, new DeviceInfo(deviceIpPointer, arg.ulIdentifyID));

                boolean alarmCallBackBindStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_AlarmInfoStatesCallBack(arg.ulIdentifyID, pfGetAlarmInfoCallBack,
                        deviceIpPointer);
                log.debug("告警上报回调函数绑定:" + alarmCallBackBindStatus);


                PU_REAL_PLAY_INFO realPlayInfo = new PU_REAL_PLAY_INFO();
                realPlayInfo.ulChannelId = new NativeLong(101);
                realPlayInfo.hPlayWnd = Pointer.NULL;
                realPlayInfo.enStreamType = HWPuSDKLibrary.PU_STREAM_TYPE.PU_VIDEO_MAIN_STREAM;
                realPlayInfo.enVideoType = HWPuSDKLibrary.PU_VIDEO_TYPE.PU_VIDEO_TYPE_META;
                realPlayInfo.enProtocolType = HWPuSDKLibrary.PU_PROTOCOL_TYPE.PU_PROTOCOL_TYPE_TCP;
                realPlayInfo.enMediaCallbackType = HWPuSDKLibrary.PU_MEDIA_CALLBACK_TYPE.PU_MEDIA_CALLBACK_TYPE_META_FRAME;
                byte[] localIpBytes = StrUtil.bytes(sdkLocalhost);
                System.arraycopy(localIpBytes, 0, realPlayInfo.szLocalIp, 0, localIpBytes.length);
                realPlayInfo.bKeepLive = true;
                realPlayInfo.szReserved[22] = 1;
                NativeLong realDataCallBackBindStatus = HWPuSDKLibrary.INSTANCE.IVS_PU_RealPlay(arg.ulIdentifyID, realPlayInfo,
                        pfRealDataCallBack, deviceIpPointer);
                log.debug("实况播放回调函数绑定:" + realDataCallBackBindStatus.longValue());
                HuaWeiSdkApi.printReturnMsg();

                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_NOT_REGISTER:
                log.debug("设备主动连接后未注册");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_UNREGISTER:
                log.debug("设备主动注销");
                HWPuSDKLibrary.INSTANCE.IVS_PU_StopAllRealPlay(arg.ulIdentifyID);
                HWPuSDKLibrary.INSTANCE.IVS_PU_Logout(arg.ulIdentifyID);
                removeDeviceMapByUlIdentifyId(arg.ulIdentifyID);
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_DISCONNECT:
                log.debug("设备网络连接断开");
                HWPuSDKLibrary.INSTANCE.IVS_PU_StopAllRealPlay(arg.ulIdentifyID);
                HWPuSDKLibrary.INSTANCE.IVS_PU_Logout(arg.ulIdentifyID);
                removeDeviceMapByUlIdentifyId(arg.ulIdentifyID);
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_SEND_RECV_ERROR:
                log.debug("发送或接收失败");
                HWPuSDKLibrary.INSTANCE.IVS_PU_StopAllRealPlay(arg.ulIdentifyID);
                HWPuSDKLibrary.INSTANCE.IVS_PU_Logout(arg.ulIdentifyID);
                removeDeviceMapByUlIdentifyId(arg.ulIdentifyID);
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_KEEPLIVE_FAIL:
                log.debug("设备保活失败");
                HWPuSDKLibrary.INSTANCE.IVS_PU_StopAllRealPlay(arg.ulIdentifyID);
                HWPuSDKLibrary.INSTANCE.IVS_PU_Logout(arg.ulIdentifyID);
                removeDeviceMapByUlIdentifyId(arg.ulIdentifyID);
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_STREAM_PACKAGE_CHANGE:
                log.debug("流套餐变更");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_WATERMARK_ERR:
                log.debug("数字水印校验错误");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_UPLOAD_IMAGE_URL:
                log.debug("设备主动获取抓拍图片上传URL请求");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_UPLOAD_IMAGE_COMP_NOTIFY:
                log.debug("设备抓拍图片上载完成通知");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_TRANSPARENT_CHANNEL_NOTIFY:
                log.debug("透明通道数据上报");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_REALPALY_ERROR:
                log.debug("实况异常");
                HuaWeiSdkApi.printReturnMsg();
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_REPORT_VISUAL_INFO:
                log.debug("上报可视化信息");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_PUPU_INFO:
                log.debug("多机协同数据上报");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_RECORD_COMP_NOTIFY:
                log.debug("录像下载完成通知");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_SLAVE_DEVICE_ADD:
                log.debug("新增从设备事件");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_SLAVE_DEVICE_MODIFY:
                log.debug("修改从设备事件");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_SLAVE_DEVICE_DELETE:
                log.debug("删除从设备事件");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_SLAVE_DEVICE_ONLINE:
                log.debug("从设备上线事件");
                break;
            case HWPuSDKLibrary.PU_EVENT_TYPE.PU_EVENT_TYPE_SLAVE_DEVICE_OFFLINE:
                log.debug("从设备下线事件");
                break;
            default:
                break;
        }
    }

    private void removeDeviceMapByUlIdentifyId(NativeLong deviceUlIdentifyId) {
        Iterator<Map.Entry<String, DeviceInfo>> iter = deviceMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, DeviceInfo> entry = iter.next();
            NativeLong ulIdentifyId = entry.getValue().getUlIdentifyId();
            if (ulIdentifyId.equals(deviceUlIdentifyId)) {
                iter.remove();
            }
        }
    }
}
