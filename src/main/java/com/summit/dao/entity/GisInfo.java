package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName(value = "gis_info")
public class GisInfo {
    @TableId(value = "gis_id", type = IdType.ID_WORKER_STR)
    private String gisId;

    public GisInfo(){}
}