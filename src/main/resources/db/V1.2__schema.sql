/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019/7/31 19:21:38                           */
/*==============================================================*/


drop table if exists alarm;

drop table if exists awakenlock_records;

drop table if exists camera_device;

drop table if exists face_info;

drop table if exists file_info;

drop table if exists gis_alarm;

drop table if exists gis_info;

drop table if exists lock_info;

drop table if exists lock_process;

drop table if exists safe_report;

drop table if exists safe_report_data;

drop table if exists role_lock_auth;

drop table if exists vedio_monitor_info;

/*==============================================================*/
/* Table: alarm                                                 */
/*==============================================================*/
create table alarm
(
   alarm_id             varchar(48) not null comment '告警id',
   alarm_name           varchar(32) comment '告警名称',
   process_id           varchar(32) not null comment '操作id，对应process_locd表中的process_id',
   alarm_time           timestamp comment '告警时间',
   alarm_status         int default 1 comment '告警状态。0已处理，1未处理',
   primary key (alarm_id),
   unique key AK_Key_2 (process_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table alarm comment '告警表';

/*==============================================================*/
/* Table: awakenlock_records                                    */
/*==============================================================*/
create table awakenlock_records
(
   awak_id              varchar(48) not null comment '唤醒操作记录id',
   lock_code            varchar(32) not null comment '锁编号',
   user_id              varchar(48) comment '操作人id',
   user_name            varchar(32) not null comment '操作人name',
   awakenlock_result    varchar(16) comment '唤醒结果。error：失败   success：成功',
   fail_reason          varchar(120) comment '失败原因',
   awak_time            timestamp comment '唤醒操作时间',
   primary key (awak_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table awakenlock_records comment '锁唤醒记录表';

/*==============================================================*/
/* Table: camera_device                                         */
/*==============================================================*/
create table camera_device
(
   dev_id               varchar(48) not null comment '摄像头id',
   device_ip            varchar(32) not null comment '摄像头ip地址',
   lock_code            varchar(48) comment '对应锁编号',
   status               int default 1 comment '摄像头状态。0正常，1异常',
   primary key (dev_id),
   unique key AK_Key_2 (device_ip)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table camera_device comment '摄像头表';

/*==============================================================*/
/* Table: face_info                                             */
/*==============================================================*/
create table face_info
(
   face_id              varchar(48) not null comment '人脸信息id',
   user_name            varchar(32) not null comment '锁操作人',
   user_id              varchar(48) comment '锁操作人id',
   gender               int comment '性别。0男，1女，2未知',
   birthday             timestamp comment '生日',
   province             varchar(16) comment '省份',
   city                 varchar(16) comment '城市',
   card_type            int comment '证件类型',
   card_id              varchar(20) comment '证件号',
   face_match_rate      float comment '人脸匹配率',
   face_lib_name        varchar(32) comment '人脸库名称',
   face_lib_type        int comment '人脸库类型',
   face_panorama_id     varchar(48) comment '人脸识别全景图id',
   face_pic_id          varchar(48) comment '人脸识别抠图id',
   face_match_id        varchar(48) comment '人脸识别和人脸库中匹配的图片id',
   pic_snapshot_time    timestamp comment '抓拍时间',
   device_ip            varchar(32) comment '摄像头设备ip',
   primary key (face_id),
   unique key AK_Key_3 (user_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table face_info comment '人脸信息表';

/*==============================================================*/
/* Table: file_info                                             */
/*==============================================================*/
create table file_info
(
   file_id              varchar(48) not null comment '文件id',
   file_name            varchar(48) comment '文件名',
   file_path            text not null comment '文件路径',
   description          text comment '文件描述',
   primary key (file_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table file_info comment '文件表';

/*==============================================================*/
/* Table: gis_alarm                                             */
/*==============================================================*/
create table gis_alarm
(
   gis_alarm_id         varchar(48) not null comment 'gis报警id',
   primary key (gis_alarm_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table gis_alarm comment 'GIS报警表';

/*==============================================================*/
/* Table: gis_info                                              */
/*==============================================================*/
create table gis_info
(
   gis_id               varchar(48) not null comment 'gis信息id',
   primary key (gis_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table gis_info comment 'GIS信息表';

/*==============================================================*/
/* Table: lock_info                                             */
/*==============================================================*/
create table lock_info
(
   lock_id              varchar(48) not null comment '锁id',
   lock_code            varchar(32) not null comment '锁编号',
   status               int comment '锁状态。1开锁，2锁定',
   createby             varchar(48) comment '创建者',
   updatetime           timestamp comment '上次更新状态时间',
   primary key (lock_id),
   unique key AK_Key_2 (lock_code)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table lock_info comment '锁信息表';

/*==============================================================*/
/* Table: lock_process                                          */
/*==============================================================*/
create table lock_process
(
   process_id           varchar(48) not null comment '锁操作记录id',
   device_ip            varchar(32) not null comment '摄像头设备ip',
   lock_code            varchar(32) not null comment '锁编号',
   user_id              varchar(48) comment '开锁人id',
   user_name            varchar(32) not null comment '开锁人name',
   process_type         int not null comment '锁操作类型，1：开锁，2：关锁',
   process_result       varchar(16) comment '开锁结果。error：失败   success：成功',
   fail_reason          varchar(120) comment '操作失败原因',
   face_panorama_id     varchar(48) comment '人脸识别全景图id',
   face_pic_id          varchar(48) comment '人脸识别抠图id',
   face_match_id        varchar(48) comment '人脸识别和人脸库中匹配的图片id',
   process_time         timestamp comment '锁操作时间',
   primary key (process_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table lock_process comment '开关锁操作记录表';

/*==============================================================*/
/* Table: safe_report                                           */
/*==============================================================*/
create table safe_report
(
   safe_rep_id          varchar(48) not null comment '平安报记录id',
   lock_code            varchar(32) not null comment '锁编号',
   type                 varchar(16) comment '返回类型。error：失败   success：成功',
   content              longtext comment '返回说明。失败显示失败原因',
   report_time          timestamp comment '上报时间',
   primary key (safe_rep_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table safe_report comment '工况(平安报)信息记录表';

/*==============================================================*/
/* Table: safe_report_data                                      */
/*==============================================================*/
create table safe_report_data
(
   id                   varchar(48) not null comment 'id',
   safe_rep_id          varchar(48) comment '对应平安报数据id',
   version              varchar(16) comment '版本',
   vol                  decimal comment '电压',
   jzdw                 varchar(32) comment '基站定位数据',
   latitude             decimal comment '纬度',
   longitude            decimal comment '经度',
   altitude             decimal comment '高程',
   speed                decimal comment '速度',
   direction            int comment '方向',
   gps_time             timestamp comment 'GPS时间',
   status_desc          varchar(32) comment '状态信息描述',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table safe_report_data comment '平安报数据表';

/*==============================================================*/
/* Table: role_lock_auth                                        */
/*==============================================================*/
create table role_lock_auth
(
   id                   varchar(48) not null comment '角色开锁权限id',
   role_id              varchar(48) not null comment '角色id',
   lock_id              varchar(48) not null comment '锁id',
   lock_code            varchar(48) not null comment '锁编号',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table role_lock_auth comment '角色与锁权限表';

/*==============================================================*/
/* Table: vedio_monitor_info                                    */
/*==============================================================*/
create table vedio_monitor_info
(
   id                   varchar(48) not null comment '视频监控信息id',
   device_ip            varchar(48) not null comment '视频来源摄像头ip',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table vedio_monitor_info comment '视频监控信息表';

