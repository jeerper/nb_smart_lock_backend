USE cbb_nb_smart_lock;

alter table lock_info add column current_password varchar(20) CHARACTER SET utf8 COLLATE utf8_bin COMMENT '当前密码';
alter table lock_info add column new_password varchar(20) CHARACTER SET utf8 COLLATE utf8_bin COMMENT '新密码';