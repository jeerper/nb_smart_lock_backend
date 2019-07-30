package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class GisInfo {
    @TableId(value = "gis_id", type = IdType.ID_WORKER_STR)
    private String gisId;

}