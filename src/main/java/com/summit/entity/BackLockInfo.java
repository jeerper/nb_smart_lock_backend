package com.summit.entity;


import lombok.Data;

import java.util.Date;

@Data
public class BackLockInfo {
    private String rmid;
    private String type;
    private String content;
    private String objx;
    private String time;

}
