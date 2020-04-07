USE cbb_nb_smart_lock;

alter table acc_crtl_realtime add column alarm_status int(11) NULL DEFAULT NULL COMMENT '告警状态(0：低电压告警，1：非法开锁告警，2低电量告警，3：掉线告警，4：故障告警，5：关锁超时报警)';
alter table acc_crtl_realtime add column work_status int(11) NULL DEFAULT NULL  COMMENT '工作状态(0：实时在线，1：忙时在线，2:闲时休眠)';
alter table acc_crtl_realtime add column enterOrExit int(11) NULL DEFAULT NULL  COMMENT '进出方式(0:进,1:出)';
alter table acc_ctrl_process add column unlockCause varchar(255) CHARACTER SET utf8 COLLATE utf8_bin COMMENT '开锁事由';
alter table acc_ctrl_process add column enterOrExit int(11) NULL DEFAULT NULL  COMMENT '进出方式(0:进,1:出)';
alter table acc_ctrl_process add column alarm_status int(11) NULL DEFAULT NULL  COMMENT '告警状态(0：低电压告警，2低电量告警，1：非法开锁告警，3：掉线告警，4：故障告警，5：关锁超时报警)';
alter table acc_ctrl_process add column work_status int(11) NULL DEFAULT NULL  COMMENT '工作状态(0：实时在线，1：忙时在线，2:闲时休眠)';
alter table access_control add column alarm_status int(11) NULL DEFAULT NULL  COMMENT '告警状态(0：低电压告警，2低电量告警，1：非法开锁告警，3：掉线告警，4：故障告警，5：关锁超时报警)';
alter table access_control add column work_status int(11) NULL DEFAULT NULL  COMMENT '工作状态(0：实时在线，1：忙时在线，2:闲时休眠)';
alter table addup_acc_ctrl_process add column alarm_count int(11) NULL DEFAULT NULL  COMMENT '报警次数';
alter table addup_acc_ctrl_process add column enterOrExit_count int(11) NULL DEFAULT NULL  COMMENT '进出频次(包括进出)';
alter table alarm add column enterOrExit int(11) NULL DEFAULT NULL  COMMENT '进出方式(0:进,1:出)';
alter table alarm add column alarm_deal_status int(11) NULL DEFAULT NULL  COMMENT '报警处理状态(0已处理，1未处理)';
alter table face_info_access_control add column auth_status int(11) NULL DEFAULT NULL  COMMENT '人脸录入状态(0：已录入，1：未录入)';
alter table lock_info add column alarm_status int(11) NULL DEFAULT NULL  COMMENT '告警状态(0：低电压告警，1低电量告警，2：非法开锁告警，3：掉线告警，4：故障告警，5：关锁超时报警)';
alter table lock_info add column work_status int(11) NULL DEFAULT NULL  COMMENT '工作状态(0：实时在线，1：忙时在线，2:闲时休眠)';