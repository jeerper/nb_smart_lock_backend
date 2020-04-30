USE cbb_nb_smart_lock;

alter table acc_ctrl_process add column face_name varchar(50) CHARACTER SET utf8 COLLATE utf8_bin COMMENT '人脸姓名';