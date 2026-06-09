-- 用户表增加手机号字段（学号存 username，手机号单独存储，登录时二选一）
USE dormitory_db;

ALTER TABLE t_user
    ADD COLUMN phone VARCHAR(20) DEFAULT NULL COMMENT '手机号' AFTER username;

ALTER TABLE t_user
    ADD UNIQUE INDEX uk_user_phone (phone);
