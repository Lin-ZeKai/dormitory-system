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
            userPhoneColumnAvailable = Boolean.FALSE;
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
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT phone FROM t_user LIMIT 1");
            rs = ps.executeQuery();
            userPhoneColumnAvailable = Boolean.TRUE;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("Unknown column 'phone'")) {
                userPhoneColumnAvailable = Boolean.FALSE;
            } else {
                throw e;
            }
        } finally {
            DBUtil.close(null, ps, rs);
        }
        return userPhoneColumnAvailable;
    }
}
