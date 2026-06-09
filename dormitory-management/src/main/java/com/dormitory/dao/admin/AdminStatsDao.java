package com.dormitory.dao.admin;

import com.dormitory.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员统计中心 DAO（新增）
 */
public class AdminStatsDao {

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

    public int countTotalStudents() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM t_user WHERE role = 'student'");
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计学生失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    /** 今日已通过且处于请假时段内的学生数 */
    public int countTodayOnLeave() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(DISTINCT user_id) FROM t_leave "
                    + "WHERE status = 'approved' AND start_time <= NOW() AND end_time >= NOW()");
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计今日请假失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    public int countPendingLeave() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM t_leave WHERE status = 'pending'");
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计待审请假失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    public int countTodayAbsent(int totalStudents) {
        return Math.max(totalStudents - countTodayCheckedIn(), 0);
    }

    /** 近 7 日每日签到人数（含今天，按日期顺序） */
    public Map<String, Integer> last7DaysCheckinTrend() {
        Map<String, Integer> dbMap = new java.util.HashMap<String, Integer>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT DATE_FORMAT(check_date, '%m-%d') AS day_label, COUNT(*) AS cnt "
                + "FROM t_attendance WHERE check_date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) "
                + "GROUP BY check_date ORDER BY check_date";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                dbMap.put(rs.getString("day_label"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询签到趋势失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -6);
        for (int i = 0; i < 7; i++) {
            int m = cal.get(java.util.Calendar.MONTH) + 1;
            int d = cal.get(java.util.Calendar.DAY_OF_MONTH);
            String key = (m < 10 ? "0" + m : String.valueOf(m)) + "-"
                    + (d < 10 ? "0" + d : String.valueOf(d));
            result.put(key, dbMap.containsKey(key) ? dbMap.get(key) : 0);
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }

    public List<Integer> leaveStatusDistribution() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(countByLeaveStatus("pending"));
        list.add(countByLeaveStatus("approved"));
        list.add(countByLeaveStatus("rejected"));
        return list;
    }

    private int countByLeaveStatus(String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM t_leave WHERE status = ?");
            ps.setString(1, status);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计请假状态失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }
}
