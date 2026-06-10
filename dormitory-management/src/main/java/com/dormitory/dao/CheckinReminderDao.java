package com.dormitory.dao;

import com.dormitory.entity.CheckinReminder;
import com.dormitory.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckinReminderDao {

    private static volatile boolean tableReady;

    private void ensureTable() {
        if (tableReady) {
            return;
        }
        synchronized (CheckinReminderDao.class) {
            if (tableReady) {
                return;
            }
            Connection conn = null;
            Statement st = null;
            try {
                conn = DBUtil.getConnection();
                st = conn.createStatement();
                st.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS t_checkin_reminder ("
                                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                                + "user_id INT NOT NULL, "
                                + "remind_date DATE NOT NULL, "
                                + "message VARCHAR(500) NOT NULL, "
                                + "admin_name VARCHAR(50) DEFAULT NULL, "
                                + "is_read TINYINT NOT NULL DEFAULT 0, "
                                + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, "
                                + "UNIQUE KEY uk_user_remind_date (user_id, remind_date)"
                                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
                tableReady = true;
            } catch (SQLException e) {
                throw new RuntimeException("初始化签到提醒表失败：" + e.getMessage(), e);
            } finally {
                if (st != null) {
                    try {
                        st.close();
                    } catch (SQLException ignored) {
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        }
    }

    public boolean upsert(int userId, String remindDate, String message, String adminName) {
        ensureTable();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO t_checkin_reminder (user_id, remind_date, message, admin_name, is_read) "
                    + "VALUES (?, ?, ?, ?, 0) "
                    + "ON DUPLICATE KEY UPDATE message = VALUES(message), admin_name = VALUES(admin_name), "
                    + "is_read = 0, create_time = CURRENT_TIMESTAMP";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, remindDate);
            ps.setString(3, message);
            ps.setString(4, adminName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("发送签到提醒失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public List<CheckinReminder> findUnreadByUserId(int userId) {
        ensureTable();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CheckinReminder> list = new ArrayList<CheckinReminder>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, user_id, remind_date, message, admin_name, is_read, create_time "
                    + "FROM t_checkin_reminder WHERE user_id = ? AND is_read = 0 "
                    + "ORDER BY remind_date DESC, create_time DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询签到提醒失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 指定日期已发送过提醒的学生 ID（含已读/未读） */
    public Set<Integer> findRemindedUserIdsByDate(String remindDate) {
        ensureTable();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Set<Integer> ids = new HashSet<Integer>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(
                    "SELECT user_id FROM t_checkin_reminder WHERE remind_date = ?");
            ps.setString(1, remindDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询提醒记录失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return ids;
    }

    public boolean markRead(int reminderId, int userId) {
        ensureTable();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(
                    "UPDATE t_checkin_reminder SET is_read = 1 WHERE id = ? AND user_id = ?");
            ps.setInt(1, reminderId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("更新提醒状态失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public void markAllUnreadByUserId(int userId) {
        ensureTable();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(
                    "UPDATE t_checkin_reminder SET is_read = 1 WHERE user_id = ? AND is_read = 0");
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新提醒状态失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public void markReadByUserAndDate(int userId, String remindDate) {
        ensureTable();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(
                    "UPDATE t_checkin_reminder SET is_read = 1 WHERE user_id = ? AND remind_date = ?");
            ps.setInt(1, userId);
            ps.setString(2, remindDate);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新提醒状态失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    private CheckinReminder mapRow(ResultSet rs) throws SQLException {
        CheckinReminder reminder = new CheckinReminder();
        reminder.setId(rs.getInt("id"));
        reminder.setUserId(rs.getInt("user_id"));
        reminder.setRemindDate(rs.getDate("remind_date"));
        reminder.setMessage(rs.getString("message"));
        reminder.setAdminName(rs.getString("admin_name"));
        reminder.setRead(rs.getInt("is_read") == 1);
        reminder.setCreateTime(rs.getTimestamp("create_time"));
        return reminder;
    }
}
