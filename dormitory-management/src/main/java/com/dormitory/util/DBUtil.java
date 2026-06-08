package com.dormitory.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库连接工具类
 * <p>
 * 从 classpath 下的 db.properties 读取 JDBC 配置，
 * 提供统一的连接获取与资源关闭方法，供 DAO 层调用。
 * </p>
 */
public final class DBUtil {

    /** MySQL 驱动类名 */
    private static final String DRIVER;
    /** 数据库连接地址 */
    private static final String URL;
    /** 数据库登录用户名 */
    private static final String USERNAME;
    /** 数据库登录密码 */
    private static final String PASSWORD;

    /**
     * 静态代码块：项目启动时加载配置并注册 JDBC 驱动，只执行一次
     */
    static {
        Properties props = new Properties();
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("未找到 db.properties 配置文件");
            }
            props.load(in);
            DRIVER = props.getProperty("jdbc.driver");
            URL = props.getProperty("jdbc.url");
            USERNAME = props.getProperty("jdbc.username");
            PASSWORD = props.getProperty("jdbc.password");
            // 加载 MySQL 驱动（JDK8 + MySQL5.7 使用 com.mysql.jdbc.Driver）
            Class.forName(DRIVER);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("数据库配置加载失败", e);
        }
    }

    /** 私有构造，禁止实例化工具类 */
    private DBUtil() {
    }

    /**
     * 获取数据库连接
     *
     * @return JDBC Connection 对象
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * 关闭 JDBC 资源，按 ResultSet → Statement → Connection 顺序释放
     *
     * @param conn 数据库连接，可为 null
     * @param stmt SQL 语句对象，可为 null
     * @param rs   结果集，可为 null
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ignored) {
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ignored) {
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
