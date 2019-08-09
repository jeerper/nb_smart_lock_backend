package com.summit.entity;


import lombok.Data;

import java.util.Date;

/**
 * 封装开锁和查询锁状态返回结果
 */
@Data
public class BackLockInfo {

    /**
     * 结果id，可为null
     */
    private String rmid;

    /**
     * 返回类型
     */
    private String type;

    /**
     * 返回说明
     */
    private String content;

    /**
     * 锁状态
     */
    private String objx;

    /**
     * 操作时间
     */
    private String time;

}
