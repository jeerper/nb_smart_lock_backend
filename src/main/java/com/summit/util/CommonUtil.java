package com.summit.util;

import com.summit.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class CommonUtil {

    /**
     * string类型时间转换为Date
     * @param time 待转换string类型时间
     * @param mark 开始或结束时间标志，用于打印日志
     * @return Date类型的时间
     */
    public static Date parseStrToDate(String time, String mark){

        if(time != null){
            try {
                return CommonConstants.timeFormat.parse(time);
            } catch (ParseException e) {
                String msg = "";
                if(CommonConstants.STARTTIMEMARK.equalsIgnoreCase(mark)){
                    msg = "开始";
                }else if(CommonConstants.ENDTIMEMARK.equalsIgnoreCase(mark)){
                    msg = "结束";
                }
                log.warn(msg+ "时间不合法");
            }
        }
        return null;
    }

    public static List<String> removeDuplicate(List<String> list) {
        HashSet<String> h = new HashSet<>(list);
        list.clear();
        list.addAll(h);
        return list;
    }
}
