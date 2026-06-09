package com.dormitory.dao.admin;

import com.dormitory.entity.User;
import com.dormitory.util.DBUtil;
import com.dormitory.util.DbSchemaUtil;

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

    private static final String SELECT_COLUMNS_NO_PHONE =
            "id, username, password, real_name, dorm_no, role, create_time";

    private static final String SELECT_COLUMNS_WITH_PHONE =
            "id, username, phone, password, real_name, dorm_no, role, create_time";

    public List<User> findAllStudents() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<User>();
        try {
            conn = DBUtil.getConnection();
            boolean withPhone = DbSchemaUtil.isUserPhoneColumnAvailable(conn);
            String sql = "SELECT " + selectColumns(withPhone)
                    + " FROM t_user WHERE role = 'student' ORDER BY create_time DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs, withPhone));
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
        try {
            conn = DBUtil.getConnection();
            boolean withPhone = DbSchemaUtil.isUserPhoneColumnAvailable(conn);
            String sql = "SELECT " + selectColumns(withPhone)
                    + " FROM t_user WHERE id = ? AND role = 'student'";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs, withPhone);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询学生失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    public boolean existsUsername(String username, Integer excludeId) {
        return existsLoginIdentifier(username, excludeId);
    }

    public boolean existsPhone(String phone, Integer excludeId) {
        return existsLoginIdentifier(phone, excludeId);
    }

    /** 学号/手机号在 username、phone 两列中全局唯一（编辑时可排除当前用户） */
    private boolean existsLoginIdentifier(String value, Integer excludeId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            boolean withPhone = DbSchemaUtil.isUserPhoneColumnAvailable(conn);
            String sql;
            if (withPhone) {
                sql = excludeId == null
                        ? "SELECT id FROM t_user WHERE username = ? OR phone = ? LIMIT 1"
                        : "SELECT id FROM t_user WHERE (username = ? OR phone = ?) AND id <> ? LIMIT 1";
            } else {
                sql = excludeId == null
                        ? "SELECT id FROM t_user WHERE username = ? LIMIT 1"
                        : "SELECT id FROM t_user WHERE username = ? AND id <> ? LIMIT 1";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, value);
            if (withPhone) {
                ps.setString(2, value);
                if (excludeId != null) {
                    ps.setInt(3, excludeId);
                }
            } else if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("校验学号/手机号失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    public boolean insertStudent(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            boolean withPhone = DbSchemaUtil.isUserPhoneColumnAvailable(conn);
            String sql;
            if (withPhone) {
                sql = "INSERT INTO t_user (username, phone, password, real_name, dorm_no, role) "
                        + "VALUES (?, ?, ?, ?, ?, 'student')";
            } else {
                sql = "INSERT INTO t_user (username, password, real_name, dorm_no, role) "
                        + "VALUES (?, ?, ?, ?, 'student')";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            if (withPhone) {
                ps.setString(2, user.getPhone());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getRealName());
                ps.setString(5, user.getDormNo());
            } else {
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getRealName());
                ps.setString(4, user.getDormNo());
            }
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
        try {
            conn = DBUtil.getConnection();
            boolean withPhone = DbSchemaUtil.isUserPhoneColumnAvailable(conn);
            boolean hasPassword = user.getPassword() != null && !user.getPassword().trim().isEmpty();
            StringBuilder sql = new StringBuilder("UPDATE t_user SET username = ?");
            if (withPhone) {
                sql.append(", phone = ?");
            }
            sql.append(", real_name = ?, dorm_no = ?");
            if (hasPassword) {
                sql.append(", password = ?");
            }
            sql.append(" WHERE id = ? AND role = 'student'");

            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            ps.setString(idx++, user.getUsername());
            if (withPhone) {
                ps.setString(idx++, user.getPhone());
            }
            ps.setString(idx++, user.getRealName());
            ps.setString(idx++, user.getDormNo());
            if (hasPassword) {
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
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("DELETE FROM t_attendance WHERE user_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement("DELETE FROM t_leave WHERE user_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement("DELETE FROM t_user WHERE id = ? AND role = 'student'");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw new RuntimeException("删除学生失败", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
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

    private String selectColumns(boolean withPhone) {
        return withPhone ? SELECT_COLUMNS_WITH_PHONE : SELECT_COLUMNS_NO_PHONE;
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
