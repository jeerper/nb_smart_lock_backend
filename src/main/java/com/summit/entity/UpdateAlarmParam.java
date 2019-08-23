package com.summit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="锁实时信息类", description="封装所需要的实时信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateAlarmParam {

    @ApiModelProperty(value="告警id，和accCtrlProId不能同时为空",name="alarmId")
    private String alarmId;
    @ApiModelProperty(value="告警状态，不传默认为0，表示更新告警状态为已处理",name="alarmStatus")
    private Integer alarmStatus = 0;
    @ApiModelProperty(value="对应门禁操作记录id，和alarmId不能同时为空",name="accCtrlProId")
    private String accCtrlProId;
    @ApiModelProperty(value="处理人",name="processPerson",hidden = true)
    private Integer processPerson;
    @ApiModelProperty(value="告警处理说明",name="processRemark")
    private String processRemark;
    @ApiModelProperty(value="锁id",name="lockId")
    private String lockId;
    @ApiModelProperty(value="是否需要开锁，默认false",name="needUnLock")
    private boolean needUnLock = false;
}
