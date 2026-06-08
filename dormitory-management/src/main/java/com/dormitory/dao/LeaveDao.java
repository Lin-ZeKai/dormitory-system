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

    private static final String BASE_COLUMNS = "id, user_id, leave_type, phone, start_time, end_time, reason, "
            + "status, reject_reason, audit_time, create_time";

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

    /** 更新待审批记录（避免重复提交产生多条待审记录） */
    public boolean updatePendingLeave(int leaveId, Leave leave) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "UPDATE t_leave SET leave_type = ?, phone = ?, start_time = ?, end_time = ?, reason = ?, "
                + "status = 'pending', reject_reason = NULL, audit_time = NULL WHERE id = ? AND status = 'pending'";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, leave.getLeaveType());
            ps.setString(2, leave.getPhone());
            ps.setTimestamp(3, new java.sql.Timestamp(leave.getStartTime().getTime()));
            ps.setTimestamp(4, new java.sql.Timestamp(leave.getEndTime().getTime()));
            ps.setString(5, leave.getReason());
            ps.setInt(6, leaveId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("更新请假申请失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    /** 被拒绝后重新提交：在原记录上修改并改回待审批 */
    public boolean resubmitLeave(int leaveId, int userId, Leave leave) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "UPDATE t_leave SET leave_type = ?, phone = ?, start_time = ?, end_time = ?, reason = ?, "
                + "status = 'pending', reject_reason = NULL, audit_time = NULL "
                + "WHERE id = ? AND user_id = ? AND status = 'rejected'";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, leave.getLeaveType());
            ps.setString(2, leave.getPhone());
            ps.setTimestamp(3, new java.sql.Timestamp(leave.getStartTime().getTime()));
            ps.setTimestamp(4, new java.sql.Timestamp(leave.getEndTime().getTime()));
            ps.setString(5, leave.getReason());
            ps.setInt(6, leaveId);
            ps.setInt(7, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("重新提交请假失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public Leave findPendingByUserId(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT " + BASE_COLUMNS + " FROM t_leave WHERE user_id = ? AND status = 'pending' "
                + "ORDER BY create_time DESC LIMIT 1";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询待审请假失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /** 查询该用户最近一条已拒绝记录（用于重新提交时更新原记录） */
    public Leave findLatestRejectedByUserId(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT " + BASE_COLUMNS + " FROM t_leave WHERE user_id = ? AND status = 'rejected' "
                + "ORDER BY audit_time DESC, create_time DESC LIMIT 1";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询已拒绝请假失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    public List<Leave> findByUserId(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Leave> list = new ArrayList<Leave>();
        String sql = "SELECT " + BASE_COLUMNS + " FROM t_leave WHERE user_id = ? ORDER BY create_time DESC";
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
                + "l.status, l.reject_reason, l.audit_time, l.create_time, u.real_name "
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

    public boolean approveLeave(int leaveId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("UPDATE t_leave SET status = 'approved', reject_reason = '审批通过', "
                    + "audit_time = NOW() WHERE id = ? AND status = 'pending'");
            ps.setInt(1, leaveId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("审批通过失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public boolean rejectLeave(int leaveId, String rejectReason) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("UPDATE t_leave SET status = 'rejected', reject_reason = ?, "
                    + "audit_time = NOW() WHERE id = ? AND status = 'pending'");
            ps.setString(1, rejectReason);
            ps.setInt(2, leaveId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("审批拒绝失败", e);
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
        leave.setRejectReason(rs.getString("reject_reason"));
        leave.setAuditTime(rs.getTimestamp("audit_time"));
        leave.setCreateTime(rs.getTimestamp("create_time"));
        return leave;
    }
}
