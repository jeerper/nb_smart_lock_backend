USE cbb_nb_smart_lock;

alter table addup_acc_ctrl_process alter column access_control_status_count set default 0;
alter table addup_acc_ctrl_process alter column battery_leve set default 0;
alter table addup_acc_ctrl_process alter column alarm_count set default 0;
alter table addup_acc_ctrl_process alter column enterOrExit_count set default 0;