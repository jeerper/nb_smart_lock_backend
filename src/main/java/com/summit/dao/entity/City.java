package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Administrator on 2019/9/11.
 */
@Data
@AllArgsConstructor
@ApiModel(value = "城市信息类",description = "封装城市信息类")
@TableName(value = "cities")
public class City {

    @ApiModelProperty(value = "城市id",name = "id")
    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "城市编号",name = "cityid")
    @TableField(value = "cityid")
    private String cityid;

    @ApiModelProperty(value = "城市名称",name = "cityName")
    @TableField(value = "city")
    private String cityName;

    @ApiModelProperty(value = "省份编号",name = "provinceid")
    @TableField(value = "provinceid")
    private String provinceid;
}
