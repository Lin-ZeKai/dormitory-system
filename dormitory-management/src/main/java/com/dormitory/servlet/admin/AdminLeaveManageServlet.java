package com.dormitory.servlet.admin;

import com.dormitory.dao.LeaveDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminLeaveManageServlet", urlPatterns = "/admin/leaves")
public class AdminLeaveManageServlet extends HttpServlet {

    private final LeaveDao leaveDao = new LeaveDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AdminServletSupport.requireAdmin(req, resp)) return;
        req.setAttribute("pendingLeaves", leaveDao.findPendingAll());
        req.setAttribute("auditedLeaves", leaveDao.findAuditedAll());
        req.setAttribute("pendingLeave", leaveDao.countPending());
        req.getRequestDispatcher("/admin/leaves.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AdminServletSupport.requireAdmin(req, resp)) return;

        String action = req.getParameter("action");
        try {
            int leaveId = Integer.parseInt(req.getParameter("leaveId"));
            if ("approveLeave".equals(action)) {
                AdminServletSupport.flash(req, leaveDao.approveLeave(leaveId) ? "审批通过" : "审批失败");
            } else if ("rejectLeave".equals(action)) {
                String rejectReason = req.getParameter("rejectReason");
                if (rejectReason == null || rejectReason.trim().isEmpty()) {
                    AdminServletSupport.flash(req, "请填写拒绝原因");
                } else if (leaveDao.rejectLeave(leaveId, rejectReason.trim())) {
                    AdminServletSupport.flash(req, "已拒绝并通知学生");
                } else {
                    AdminServletSupport.flash(req, "拒绝失败");
                }
            }
        } catch (NumberFormatException e) {
            AdminServletSupport.flash(req, "参数错误");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/leaves");
    }
}
