package com.summit.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Page {
    private Integer current;
    private Integer pageSize;
    public Page(){}
}
