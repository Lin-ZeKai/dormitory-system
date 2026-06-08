-- ============================================================
-- 宿舍考勤与管理系统 - 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS dormitory_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE dormitory_db;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    real_name   VARCHAR(50)  DEFAULT NULL,
    dorm_no     VARCHAR(50)  DEFAULT NULL COMMENT '宿舍号',
    role        VARCHAR(20)  NOT NULL DEFAULT 'student' COMMENT 'student/admin',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 考勤签到表
CREATE TABLE IF NOT EXISTS t_attendance (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    user_id    INT NOT NULL,
    check_date DATE NOT NULL COMMENT '签到日期',
    check_time DATETIME NOT NULL COMMENT '签到时间',
    status     VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT 'normal/late',
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 请假申请表
CREATE TABLE IF NOT EXISTS t_leave (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    user_id       INT NOT NULL,
    leave_type    VARCHAR(20) NOT NULL COMMENT 'late/overnight/sick/public/event/other',
    phone         VARCHAR(20) NOT NULL,
    start_time    DATETIME NOT NULL,
    end_time      DATETIME NOT NULL,
    reason        VARCHAR(500) NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected',
    reject_reason VARCHAR(500) DEFAULT NULL COMMENT '审批意见/拒绝原因',
    audit_time    DATETIME DEFAULT NULL COMMENT '审批时间',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统公告表
CREATE TABLE IF NOT EXISTS t_announcement (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(200) NOT NULL,
    content      TEXT NOT NULL,
    publisher    VARCHAR(50) DEFAULT NULL,
    is_important TINYINT NOT NULL DEFAULT 0,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 测试数据
INSERT INTO t_user (username, password, real_name, dorm_no, role)
VALUES ('admin', '123456', '管理员', NULL, 'admin')
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO t_user (username, password, real_name, dorm_no, role)
VALUES ('2021001', '123456', '张三', '3号楼302', 'student')
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO t_announcement (title, content, publisher, is_important)
SELECT '宿舍考勤系统上线公告', '宿舍考勤与管理系统正式上线，请全体住宿同学每日按时完成系统签到。', '信息中心', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM t_announcement LIMIT 1);
