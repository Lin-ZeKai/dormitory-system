package com.dormitory.dao;

import com.dormitory.entity.Attendance;
import com.dormitory.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceDao {

    public Attendance findTodayByUserId(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT id, user_id, check_date, check_time, status FROM t_attendance "
                + "WHERE user_id = ? AND check_date = CURDATE()";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询今日签到失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    public boolean checkIn(int userId, java.util.Date checkTime, String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO t_attendance (user_id, check_date, check_time, status) VALUES (?, CURDATE(), ?, ?)";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setTimestamp(2, new java.sql.Timestamp(checkTime.getTime()));
            ps.setString(3, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("签到失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public int countByUserAndMonth(int userId, String yearMonth) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) FROM t_attendance WHERE user_id = ? AND DATE_FORMAT(check_date, '%Y-%m') = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, yearMonth);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计签到失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    public int countLateByUserAndMonth(int userId, String yearMonth) {
        return countByUserStatusAndMonth(userId, yearMonth, "late");
    }

    private int countByUserStatusAndMonth(int userId, String yearMonth, String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) FROM t_attendance WHERE user_id = ? AND status = ? "
                + "AND DATE_FORMAT(check_date, '%Y-%m') = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, status);
            ps.setString(3, yearMonth);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计考勤失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    public List<Attendance> findByUserAndMonth(int userId, String yearMonth, String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Attendance> list = new ArrayList<Attendance>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, user_id, check_date, check_time, status FROM t_attendance "
                        + "WHERE user_id = ? AND DATE_FORMAT(check_date, '%Y-%m') = ?");
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
        }
        sql.append(" ORDER BY check_date DESC, check_time DESC");
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setInt(1, userId);
            ps.setString(2, yearMonth);
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(3, status);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询考勤记录失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    public int countTodayCheckedIn() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM t_attendance WHERE check_date = CURDATE()");
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计今日签到失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    public int countTodayAbsentStudents(int totalStudents) {
        return Math.max(totalStudents - countTodayCheckedIn(), 0);
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setUserId(rs.getInt("user_id"));
        a.setCheckDate(rs.getDate("check_date"));
        a.setCheckTime(rs.getTimestamp("check_time"));
        a.setStatus(rs.getString("status"));
        return a;
    }

    public static String currentYearMonth() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        return y + "-" + (m < 10 ? "0" + m : String.valueOf(m));
    }
}
