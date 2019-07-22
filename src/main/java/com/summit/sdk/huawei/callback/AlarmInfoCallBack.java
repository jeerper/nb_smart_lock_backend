package com.summit.sdk.huawei.callback;

import cn.hutool.core.util.StrUtil;
import com.summit.sdk.huawei.HWPuSDKLibrary;
import com.summit.sdk.huawei.PU_ALARM_REPORT;
import com.summit.sdk.huawei.PU_TIME;
import com.summit.sdk.huawei.model.AlarmType;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

@Slf4j
public class AlarmInfoCallBack implements HWPuSDKLibrary.pfGetAlarmInfoCallBack {
    @Override
    public void apply(PU_ALARM_REPORT pstAlarmReport, Pointer pUsrData) {

        log.debug("告警设备ID:" + pstAlarmReport.ulDeviceId.longValue());
        AlarmType alarmType = AlarmType.codeOf(pstAlarmReport.enAlarmType);
        if (alarmType == null) {
            log.debug("未知告警类型:" + pstAlarmReport.enAlarmType);
        } else {
            log.debug("告警类型:" + alarmType.getAlarmDescription());
        }
        PU_TIME timeBytes = pstAlarmReport.stTime;
        int year = Integer.valueOf(StrUtil.str(timeBytes.szYear, "").trim());
        int month = Integer.valueOf(StrUtil.str(timeBytes.szMonth, "").trim());
        int day = Integer.valueOf(StrUtil.str(timeBytes.szDay, "").trim());
        int hour = Integer.valueOf(StrUtil.str(timeBytes.szHour, "").trim());
        int minute = Integer.valueOf(StrUtil.str(timeBytes.szMinute, "").trim());
        int second = Integer.valueOf(StrUtil.str(timeBytes.szSecond, "").trim());
        String timeString = new DateTime(year, month, day, hour, minute, second).toString("yyyy-MM-dd HH:mm:ss");
        log.debug("告警时间:" + timeString);
        if (pstAlarmReport.enAction == 1) {
            log.debug("告警动作:移除");
        } else if (pstAlarmReport.enAction == 2) {
            log.debug("告警动作:发生");
        }
        log.debug("告警ID:" + pstAlarmReport.ulAlarmId.longValue());
        log.debug("设备IP:" + pUsrData.getString(0));
    }
}
