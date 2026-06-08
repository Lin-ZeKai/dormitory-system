package com.dormitory.servlet;

import com.dormitory.dao.AttendanceDao;
import com.dormitory.entity.Attendance;
import com.dormitory.entity.User;
import com.dormitory.util.CheckinTimeUtil;
import com.dormitory.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet(name = "CheckinServlet", urlPatterns = "/student/checkin")
public class CheckinServlet extends HttpServlet {

    private final AttendanceDao attendanceDao = new AttendanceDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null) {
            WebUtil.redirectLogin(req, resp);
            return;
        }

        Attendance today = attendanceDao.findTodayByUserId(user.getId());
        Date now = new Date();
        boolean inWindow = CheckinTimeUtil.isWithinWindow(now);
        boolean checkedIn = today != null;

        req.setAttribute("todayAttendance", today);
        req.setAttribute("inCheckinWindow", inWindow);
        req.setAttribute("canCheckin", !checkedIn && inWindow);
        req.setAttribute("checkinWindowText", CheckinTimeUtil.getWindowText());
        req.getRequestDispatcher("/student/checkin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null) {
            WebUtil.redirectLogin(req, resp);
            return;
        }

        if (attendanceDao.findTodayByUserId(user.getId()) != null) {
            req.setAttribute("errorMsg", "今日已签到，请勿重复操作");
            doGet(req, resp);
            return;
        }

        Date now = new Date();
        if (!CheckinTimeUtil.isWithinWindow(now)) {
            req.setAttribute("errorMsg", "当前不在签到时段内（" + CheckinTimeUtil.getWindowText() + "），无法签到");
            doGet(req, resp);
            return;
        }

        String status = CheckinTimeUtil.resolveStatus(now);
        attendanceDao.checkIn(user.getId(), now, status);
        req.setAttribute("successMsg", "签到成功");
        doGet(req, resp);
    }
}
