package com.summit.dao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value="简单门禁信息类", description="包含门禁名称、id、当前角色门禁授权状态")
public class SimpleAccCtrlInfo {
    @ApiModelProperty(value="门禁id",name="accessControlId")
    private String accessControlId;
    @ApiModelProperty(value="门禁名称",name="accessControlName")
    private String accessControlName;
//    @ApiModelProperty(value="当前角色门禁授权状态，true为已授权，false为未授权",name="authStatus")
//    private String authStatus;

}
