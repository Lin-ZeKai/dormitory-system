package com.dormitory.servlet.admin;

import com.dormitory.dao.admin.AdminAttendanceQueryDao;
import com.dormitory.entity.StudentAttendanceOverview;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "AdminAttendanceServlet", urlPatterns = "/admin/attendance")
public class AdminAttendanceServlet extends HttpServlet {

    private final AdminAttendanceQueryDao attendanceQueryDao = new AdminAttendanceQueryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AdminServletSupport.requireAdmin(req, resp)) return;

        String checkDate = req.getParameter("checkDate");
        String userIdStr = req.getParameter("userId");
        Integer userId = null;
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                userId = Integer.parseInt(userIdStr.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        req.setAttribute("studentOptions", attendanceQueryDao.findStudentsForFilter());
        req.setAttribute("attendanceList", attendanceQueryDao.findAll(checkDate, userId));
        req.setAttribute("filterDate", checkDate);
        req.setAttribute("filterUserId", userId);

        String overviewDate = (checkDate != null && !checkDate.trim().isEmpty())
                ? checkDate.trim()
                : new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        List<StudentAttendanceOverview> statusList =
                attendanceQueryDao.findStudentStatusOverview(overviewDate);
        req.setAttribute("statusOverviewList", statusList);
        req.setAttribute("overviewDate", overviewDate);

        int checkedCount = 0;
        for (StudentAttendanceOverview row : statusList) {
            if (row.isCheckedIn()) {
                checkedCount++;
            }
        }
        req.setAttribute("overviewCheckedCount", checkedCount);
        req.setAttribute("overviewUncheckedCount", statusList.size() - checkedCount);

        req.getRequestDispatcher("/admin/attendance.jsp").forward(req, resp);
    }
}
