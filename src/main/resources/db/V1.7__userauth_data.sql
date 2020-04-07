USE cbb_userauth;

INSERT IGNORE INTO `sys_function` VALUES ('0e612e1d314745a0ab45e3b747152148', 'root', '门禁人脸授权', 10, 1, 'Business/auth', 'eye', NULL, 0);
INSERT IGNORE INTO `sys_function` VALUES ('67f55dd26565476bb962a52a4586a0b8', 'e5aa55ee586546b7ac5a707d5b82a244', '信息审批', 6, 1, 'System/auth', NULL, NULL, 0);
INSERT IGNORE INTO `sys_function` VALUES ('9ee3cdb8c944477d8b2b5cbb6eb6793b', 'e5aa55ee586546b7ac5a707d5b82a244', '角色管理', 2, 1, 'System/role', NULL, NULL, 0);
INSERT IGNORE INTO `sys_function` VALUES ('ac42d1b4417a477b8bb5024e3625c76e', 'e5aa55ee586546b7ac5a707d5b82a244', '日志管理', 5, 1, 'System/log', NULL, NULL, 0);
INSERT IGNORE INTO `sys_function` VALUES ('b80cbe81122a48a685d3b4024f2fe310', 'e5aa55ee586546b7ac5a707d5b82a244', '机构管理', 4, 1, 'System/dept', NULL, NULL, 0);
INSERT IGNORE INTO `sys_function` VALUES ('e5aa55ee586546b7ac5a707d5b82a244', 'root', '系统管理', 20, 1, '', 'team', '系统设置', 0);
INSERT IGNORE INTO `sys_function` VALUES ('eff36a373e84463397186861633b3d4b', 'e5aa55ee586546b7ac5a707d5b82a244', '用户管理', 1, 1, 'System/user', NULL, '用户设置', 0);
INSERT IGNORE INTO `sys_function` VALUES ('f1070bcdd9d84c43945c0a304700a24f', 'e5aa55ee586546b7ac5a707d5b82a244', '功能管理', 3, 1, 'System/function', NULL, NULL, 0);
INSERT IGNORE INTO `sys_function` VALUES ('root', NULL, '功能树', 1, 1, NULL, NULL, '根节点请勿删除或移动', 0);