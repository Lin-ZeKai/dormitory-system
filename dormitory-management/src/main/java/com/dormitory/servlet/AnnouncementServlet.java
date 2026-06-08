package com.dormitory.servlet;

import com.dormitory.dao.AnnouncementDao;
import com.dormitory.entity.Announcement;
import com.dormitory.entity.User;
import com.dormitory.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AnnouncementServlet", urlPatterns = "/announcements")
public class AnnouncementServlet extends HttpServlet {

    private final AnnouncementDao announcementDao = new AnnouncementDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null) {
            WebUtil.redirectLogin(req, resp);
            return;
        }
        req.setAttribute("announcements", announcementDao.findAll());
        req.setAttribute("isAdmin", WebUtil.isAdmin(user));
        req.getRequestDispatcher("/announcements.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null || !WebUtil.isAdmin(user)) {
            WebUtil.redirectLogin(req, resp);
            return;
        }

        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String important = req.getParameter("important");

        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            req.setAttribute("errorMsg", "公告标题和内容不能为空");
            doGet(req, resp);
            return;
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(title.trim());
        announcement.setContent(content.trim());
        announcement.setPublisher(user.getRealName() != null ? user.getRealName() : user.getUsername());
        announcement.setIsImportant("on".equals(important) || "1".equals(important) ? 1 : 0);
        announcementDao.insert(announcement);

        resp.sendRedirect(req.getContextPath() + "/announcements");
    }
}
