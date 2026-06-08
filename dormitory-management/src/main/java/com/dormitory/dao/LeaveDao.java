package com.dormitory.dao;

import com.dormitory.entity.Leave;
import com.dormitory.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaveDao {

    public boolean insert(Leave leave) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO t_leave (user_id, leave_type, phone, start_time, end_time, reason, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'pending')";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, leave.getUserId());
            ps.setString(2, leave.getLeaveType());
            ps.setString(3, leave.getPhone());
            ps.setTimestamp(4, new java.sql.Timestamp(leave.getStartTime().getTime()));
            ps.setTimestamp(5, new java.sql.Timestamp(leave.getEndTime().getTime()));
            ps.setString(6, leave.getReason());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("提交请假失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public List<Leave> findByUserId(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Leave> list = new ArrayList<Leave>();
        String sql = "SELECT id, user_id, leave_type, phone, start_time, end_time, reason, status, create_time "
                + "FROM t_leave WHERE user_id = ? ORDER BY create_time DESC";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询请假记录失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    public int countPending() {
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

    public int countPendingByUser(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM t_leave WHERE user_id = ? AND status = 'pending'");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计请假失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    public List<Leave> findPendingAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Leave> list = new ArrayList<Leave>();
        String sql = "SELECT l.id, l.user_id, l.leave_type, l.phone, l.start_time, l.end_time, l.reason, "
                + "l.status, l.create_time, u.real_name "
                + "FROM t_leave l LEFT JOIN t_user u ON l.user_id = u.id "
                + "WHERE l.status = 'pending' ORDER BY l.create_time DESC";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Leave leave = mapRow(rs);
                leave.setRealName(rs.getString("real_name"));
                list.add(leave);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询待审请假失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    public boolean updateStatus(int leaveId, String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("UPDATE t_leave SET status = ? WHERE id = ?");
            ps.setString(1, status);
            ps.setInt(2, leaveId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("更新请假状态失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    private Leave mapRow(ResultSet rs) throws SQLException {
        Leave leave = new Leave();
        leave.setId(rs.getInt("id"));
        leave.setUserId(rs.getInt("user_id"));
        leave.setLeaveType(rs.getString("leave_type"));
        leave.setPhone(rs.getString("phone"));
        leave.setStartTime(rs.getTimestamp("start_time"));
        leave.setEndTime(rs.getTimestamp("end_time"));
        leave.setReason(rs.getString("reason"));
        leave.setStatus(rs.getString("status"));
        leave.setCreateTime(rs.getTimestamp("create_time"));
        return leave;
    }
}
