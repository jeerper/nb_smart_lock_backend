USE cbb_userauth;

INSERT IGNORE INTO `sys_dictionary` VALUES ('dept_type', 'root', '部门类别', 'dept_type', '部门类别');
INSERT IGNORE INTO `sys_dictionary` VALUES ('dept_type_1', 'dept_type', '内部机构', '0', '内部机构');
INSERT IGNORE INTO `sys_dictionary` VALUES ('dept_type_2', 'dept_type', '外部机构', '1', '外部机构');
INSERT IGNORE INTO `sys_dictionary` VALUES ('isAudited', 'root', '审核结果', 'isAudited', '审核结果');
INSERT IGNORE INTO `sys_dictionary` VALUES ('isAudited_0', 'isAudited', '待处理', '0', '待处理');
INSERT IGNORE INTO `sys_dictionary` VALUES ('isAudited_1', 'isAudited', '已批准', '1', '已批准');
INSERT IGNORE INTO `sys_dictionary` VALUES ('isAudited_2', 'isAudited', '已拒绝', '2', '已拒绝');