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
public class   LockRequest {

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
     * 当前操作记录id
     */
    @ApiModelProperty(value="当前操作记录id",name="accCtrlProId")
    private String accCtrlProId;
    /**
     * 开锁指令下发uuid
     */
    @ApiModelProperty(value="开锁指令下发uuid",name="uuid")
    private String uuid;


    public LockRequest(){}

}
