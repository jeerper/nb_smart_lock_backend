USE cbb_nb_smart_lock;

CREATE TABLE IF NOT EXISTS  `acc_crtl_realtime`  (
  `acc_crtl_realtime_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '门禁实时信息id',
  `access_control_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '门禁id',
  `access_control_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '门禁名',
  `lock_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '锁id',
  `lock_code` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '锁编号',
  `acc_ctrl_status` int(11) NULL DEFAULT NULL COMMENT '门禁状态，1：开锁，2：关锁，3：告警，4:不在线',
  `lock_status` int(11) NULL DEFAULT NULL COMMENT '锁状态，1：开锁，2：关锁，3：告警，4:不在线',
  `dev_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '设备id',
  `device_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '设备ip',
  `device_type` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '摄像头类型,entry入口，exit出口',
  `face_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸id',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '用户名',
  `user_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '用户id',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '姓名',
  `gender` int(11) NULL DEFAULT NULL COMMENT '性别，0男，1女，2未知',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `province` varchar(12) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(12) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '城市',
  `card_type` int(11) NULL DEFAULT NULL COMMENT '证件类型。0：身份证，1：护照，2：军官证，3：驾驶证，4：其他',
  `card_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '证件号',
  `face_match_rate` float NULL DEFAULT NULL COMMENT '人脸匹配率',
  `face_lib_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸库名称',
  `face_lib_type` int(11) NULL DEFAULT NULL COMMENT '人脸库类型。0：未知，1：黑名单，2：白名单，3：报警',
  `face_panorama_url` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '人脸全景图url',
  `face_pic_url` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '人脸识别抠图url',
  `face_match_url` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '人脸识别和人脸库中匹配的图片url',
  `pic_snapshot_time` timestamp(0) NULL DEFAULT NULL COMMENT '快照时间',
  `longitude` varchar(24) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '经度',
  `latitude` varchar(24) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '纬度',
  `alarm_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '对应门禁最新告警id',
  `acc_ctrl_pro_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '对应门禁最新操作记录id',
  `updatetime` timestamp(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`acc_crtl_realtime_id`) USING BTREE,
  UNIQUE INDEX `AK_Key_2`(`lock_code`) USING BTREE,
  UNIQUE INDEX `AK_Key_3`(`access_control_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `acc_ctrl_process`  (
  `acc_ctrl_pro_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '门禁操作记录id',
  `access_control_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '门禁id',
  `access_control_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '门禁名',
  `device_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '摄像头id',
  `device_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '摄像头设备ip',
  `device_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '摄像头类型,entry入口，exit出口',
  `lock_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '锁id',
  `lock_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '锁编号',
  `user_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '操作人id',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '操作人name',
  `gender` int(11) NULL DEFAULT NULL COMMENT '性别。0男，1女，2未知',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `province` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '城市',
  `card_type` int(11) NULL DEFAULT NULL COMMENT '证件类型。0：身份证，1：护照，2：军官证，3：驾驶证，4：其他',
  `card_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '证件号',
  `face_match_rate` float NULL DEFAULT NULL COMMENT '匹配率',
  `face_lib_type` int(11) NULL DEFAULT NULL COMMENT '人脸库类型。0：未知，1：黑名单，2：白名单，3：报警',
  `face_lib_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸库名称',
  `process_type` int(11) NULL DEFAULT NULL COMMENT '门禁操作类型，1：开锁，2：关锁，3：告警',
  `process_uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '用于查询开锁状态的命令ID',
  `process_result` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '开锁结果：1：下发指令成功 2：异常 4：不在线 5：未回复 6：成功 \r\n7：失败 8：消息有误 9：不支持的消息',
  `fail_reason` varchar(120) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '操作失败原因',
  `panorama_pic_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '全景图片id',
  `face_panorama_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸识别全景图id',
  `face_pic_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸识别抠图id',
  `face_match_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸识别和人脸库中匹配的图片id',
  `process_time` timestamp(0) NULL DEFAULT NULL COMMENT '锁操作时间',
  `process_method` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '操作方式，1：刷脸操作，2：接口操作',
  `create_time` timestamp(0) NULL DEFAULT NULL COMMENT '操作创建时间',
  PRIMARY KEY (`acc_ctrl_pro_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '门禁操作记录表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `access_control`  (
  `access_control_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '门禁id',
  `access_control_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '门禁名称',
  `lock_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '锁id',
  `lock_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '锁编号',
  `entry_camera_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '入口摄像头id',
  `entry_camera_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '入口摄像头ip地址',
  `exit_camera_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '出口摄像头id',
  `exit_camera_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '出口摄像头ip地址',
  `status` int(11) NULL DEFAULT NULL COMMENT '门禁状态。1：打开，2：锁定，3：告警',
  `createby` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '创建人',
  `createtime` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updatetime` timestamp(0) NULL DEFAULT NULL COMMENT '更新时间',
  `longitude` varchar(24) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '经度',
  `latitude` varchar(24) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`access_control_id`) USING BTREE,
  UNIQUE INDEX `lock_code`(`lock_code`) USING BTREE,
  UNIQUE INDEX `lock_id`(`lock_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '门禁信息表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `addup_acc_ctrl_process`  (
  `id` varchar(48) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '统计分析开关锁的id',
  `access_control_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门禁id',
  `access_control_status_count` int(11) NULL DEFAULT NULL COMMENT '门禁状态。1：打开，2：锁定',
  `battery_leve` int(24) NULL DEFAULT NULL COMMENT '电池电量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `alarm`  (
  `alarm_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '告警id',
  `alarm_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '告警名称',
  `acc_ctrl_pro_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '门禁操作id，对应acc_ctrl_process表中的acc_ctrl_pro_id',
  `alarm_time` timestamp(0) NULL DEFAULT NULL COMMENT '告警时间',
  `alarm_status` int(11) NULL DEFAULT 1 COMMENT '告警状态。0已处理，1未处理',
  `process_person` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '处理人',
  `description` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '告警描述信息',
  `updatetime` timestamp(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  `process_remark` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '处理备注',
  PRIMARY KEY (`alarm_id`) USING BTREE,
  UNIQUE INDEX `AK_Key_2`(`acc_ctrl_pro_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '告警表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `camera_device`  (
  `dev_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '摄像头id',
  `device_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '摄像头ip地址',
  `lock_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '对应锁id',
  `lock_code` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '对应锁编号',
  `status` int(11) NULL DEFAULT 1 COMMENT '摄像头状态。0正常，1异常',
  `type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '摄像头类型,entry入口，exit出口',
  `createby` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '创建人',
  `createtime` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updatetime` timestamp(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dev_id`) USING BTREE,
  UNIQUE INDEX `AK_Key_2`(`device_ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '摄像头表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `cities`  (
  `id` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `cityid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `city` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `provinceid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `city`(`city`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '行政区域地州市信息表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `face_info`  (
  `face_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '人脸信息id',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸名称',
  `user_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '锁操作人id',
  `gender` int(11) NULL DEFAULT NULL COMMENT '性别。0男，1女，2未知',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `province` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '城市',
  `card_type` int(11) NULL DEFAULT NULL COMMENT '证件类型。证件类型。0：身份证，1：护照，2：军官证，3：驾驶证，4：未知',
  `card_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '证件号',
  `face_match_rate` float NULL DEFAULT NULL COMMENT '人脸匹配率',
  `face_lib_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸库名称',
  `face_lib_type` int(11) NULL DEFAULT NULL COMMENT '人脸库类型。0：未知，1：黑名单，2：白名单，3：报警',
  `face_panorama_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸识别全景图id',
  `face_pic_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸识别抠图id',
  `face_match_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸识别和人脸库中匹配的图片id',
  `device_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '摄像头设备ip',
  `pic_snapshot_time` timestamp(0) NULL DEFAULT NULL COMMENT '抓拍时间',
  `face_lib_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸库的id，表示这个人脸属于哪个人脸库',
  `face_image` varchar(60) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸库图片',
  `face_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸名称',
  `face_type` int(11) NULL DEFAULT NULL COMMENT '人脸类型。0表示内部人员，1表示临时人员',
  `face_startTime` timestamp(0) NULL DEFAULT NULL COMMENT '人脸录入的当前时间',
  `face_endTime` date NULL DEFAULT NULL COMMENT '人脸录入有效的截至时间',
  `is_valid_time` int(11) NULL DEFAULT NULL COMMENT '人脸有效期是否过期。0：没有过期，1：已经过期',
  PRIMARY KEY (`face_id`) USING BTREE,
  UNIQUE INDEX `AK_Key_3`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '人脸信息表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `face_info_access_control`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `access_control_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门禁信息id',
  `face_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '人脸信息id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `file_info`  (
  `file_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '文件id',
  `file_name` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '文件名',
  `file_path` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '文件路径',
  `description` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '文件描述',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '文件表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `lock_info`  (
  `lock_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁id',
  `lock_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁编号',
  `status` int(11) NULL DEFAULT NULL COMMENT '锁状态。1开锁，2锁定，3告警',
  `createby` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '创建者',
  `createtime` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updatetime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '上次更新状态时间',
  PRIMARY KEY (`lock_id`) USING BTREE,
  UNIQUE INDEX `AK_Key_2`(`lock_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '锁信息表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `lock_process`  (
  `process_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁操作记录id',
  `device_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '摄像头设备ip',
  `lock_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '锁id',
  `lock_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁编号',
  `user_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '开锁人id',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '开锁人name',
  `gender` int(11) NULL DEFAULT NULL COMMENT '性别。0男，1女，2未知',
  `birthday` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '生日',
  `province` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '城市',
  `card_type` int(11) NULL DEFAULT NULL COMMENT '证件类型。0：身份证，1：护照，2：军官证，3：驾驶证，4：其他',
  `card_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '证件号',
  `face_match_rate` float(255, 0) NULL DEFAULT NULL COMMENT '匹配率',
  `face_lib_type` int(11) NULL DEFAULT NULL COMMENT '人脸库类型。0：未知，1：黑名单，2：白名单，3：报警',
  `face_lib_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸库名称',
  `process_type` int(11) NULL DEFAULT NULL COMMENT '锁操作类型，1：开锁，2：关锁，3：告警',
  `process_result` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '开锁结果。error：失败   success：成功',
  `fail_reason` varchar(120) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '操作失败原因',
  `face_panorama_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸识别全景图id',
  `face_pic_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸识别抠图id',
  `face_match_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '人脸识别和人脸库中匹配的图片id',
  `process_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '锁操作时间',
  PRIMARY KEY (`process_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '开关锁操作记录表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `provinces`  (
  `id` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `provinceid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `province` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `province`(`province`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '省份信息表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `role_accesscontrol_auth`  (
  `id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '角色开锁权限id',
  `role_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '角色id',
  `access_control_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '门禁id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_id`(`role_id`, `access_control_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '角色与门禁权限表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `role_lock_auth`  (
  `id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '角色开锁权限id',
  `role_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '角色id',
  `lock_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁id',
  `lock_code` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '角色与锁权限表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `safe_report`  (
  `safe_rep_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '平安报记录id',
  `lock_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '锁编号',
  `type` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '返回类型。error：失败   success：成功',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '返回说明。失败显示失败原因',
  `report_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '上报时间',
  PRIMARY KEY (`safe_rep_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '工况(平安报)信息记录表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `safe_report_data`  (
  `id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'id',
  `safe_rep_id` varchar(48) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '对应平安报数据id',
  `version` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '版本',
  `vol` decimal(10, 0) NULL DEFAULT NULL COMMENT '电压',
  `jzdw` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '基站定位数据',
  `latitude` decimal(10, 0) NULL DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10, 0) NULL DEFAULT NULL COMMENT '经度',
  `altitude` decimal(10, 0) NULL DEFAULT NULL COMMENT '高程',
  `speed` decimal(10, 0) NULL DEFAULT NULL COMMENT '速度',
  `direction` int(11) NULL DEFAULT NULL COMMENT '方向',
  `gps_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'GPS时间',
  `status_desc` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '状态信息描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '平安报数据表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `unlock_command_queue`  (
  `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `acc_ctrl_pro_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `lock_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `unlock_face_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
