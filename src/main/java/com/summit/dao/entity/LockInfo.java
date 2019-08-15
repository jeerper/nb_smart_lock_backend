package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@TableName(value = "lock_info")
@ApiModel(value="锁信息类", description="封装锁信息")
public class LockInfo {
    @ApiModelProperty(value="锁id,录入时不传，传了则忽略，仍用系统自动生成id。更新时锁id和锁编号至少传入一个，两个都传则用id更新",name="lockId")
    @TableId(value = "lock_id", type = IdType.ID_WORKER_STR)
    private String lockId;
    @ApiModelProperty(value="锁编号",name="lockCode",required = true)
    @TableField(value = "lock_code")
    private String lockCode;

    //1开锁，2锁定，3告警
    @ApiModelProperty(value="锁状态,1开锁，2锁定，3告警",name="status",notes = "1开锁，2锁定，3告警",required = true)
    @TableField(value = "status")
    private Integer status;
    @ApiModelProperty(value="锁创建人,取当前用户名称",name="createby",notes = "这里填name",hidden = true)
    @TableField(value = "createby")
    private String createby;
    @ApiModelProperty(hidden = true)
    @TableField(value = "createtime")
    private Date createtime;
//    @ApiModelProperty(value="锁创建时间",name="createtimeStr",notes = "格式为yyyy-MM-dd HH:mm:ss")
//    @TableField(exist = false)
//    private String createtimeStr;

    @ApiModelProperty(hidden = true)
    @TableField(value = "updatetime")
    private Date updatetime;
//    @ApiModelProperty(value="锁更新时间",name="updatetimeStr",notes = "格式为yyyy-MM-dd HH:mm:ss")
//    @TableField(exist = false)
//    private String updatetimeStr;

    @ApiModelProperty(value="锁关联的摄像头列表,为null则json中不返回此字段",name="devices",hidden = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(exist = false)
    private List<CameraDevice> devices;

    @ApiModelProperty(value="锁关联的角色列表,此字段不返回前台",name="devices",hidden = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(exist = false)
    private List<LockRole> roles;

    public LockInfo(){}

}