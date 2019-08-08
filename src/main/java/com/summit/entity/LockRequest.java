package com.summit.entity;

import lombok.Data;

/**
 * 封装开锁和查询锁状态post请求参数
 */
@Data
public class LockRequest {

    /**
     * 锁终端标号
     */
    private String terminalNum;

    /**
     * 操作人
     */
    private String operName;

    public LockRequest(){}

    public LockRequest(String terminalNum, String operName) {
        this.terminalNum = terminalNum;
        this.operName = operName;
    }
}
