USE cbb_userauth;

DELETE FROM sys_dept WHERE ID = '1';
INSERT INTO `sys_dept` (`ID`, `PID`, `DEPTCODE`, `DEPTNAME`, `ADCD`, `REMARK`,`deptType`) VALUES ('1', '-1', '0000', '管理处', NULL, '根节点请勿删除或移动','0');