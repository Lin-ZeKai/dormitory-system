package com.dormitory.dao.admin;

import com.dormitory.entity.Attendance;
import com.dormitory.entity.User;
import com.dormitory.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员端考勤查询 DAO（新增）
 */
public class AdminAttendanceQueryDao {

    public List<Attendance> findAll(String checkDate, Integer userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Attendance> list = new ArrayList<Attendance>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.id, a.user_id, a.check_date, a.check_time, a.status, u.real_name "
                        + "FROM t_attendance a LEFT JOIN t_user u ON a.user_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (checkDate != null && !checkDate.trim().isEmpty()) {
            sql.append(" AND a.check_date = ?");
            params.add(checkDate.trim());
        }
        if (userId != null) {
            sql.append(" AND a.user_id = ?");
            params.add(userId);
        }
        sql.append(" ORDER BY a.check_date DESC, a.check_time DESC LIMIT 500");
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setString(i + 1, String.valueOf(p));
                }
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Attendance a = new Attendance();
                a.setId(rs.getInt("id"));
                a.setUserId(rs.getInt("user_id"));
                a.setCheckDate(rs.getDate("check_date"));
                a.setCheckTime(rs.getTimestamp("check_time"));
                a.setStatus(rs.getString("status"));
                a.setRealName(rs.getString("real_name"));
                list.add(a);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询签到记录失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    public List<User> findStudentsForFilter() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<User>();
        String sql = "SELECT id, username, real_name FROM t_user WHERE role = 'student' ORDER BY username";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setRealName(rs.getString("real_name"));
                list.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询学生筛选列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }
}
