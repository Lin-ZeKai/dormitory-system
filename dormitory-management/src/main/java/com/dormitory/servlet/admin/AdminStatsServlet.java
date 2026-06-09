package com.dormitory.servlet.admin;

import com.dormitory.dao.admin.AdminStatsDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminStatsServlet", urlPatterns = "/admin/stats")
public class AdminStatsServlet extends HttpServlet {

    private final AdminStatsDao statsDao = new AdminStatsDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AdminServletSupport.requireAdmin(req, resp)) return;

        int totalStudents = statsDao.countTotalStudents();
        int todayCheckedIn = statsDao.countTodayCheckedIn();
        int todayOnLeave = statsDao.countTodayOnLeave();
        int todayAbsent = statsDao.countTodayAbsent(totalStudents);
        int pendingLeave = statsDao.countPendingLeave();

        Map<String, Integer> trend = statsDao.last7DaysCheckinTrend();
        List<String> trendLabels = new ArrayList<String>(trend.keySet());
        List<Integer> trendValues = new ArrayList<Integer>(trend.values());
        List<Integer> leaveDist = statsDao.leaveStatusDistribution();

        req.setAttribute("totalStudents", totalStudents);
        req.setAttribute("todayCheckedIn", todayCheckedIn);
        req.setAttribute("todayOnLeave", todayOnLeave);
        req.setAttribute("todayAbsent", todayAbsent);
        req.setAttribute("pendingLeave", pendingLeave);
        req.setAttribute("trendLabels", trendLabels);
        req.setAttribute("trendValues", trendValues);
        req.setAttribute("leavePending", leaveDist.get(0));
        req.setAttribute("leaveApproved", leaveDist.get(1));
        req.setAttribute("leaveRejected", leaveDist.get(2));

        req.getRequestDispatcher("/admin/stats.jsp").forward(req, resp);
    }
}
