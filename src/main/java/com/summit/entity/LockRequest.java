package com.summit.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 封装开锁和查询锁状态post请求参数
 */
@Data
@AllArgsConstructor
@ApiModel(value="开锁和查询锁状态接口请求信息类", description="封装所需要的请求信息")
public class LockRequest {

    /**
     * 锁终端id
     */
    @ApiModelProperty(value="锁id",name="lockId")
    private String lockId;

    /**
     * 锁终端标号
     */
    @ApiModelProperty(value="锁编号",name="terminalNum")
    private String terminalNum;

    /**
     * 操作人
     */
    @ApiModelProperty(value="操作人",name="operName")
    private String operName;

    /**
     * 是否开锁
     */
    @ApiModelProperty(value="是否开锁，默认开锁。查询状态接口无需传此参数",name="isUnLock")
    private boolean isUnLock = true;

    /**
     * 告警处理说明
     */
    @ApiModelProperty(value="告警处理说明。查询状态接口无需传此参数",name="processRemark")
    private String processRemark;

    /**
     * 告警id
     */
    @ApiModelProperty(value="告警id",name="alarmId")
    private String alarmId;

    public LockRequest(){}

}
