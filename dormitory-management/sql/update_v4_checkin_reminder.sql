-- 签到提醒表：管理员提醒未签到学生
USE dormitory_db;

CREATE TABLE IF NOT EXISTS t_checkin_reminder (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT NOT NULL COMMENT '被提醒学生',
    remind_date DATE NOT NULL COMMENT '提醒对应的考勤日期',
    message     VARCHAR(500) NOT NULL COMMENT '提醒内容',
    admin_name  VARCHAR(50) DEFAULT NULL COMMENT '发送提醒的管理员',
    is_read     TINYINT NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_remind_date (user_id, remind_date),
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
