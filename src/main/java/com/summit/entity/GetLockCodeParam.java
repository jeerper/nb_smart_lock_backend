package com.summit.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="获取锁密码", description="封装获取锁密码信息")
public class GetLockCodeParam {
    @ApiModelProperty(value="锁编号",name="lockCode",required = true)
    private String lockCode;
    @ApiModelProperty(value="进出方式",name="enterOrExit",required = true)
    private Integer enterOrExit;

}
