USE cbb_nb_smart_lock;

alter table alarm add column face_name varchar(50) CHARACTER SET utf8 COLLATE utf8_bin COMMENT '人脸姓名';
alter table alarm add column user_name varchar(50) CHARACTER SET utf8 COLLATE utf8_bin COMMENT '登录账号';