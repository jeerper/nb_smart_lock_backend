package com.summit.entity;

import lombok.Data;

@Data
public class SafeReportInfo{
    private String rmid;
    private String type;
    private String content;
    private Bojx[] objx;
    private String time;

}
