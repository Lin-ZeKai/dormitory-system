package com.dormitory.servlet;

import com.dormitory.dao.AttendanceDao;
import com.dormitory.dao.LeaveDao;
import com.dormitory.dao.UserDao;
import com.dormitory.entity.Leave;
import com.dormitory.entity.User;
import com.dormitory.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminHomeServlet", urlPatterns = "/admin/home")
public class AdminHomeServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final LeaveDao leaveDao = new LeaveDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null) {
            WebUtil.redirectLogin(req, resp);
            return;
        }
        if (!WebUtil.isAdmin(user)) {
            resp.sendRedirect(req.getContextPath() + "/student/home");
            return;
        }

        int totalStudents = userDao.countByRole("student");
        int todayCheckedIn = attendanceDao.countTodayCheckedIn();
        int todayAbsent = attendanceDao.countTodayAbsentStudents(totalStudents);
        int pendingLeave = leaveDao.countPending();
        List<Leave> pendingLeaves = leaveDao.findPendingAll();

        req.setAttribute("totalStudents", totalStudents);
        req.setAttribute("todayCheckedIn", todayCheckedIn);
        req.setAttribute("todayAbsent", todayAbsent);
        req.setAttribute("pendingLeave", pendingLeave);
        req.setAttribute("pendingLeaves", pendingLeaves);

        req.getRequestDispatcher("/admin/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null || !WebUtil.isAdmin(user)) {
            WebUtil.redirectLogin(req, resp);
            return;
        }

        String action = req.getParameter("action");
        if ("approveLeave".equals(action)) {
            int leaveId = Integer.parseInt(req.getParameter("leaveId"));
            leaveDao.updateStatus(leaveId, "approved");
        } else if ("rejectLeave".equals(action)) {
            int leaveId = Integer.parseInt(req.getParameter("leaveId"));
            leaveDao.updateStatus(leaveId, "rejected");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/home");
    }
}
