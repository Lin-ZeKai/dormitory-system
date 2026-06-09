package com.dormitory.dao;

import com.dormitory.entity.User;
import com.dormitory.util.DBUtil;
import com.dormitory.util.DbSchemaUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private static final String BASE_COLUMNS_NO_PHONE =
            "id, username, password, real_name, dorm_no, role, create_time";

    private static final String BASE_COLUMNS_WITH_PHONE =
            "id, username, phone, password, real_name, dorm_no, role, create_time";

    /** 使用学号或手机号 + 密码登录 */
    public User findByLoginAndPassword(String login, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            boolean withPhone = DbSchemaUtil.isUserPhoneColumnAvailable(conn);
            String sql;
            if (withPhone) {
                sql = "SELECT " + BASE_COLUMNS_WITH_PHONE
                        + " FROM t_user WHERE (username = ? OR phone = ?) AND password = ?";
            } else {
                sql = "SELECT " + BASE_COLUMNS_NO_PHONE
                        + " FROM t_user WHERE username = ? AND password = ?";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, login);
            if (withPhone) {
                ps.setString(2, login);
                ps.setString(3, password);
            } else {
                ps.setString(2, password);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs, withPhone);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /** 学号是否已被占用（username 或 phone 列任一匹配即视为重复） */
    public boolean existsByUsername(String username) {
        return existsByLoginIdentifier(username);
    }

    /** 手机号是否已被占用（username 或 phone 列任一匹配即视为重复） */
    public boolean existsByPhone(String phone) {
        return existsByLoginIdentifier(phone);
    }

    private boolean existsByLoginIdentifier(String value) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            boolean withPhone = DbSchemaUtil.isUserPhoneColumnAvailable(conn);
            String sql = withPhone
                    ? "SELECT id FROM t_user WHERE username = ? OR phone = ? LIMIT 1"
                    : "SELECT id FROM t_user WHERE username = ? LIMIT 1";
            ps = conn.prepareStatement(sql);
            ps.setString(1, value);
            if (withPhone) {
                ps.setString(2, value);
            }
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("查询用户失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    public boolean insert(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            boolean withPhone = DbSchemaUtil.isUserPhoneColumnAvailable(conn);
            String sql;
            if (withPhone) {
                sql = "INSERT INTO t_user (username, phone, password, real_name, dorm_no, role) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO t_user (username, password, real_name, dorm_no, role) "
                        + "VALUES (?, ?, ?, ?, ?)";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            if (withPhone) {
                ps.setString(2, user.getPhone());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getRealName());
                ps.setString(5, user.getDormNo());
                ps.setString(6, user.getRole());
            } else {
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getRealName());
                ps.setString(4, user.getDormNo());
                ps.setString(5, user.getRole());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("注册用户失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public int countByRole(String role) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM t_user WHERE role = ?");
            ps.setString(1, role);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计用户失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    private User mapRow(ResultSet rs, boolean withPhone) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        if (withPhone) {
            user.setPhone(rs.getString("phone"));
        }
        user.setPassword(rs.getString("password"));
        user.setRealName(rs.getString("real_name"));
        user.setDormNo(rs.getString("dorm_no"));
        user.setRole(rs.getString("role"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        return user;
    }
}
