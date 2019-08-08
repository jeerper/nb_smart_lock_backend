package com.summit.entity;

import lombok.Data;

/**
 * 平安报数据
 */
@Data
public class Bojx {

    /**
     * 版本
     */
    private String version;

    /**
     * 电压
     */
    private String vol;

    /**
     * 基站定位数据
     */
    private String jzdw;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 高程
     */
    private String altitude;

    /**
     * 速度
     */
    private String speed;

    /**
     * 方向
     */
    private String direction;

    /**
     * GPS时间
     */
    private String gps_time;

    /**
     * 状态信息描述
     */
    private String status_desc;


}
