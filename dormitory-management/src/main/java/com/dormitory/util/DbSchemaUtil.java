package com.dormitory.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库表结构探测（如 t_user.phone 列是否已迁移）
 */
public final class DbSchemaUtil {

    private static volatile Boolean userPhoneColumnAvailable;

    private DbSchemaUtil() {
    }

    public static boolean isUserPhoneColumnAvailable() {
        if (userPhoneColumnAvailable != null) {
            return userPhoneColumnAvailable;
        }
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            return isUserPhoneColumnAvailable(conn);
        } catch (SQLException e) {
            // 连接失败时不缓存结果，避免误判为「无 phone 列」
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public static boolean isUserPhoneColumnAvailable(Connection conn) throws SQLException {
        if (userPhoneColumnAvailable != null) {
            return userPhoneColumnAvailable;
        }
        userPhoneColumnAvailable = probePhoneColumn(conn);
        return userPhoneColumnAvailable;
    }

    private static boolean probePhoneColumn(Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT phone FROM t_user LIMIT 1");
            rs = ps.executeQuery();
            return true;
        } catch (SQLException e) {
            if (isMissingPhoneColumn(e)) {
                return false;
            }
            throw e;
        } finally {
            DBUtil.close(null, ps, rs);
        }
    }

    private static boolean isMissingPhoneColumn(SQLException e) {
        String msg = e.getMessage();
        if (msg == null) {
            return false;
        }
        String lower = msg.toLowerCase();
        return lower.contains("unknown column 'phone'")
                || lower.contains("unknown column `phone`")
                || msg.contains("Unknown column 'phone'")
                || msg.contains("不存在") && msg.contains("phone");
    }
}
