package com.summit.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(value="指令下发队列领域对象", description="指令下发队列信息")
@TableName(value = "unlock_command_queue")
public class UnlockCommandQueue {
    @ApiModelProperty(value="指令ID")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;
    @ApiModelProperty(value="门禁操作记录id")
    @TableField(value = "acc_ctrl_pro_id")
    private String accCtrlProId;
    @ApiModelProperty(value="门禁锁编码")
    @TableField(value = "lock_code")
    private String lockCode;
    @ApiModelProperty(value="触发开锁用户名称")
    @TableField(value = "unlock_face_name")
    private String unlockFaceName;

    @ApiModelProperty(value="操作时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField(value = "create_time")
    private Date createTime;

}
