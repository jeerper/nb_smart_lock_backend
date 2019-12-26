package com.summit.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 开锁结果接口对象
 */
@ApiModel(value="开锁结果", description="封装所需要的开锁结果信息")
@Data
public class UnlockResultInfo {
    @ApiModelProperty(value="锁编码")
    private String lockCode;
    @ApiModelProperty(value="是否成功开锁",notes = "true代表成功,false代表失败")
    private boolean success;
    @ApiModelProperty(value="开锁信息")
    private String message;
}
