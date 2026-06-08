package com.dormitory.servlet;

import com.dormitory.dao.AnnouncementDao;
import com.dormitory.dao.AttendanceDao;
import com.dormitory.dao.LeaveDao;
import com.dormitory.entity.Announcement;
import com.dormitory.entity.Attendance;
import com.dormitory.entity.User;
import com.dormitory.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudentHomeServlet", urlPatterns = "/student/home")
public class StudentHomeServlet extends HttpServlet {

    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final LeaveDao leaveDao = new LeaveDao();
    private final AnnouncementDao announcementDao = new AnnouncementDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null) {
            WebUtil.redirectLogin(req, resp);
            return;
        }

        String yearMonth = AttendanceDao.currentYearMonth();
        int monthlyCount = attendanceDao.countByUserAndMonth(user.getId(), yearMonth);
        int lateCount = attendanceDao.countLateByUserAndMonth(user.getId(), yearMonth);
        int pendingLeave = leaveDao.countPendingByUser(user.getId());
        Attendance today = attendanceDao.findTodayByUserId(user.getId());
        List<Announcement> latestAnnouncements = announcementDao.findLatest(3);

        int attendanceRate = monthlyCount == 0 ? 0 : (int) Math.round((monthlyCount - lateCount) * 100.0 / monthlyCount);

        req.setAttribute("monthlyCount", monthlyCount);
        req.setAttribute("lateCount", lateCount);
        req.setAttribute("pendingLeave", pendingLeave);
        req.setAttribute("attendanceRate", attendanceRate);
        req.setAttribute("todayAttendance", today);
        req.setAttribute("latestAnnouncements", latestAnnouncements);

        req.getRequestDispatcher("/student/index.jsp").forward(req, resp);
    }
}
