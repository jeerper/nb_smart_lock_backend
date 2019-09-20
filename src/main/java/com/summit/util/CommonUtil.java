package com.summit.util;

import com.summit.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class CommonUtil {

    public static ThreadLocal<DateFormat> timeFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat(CommonConstants.timeFormat));
    public static ThreadLocal<DateFormat> dateFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat(CommonConstants.dateFormat));
    public static ThreadLocal<DateFormat> snapshotTimeFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat(CommonConstants.snapshotTimeFormat));
    public static ThreadLocal<DateFormat> frontTimeFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat(CommonConstants.frontTimeFormat));

    /**
     * string类型时间转换为Date
     * @param time 待转换string类型时间
     * @param mark 开始或结束时间标志，用于打印日志
     * @return Date类型的时间
     */
    public static Date strToDate(String time, String mark){

        if(time != null && !"".equals(time)){
            try {
                return timeFormat.get().parse(time);
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
     * string类型时间转换为Date
     * @param time 待转换string类型时间
     * @param pattern 格式
     * @return Date类型日期
     * @throws ParseException 日期转换异常
     */
    public static Date parseStr2Date(String time,String pattern) throws ParseException {
        if (CommonUtil.isEmptyStr(time) || CommonUtil.isEmptyStr(pattern))
            return null;
        return new SimpleDateFormat(pattern).parse(time);
    }

    /**
     * Date类型时间格式化为string
     * @param time 待转换string类型时间
     * @param pattern 格式
     * @return String类型日期
     */
    public static String formmatDate2Str(Date time,String pattern) {
        if (time == null || CommonUtil.isEmptyStr(pattern))
            return null;
        return new SimpleDateFormat(pattern).format(time);
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

    /**
     *
     * @param beginTime 开始时间
     * @param endTime 截至时间
     * @return true 或 false
     * @throws ParseException
     */
    public static boolean isInTime(Date beginTime,Date endTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date date=new Date();
        String nowtime = df.format(date);
        long nowtime1 = df.parse(nowtime).getTime();
        long biegtime = beginTime.getTime();
        long endtimeTime = endTime.getTime();
        if (nowtime1>=biegtime && nowtime1<=endtimeTime){
            return true;
        }
        return false;
    }

    /**
     *  读取文件的流
     * @param path
     * @return 文件内容
     */
    public static String readFile(String path){
        BufferedReader reader=null;
        String lastStr="";
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader=new BufferedReader(inputStreamReader);
            String tempString=null;
            while ((tempString = reader.readLine()) != null){
                lastStr+=tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastStr;
    }

}
