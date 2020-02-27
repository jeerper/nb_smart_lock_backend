package com.summit.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     *
     * @param  dateStr   时间字符串
     * @param pattern 格式化之后的时间
     * @return
     */
    public static Date  stringToDate(String dateStr  ,String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date returnDate=null;
        try {
            //使用SimpleDateFormat的parse()方法生成Date
            returnDate = sdf.parse(dateStr);
            //打印Date
           // System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    /**
     * 格式化当前时间
     * @param pattern  时间格式化方式
     * @return
     */
    public static Date  getDatePattern(String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date returnDate=null;
        try {
            Date date = new Date();
            //使用SimpleDateFormat的parse()方法生成Date
            String startTime = sdf.format(date);
            returnDate = sdf.parse(startTime);
            //打印Date
            // System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }


}
