package com.summit.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CommonUtil {

    /**
     * 开始时间标志
     */
    public static final String STARTTIMEMARK = "s";

    /**
     * 结束时间标志
     */
    public static final String ENDTIMEMARK = "e";


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * string类型时间转换为Date
     * @param time 待转换string类型时间
     * @param mark 开始或结束时间标志，用于打印日志
     * @return Date类型的时间
     */
    public static Date parseStrToDate(String time, String mark){

        if(time != null){
            try {
                return dateFormat.parse(time);
            } catch (ParseException e) {
                String msg = "";
                if(STARTTIMEMARK.equalsIgnoreCase(mark)){
                    msg = "开始";
                }else if(ENDTIMEMARK.equalsIgnoreCase(mark)){
                    msg = "结束";
                }
                log.warn(msg+ "时间不合法");
            }
        }
        return null;
    }
}
