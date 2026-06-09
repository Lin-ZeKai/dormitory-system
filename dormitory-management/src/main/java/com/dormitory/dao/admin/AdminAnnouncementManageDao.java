package com.dormitory.dao.admin;

import com.dormitory.entity.Announcement;
import com.dormitory.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 管理员端公告编辑 DAO（新增，补充原有 AnnouncementDao 无 update 的能力）
 */
public class AdminAnnouncementManageDao {

    public Announcement findById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT id, title, content, publisher, is_important, create_time FROM t_announcement WHERE id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Announcement a = new Announcement();
                a.setId(rs.getInt("id"));
                a.setTitle(rs.getString("title"));
                a.setContent(rs.getString("content"));
                a.setPublisher(rs.getString("publisher"));
                a.setIsImportant(rs.getInt("is_important"));
                a.setCreateTime(rs.getTimestamp("create_time"));
                return a;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询公告失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    public boolean update(Announcement announcement) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "UPDATE t_announcement SET title = ?, content = ?, is_important = ? WHERE id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, announcement.getTitle());
            ps.setString(2, announcement.getContent());
            ps.setInt(3, announcement.getIsImportant() == null ? 0 : announcement.getIsImportant());
            ps.setInt(4, announcement.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("修改公告失败", e);
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }
}
