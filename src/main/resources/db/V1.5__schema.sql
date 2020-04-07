USE cbb_nb_smart_lock;

CREATE TABLE IF NOT EXISTS `dept_accesscontrol_auth`(
  `id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '主键',
  `dept_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL '部门id',
  `access_control_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL '门禁id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '部门门禁授权表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `dept_face_auth`(
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '主键',
  `dept_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL '部门id',
  `face_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL '人脸id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '部门人脸授权表' ROW_FORMAT = Dynamic;