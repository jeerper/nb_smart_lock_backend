package com.summit.entity;

import lombok.Data;

/**
 * 封装查询平安报信息的post请求参数
 */
@Data
public class ReportParam {
    /**
     * 锁终端编号
     */
    private String terminalNum;

    /**
     * 查询开始时间，查询时间段不允许超过24小时
     */
    private String startTime;

    /**
     * 查询截止时间，查询时间段不允许超过24小时
     */
    private String endTime;

}
