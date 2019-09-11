package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

/**
 * Created by Administrator on 2019/9/11.
 */
@Data
@AllArgsConstructor
@ApiModel(value = "省份信息类",description = "封装省份信息类")
@TableName(value = "provinces")
public class Province {

    @ApiModelProperty(value = "省份id",name ="id")
    @TableId(value = "id",type= IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "省份编号",name = "provinceid")
    @TableField(value = "provinceid")
    private String provinceid;

    @ApiModelProperty(value = "省份",name = "province")
    @TableField(value = "province")
    private String province;
}

