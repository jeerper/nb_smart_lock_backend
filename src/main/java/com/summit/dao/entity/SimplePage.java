package com.summit.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimplePage {
    private Integer current;
    private Integer pageSize;
    public SimplePage(){}
}
