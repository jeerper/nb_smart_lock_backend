package com.summit.entity;

import lombok.Data;

/**
 * 封装平安报完整信息，包含结果、时间以及多个平安报数据
 */
@Data
public class SafeReportInfo{

    /**
     * 操作id
     */
    private String rmid;

    /**
     * 操作id
     */
    private String type;

    /**
     * 操作id
     */
    private String content;

    /**
     * 时间段内多个平安报数据
     */
    private Bojx[] objx;

    /**
     * 操作时间
     */
    private String time;

}
