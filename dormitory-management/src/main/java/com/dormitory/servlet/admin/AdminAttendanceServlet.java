package com.dormitory.servlet.admin;

import com.dormitory.dao.admin.AdminAttendanceQueryDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        req.getRequestDispatcher("/admin/attendance.jsp").forward(req, resp);
    }
}
