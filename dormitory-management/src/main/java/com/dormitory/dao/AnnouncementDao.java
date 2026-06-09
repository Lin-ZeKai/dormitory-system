package com.dormitory.dao;

import com.dormitory.entity.Announcement;
import com.dormitory.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDao {

    public List<Announcement> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Announcement> list = new ArrayList<Announcement>();
        String sql = "SELECT id, title, content, publisher, is_important, create_time "
                + "FROM t_announcement ORDER BY create_time DESC";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询公告失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    public List<Announcement> findLatest(int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Announcement> list = new ArrayList<Announcement>();
        String sql = "SELECT id, title, content, publisher, is_important, create_time "
                + "FROM t_announcement ORDER BY create_time DESC LIMIT ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询公告失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    public boolean insert(Announcement announcement) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO t_announcement (title, content, publisher, is_important) VALUES (?, ?, ?, ?)";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, announcement.getTitle());
            ps.setString(2, announcement.getContent());
            ps.setString(3, announcement.getPublisher());
            ps.setInt(4, announcement.getIsImportant() == null ? 0 : announcement.getIsImportant());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("发布公告失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    public boolean deleteById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("DELETE FROM t_announcement WHERE id = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("删除公告失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    private Announcement mapRow(ResultSet rs) throws SQLException {
        Announcement a = new Announcement();
        a.setId(rs.getInt("id"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setPublisher(rs.getString("publisher"));
        a.setIsImportant(rs.getInt("is_important"));
        a.setCreateTime(rs.getTimestamp("create_time"));
        return a;
    }
}
