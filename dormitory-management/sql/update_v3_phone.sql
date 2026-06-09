-- 用户表增加手机号字段（学号存 username，手机号单独存储，登录时二选一）
-- 若已执行过新版 init.sql（已含 phone 列），本脚本中 ADD COLUMN 会报错，可忽略
USE dormitory_db;

-- MySQL 5.7：若 phone 列已存在会报错，可忽略
ALTER TABLE t_user
    ADD COLUMN phone VARCHAR(20) DEFAULT NULL COMMENT '手机号' AFTER username;

-- 若唯一索引已存在会报错，可忽略
ALTER TABLE t_user
    ADD UNIQUE INDEX uk_user_phone (phone);
