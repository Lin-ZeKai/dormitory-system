package com.dormitory.dao.admin;

import com.dormitory.entity.User;
import com.dormitory.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员端学生信息管理 DAO（新增，不修改原有 UserDao）
 */
public class AdminStudentDao {

    public List<User> findAllStudents() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<User>();
        String sql = "SELECT id, username, password, real_name, dorm_no, role, create_time "
                + "FROM t_user WHERE role = 'student' ORDER BY create_time DESC";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询学生列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    public User findStudentById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT id, username, password, real_name, dorm_no, role, create_time "
                + "FROM t_user WHERE id = ? AND role = 'student'";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询学生失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    public boolean existsUsername(String username, Integer excludeId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            if (excludeId == null) {
                ps = conn.prepareStatement("SELECT id FROM t_user WHERE username = ?");
                ps.setString(1, username);
            } else {
                ps = conn.prepareStatement("SELECT id FROM t_user WHERE username = ? AND id <> ?");
                ps.setString(1, username);
                ps.setInt(2, excludeId);
            }
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("校验用户名失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    public boolean insertStudent(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO t_user (username, password, real_name, dorm_no, role) VALUES (?, ?, ?, ?, 'student')";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRealName());
            ps.setString(4, user.getDormNo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("添加学生失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public boolean updateStudent(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "UPDATE t_user SET username = ?, real_name = ?, dorm_no = ?";
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            sql += ", password = ?";
        }
        sql += " WHERE id = ? AND role = 'student'";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getRealName());
            ps.setString(3, user.getDormNo());
            int idx = 4;
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                ps.setString(idx++, user.getPassword());
            }
            ps.setInt(idx, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("修改学生失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public boolean deleteStudent(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("DELETE FROM t_user WHERE id = ? AND role = 'student'");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("删除学生失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public int countStudents() {
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

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRealName(rs.getString("real_name"));
        user.setDormNo(rs.getString("dorm_no"));
        user.setRole(rs.getString("role"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        return user;
    }
}
