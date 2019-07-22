package com.summit.entity;

import lombok.Data;

@Data
public class LockRequest {
    private String terminalNum;
    private String operName;
    public LockRequest(){}

    public LockRequest(String terminalNum, String operName) {
        this.terminalNum = terminalNum;
        this.operName = operName;
    }
}
