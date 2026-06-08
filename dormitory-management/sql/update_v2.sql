-- 已有数据库升级脚本（若已执行过旧版 init.sql，请执行本脚本）
USE dormitory_db;

-- 若 dorm_no 列已存在会报错，可忽略
ALTER TABLE t_user ADD COLUMN dorm_no VARCHAR(50) DEFAULT NULL COMMENT '宿舍号';

CREATE TABLE IF NOT EXISTS t_attendance (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    user_id    INT NOT NULL,
    check_date DATE NOT NULL,
    check_time DATETIME NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'normal',
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_leave (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT NOT NULL,
    leave_type  VARCHAR(20) NOT NULL,
    phone       VARCHAR(20) NOT NULL,
    start_time  DATETIME NOT NULL,
    end_time    DATETIME NOT NULL,
    reason      VARCHAR(500) NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'pending',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_announcement (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(200) NOT NULL,
    content      TEXT NOT NULL,
    publisher    VARCHAR(50) DEFAULT NULL,
    is_important TINYINT NOT NULL DEFAULT 0,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO t_user (username, password, real_name, dorm_no, role)
VALUES ('2021001', '123456', '张三', '3号楼302', 'student')
ON DUPLICATE KEY UPDATE username = username;
