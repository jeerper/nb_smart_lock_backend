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

        if(time != null && !"".equals(time)){
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

    /**
     * List集合去重
     * @param list 待去重List集合
     */
    public static void removeDuplicate(List<String> list) {
        HashSet<String> h = new HashSet<>(list);
        list.clear();
        list.addAll(h);
    }

    /**
     * List集合判空
     * @param list 待判空List集合
     * @return 是否为空
     */
    public static boolean isEmptyList(List list) {
        return list == null || list.isEmpty();
    }

    /**
     * 数组判空
     * @param arr 待判空数组
     * @return 数组是否为空
     */
    public static boolean isEmptyArr(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    /**
     * 字符串判空
     * @param str 待判空字符串
     * @return 字符串是否为空
     */
    public static boolean isEmptyStr(String str) {
        return str == null || str.trim().length() == 0;
    }
}
