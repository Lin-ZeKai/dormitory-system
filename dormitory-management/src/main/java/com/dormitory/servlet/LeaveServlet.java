package com.dormitory.servlet;

import com.dormitory.dao.LeaveDao;
import com.dormitory.entity.Leave;
import com.dormitory.entity.User;
import com.dormitory.util.ValidationUtil;
import com.dormitory.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(name = "LeaveServlet", urlPatterns = "/student/leave")
public class LeaveServlet extends HttpServlet {

    private final LeaveDao leaveDao = new LeaveDao();
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null) {
            WebUtil.redirectLogin(req, resp);
            return;
        }
        req.setAttribute("leaveList", leaveDao.findByUserId(user.getId()));
        req.getRequestDispatcher("/student/leave.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null) {
            WebUtil.redirectLogin(req, resp);
            return;
        }

        String leaveType = req.getParameter("leaveType");
        String phone = req.getParameter("phone");
        String startTimeStr = req.getParameter("startTime");
        String endTimeStr = req.getParameter("endTime");
        String reason = req.getParameter("reason");
        String resubmitIdStr = req.getParameter("resubmitId");

        if (isEmpty(leaveType) || isEmpty(phone) || isEmpty(startTimeStr)
                || isEmpty(endTimeStr) || isEmpty(reason)) {
            req.setAttribute("errorMsg", "请完整填写请假信息");
            doGet(req, resp);
            return;
        }
        if (!ValidationUtil.isPhone(phone)) {
            req.setAttribute("errorMsg", ValidationUtil.PHONE_MESSAGE);
            doGet(req, resp);
            return;
        }

        try {
            Leave leave = buildLeave(user.getId(), leaveType, phone, startTimeStr, endTimeStr, reason);

            if (leave.getEndTime().before(leave.getStartTime())) {
                req.setAttribute("errorMsg", "结束时间不能早于开始时间");
                doGet(req, resp);
                return;
            }

            boolean success;
            if (!isEmpty(resubmitIdStr)) {
                success = leaveDao.resubmitLeave(Integer.parseInt(resubmitIdStr), user.getId(), leave);
                req.setAttribute("successMsg", success ? "已重新提交，等待审批" : "重新提交失败，请确认记录状态");
            } else {
                Leave pending = leaveDao.findPendingByUserId(user.getId());
                if (pending != null) {
                    success = leaveDao.updatePendingLeave(pending.getId(), leave);
                    req.setAttribute("successMsg", success ? "待审请假已更新" : "更新失败");
                } else {
                    Leave rejected = leaveDao.findLatestRejectedByUserId(user.getId());
                    if (rejected != null) {
                        success = leaveDao.resubmitLeave(rejected.getId(), user.getId(), leave);
                        req.setAttribute("successMsg", success ? "已重新提交，等待审批" : "重新提交失败");
                    } else {
                        success = leaveDao.insert(leave);
                        req.setAttribute("successMsg", success ? "请假申请已提交，等待审批" : "提交失败");
                    }
                }
            }
        } catch (ParseException e) {
            req.setAttribute("errorMsg", "时间格式不正确");
        } catch (NumberFormatException e) {
            req.setAttribute("errorMsg", "重新提交参数无效");
        }

        doGet(req, resp);
    }

    private Leave buildLeave(int userId, String leaveType, String phone, String startTimeStr,
                             String endTimeStr, String reason) throws ParseException {
        Leave leave = new Leave();
        leave.setUserId(userId);
        leave.setLeaveType(leaveType.trim());
        leave.setPhone(phone.trim());
        leave.setStartTime(dateTimeFormat.parse(startTimeStr));
        leave.setEndTime(dateTimeFormat.parse(endTimeStr));
        leave.setReason(reason.trim());
        return leave;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
