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
    @ApiModelProperty(value="状态码")
    private int statusCode;
    @ApiModelProperty(value="电池电量")
    private int batteryLevel;
    @ApiModelProperty(value="开锁次数")
    private int unlockNum;
    @ApiModelProperty(value="操作结果")
    private int result;
    @ApiModelProperty(value="是否成功开锁",notes = "true代表成功,false代表失败")
    private boolean success;
    @ApiModelProperty(value="当前密码",name="currentPassword")
    private String currentPassword;
    @ApiModelProperty(value="新密码",name="newPassword")
    private String newPassword;
}
