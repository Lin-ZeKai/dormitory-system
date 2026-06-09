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
        List<Leave> auditedLeaves = leaveDao.findAuditedAll();

        req.setAttribute("totalStudents", totalStudents);
        req.setAttribute("todayCheckedIn", todayCheckedIn);
        req.setAttribute("todayAbsent", todayAbsent);
        req.setAttribute("pendingLeave", pendingLeave);
        req.setAttribute("pendingLeaves", pendingLeaves);
        req.setAttribute("auditedLeaves", auditedLeaves);

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
        try {
            int leaveId = Integer.parseInt(req.getParameter("leaveId"));
            if ("approveLeave".equals(action)) {
                if (leaveDao.approveLeave(leaveId)) {
                    req.getSession().setAttribute("flashMsg", "审批通过");
                } else {
                    req.getSession().setAttribute("flashMsg", "审批失败，记录可能已处理");
                }
            } else if ("rejectLeave".equals(action)) {
                String rejectReason = req.getParameter("rejectReason");
                if (rejectReason == null || rejectReason.trim().isEmpty()) {
                    req.getSession().setAttribute("flashMsg", "请填写拒绝原因");
                } else if (leaveDao.rejectLeave(leaveId, rejectReason.trim())) {
                    req.getSession().setAttribute("flashMsg", "已拒绝并通知学生");
                } else {
                    req.getSession().setAttribute("flashMsg", "拒绝失败，记录可能已处理");
                }
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashMsg", "参数错误");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/home");
    }
}
