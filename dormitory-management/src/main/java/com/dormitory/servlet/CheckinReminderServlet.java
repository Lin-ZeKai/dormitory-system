package com.dormitory.servlet;

import com.dormitory.dao.CheckinReminderDao;
import com.dormitory.entity.User;
import com.dormitory.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CheckinReminderServlet", urlPatterns = "/student/checkin-reminder")
public class CheckinReminderServlet extends HttpServlet {

    private final CheckinReminderDao reminderDao = new CheckinReminderDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null) {
            WebUtil.redirectLogin(req, resp);
            return;
        }

        if (!"dismiss".equals(req.getParameter("action"))) {
            resp.sendRedirect(req.getContextPath() + "/student/home");
            return;
        }

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            reminderDao.markRead(id, user.getId());
        } catch (NumberFormatException ignored) {
        }

        resp.sendRedirect(req.getContextPath() + "/student/home");
    }
}
