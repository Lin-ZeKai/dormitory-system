-- 请假表增加审批意见字段
USE dormitory_db;

ALTER TABLE t_leave ADD COLUMN reject_reason VARCHAR(500) DEFAULT NULL COMMENT '审批意见/拒绝原因';
ALTER TABLE t_leave ADD COLUMN audit_time DATETIME DEFAULT NULL COMMENT '审批时间';
