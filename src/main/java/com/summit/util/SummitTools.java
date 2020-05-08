package com.summit.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by Administrator on 2019/8/26.
 */
@Component
public class SummitTools {
    /**
     * 当字符串不为null且长度大于0时返回true
     * @param s
     * @return
     */
    public static boolean stringNotNull(String s){
        if(s !=null && !s.isEmpty() && s.trim().length()>0){
            return true;
        }
        return false;
    }
    /**
     * 获得唯一Id(通用)
     *
     * @return
     */
    public static String getKey() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
