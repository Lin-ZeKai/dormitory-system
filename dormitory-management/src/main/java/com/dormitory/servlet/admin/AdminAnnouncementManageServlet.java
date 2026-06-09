package com.dormitory.servlet.admin;

import com.dormitory.dao.AnnouncementDao;
import com.dormitory.dao.admin.AdminAnnouncementManageDao;
import com.dormitory.entity.Announcement;
import com.dormitory.entity.User;
import com.dormitory.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminAnnouncementManageServlet", urlPatterns = "/admin/announcements")
public class AdminAnnouncementManageServlet extends HttpServlet {

    private final AnnouncementDao announcementDao = new AnnouncementDao();
    private final AdminAnnouncementManageDao manageDao = new AdminAnnouncementManageDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AdminServletSupport.requireAdmin(req, resp)) return;
        req.setAttribute("announcements", announcementDao.findAll());
        req.getRequestDispatcher("/admin/announcements-manage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AdminServletSupport.requireAdmin(req, resp)) return;

        User user = WebUtil.getLoginUser(req);
        String action = req.getParameter("action");
        if ("create".equals(action)) {
            handleCreate(req, user);
        } else if ("update".equals(action)) {
            handleUpdate(req);
        } else if ("delete".equals(action)) {
            handleDelete(req);
        } else {
            AdminServletSupport.flash(req, "未知操作");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/announcements");
    }

    private void handleCreate(HttpServletRequest req, User user) {
        String title = trim(req.getParameter("title"));
        String content = trim(req.getParameter("content"));
        String important = req.getParameter("important");
        if (isEmpty(title) || isEmpty(content)) {
            AdminServletSupport.flash(req, "标题和内容不能为空");
            return;
        }
        Announcement a = new Announcement();
        a.setTitle(title);
        a.setContent(content);
        a.setPublisher(user.getRealName() != null ? user.getRealName() : user.getUsername());
        a.setIsImportant("on".equals(important) || "1".equals(important) ? 1 : 0);
        AdminServletSupport.flash(req, announcementDao.insert(a) ? "公告发布成功" : "发布失败");
    }

    private void handleUpdate(HttpServletRequest req) {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String title = trim(req.getParameter("title"));
            String content = trim(req.getParameter("content"));
            String important = req.getParameter("important");
            if (isEmpty(title) || isEmpty(content)) {
                AdminServletSupport.flash(req, "标题和内容不能为空");
                return;
            }
            Announcement a = new Announcement();
            a.setId(id);
            a.setTitle(title);
            a.setContent(content);
            a.setIsImportant("on".equals(important) || "1".equals(important) ? 1 : 0);
            AdminServletSupport.flash(req, manageDao.update(a) ? "公告修改成功" : "修改失败");
        } catch (NumberFormatException e) {
            AdminServletSupport.flash(req, "参数错误");
        }
    }

    private void handleDelete(HttpServletRequest req) {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            AdminServletSupport.flash(req, announcementDao.deleteById(id) ? "公告已删除" : "删除失败");
        } catch (NumberFormatException e) {
            AdminServletSupport.flash(req, "参数错误");
        }
    }

    private String trim(String v) {
        return v == null ? null : v.trim();
    }

    private boolean isEmpty(String v) {
        return v == null || v.isEmpty();
    }
}
