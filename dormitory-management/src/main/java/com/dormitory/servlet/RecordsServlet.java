package com.dormitory.servlet;

import com.dormitory.dao.AttendanceDao;
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

@WebServlet(name = "RecordsServlet", urlPatterns = "/student/records")
public class RecordsServlet extends HttpServlet {

    private final AttendanceDao attendanceDao = new AttendanceDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null) {
            WebUtil.redirectLogin(req, resp);
            return;
        }

        String month = req.getParameter("month");
        if (month == null || month.trim().isEmpty()) {
            month = AttendanceDao.currentYearMonth();
        }
        String status = req.getParameter("status");
        if (status != null && status.trim().isEmpty()) {
            status = null;
        }

        List<Attendance> records = attendanceDao.findByUserAndMonth(user.getId(), month, status);
        req.setAttribute("records", records);
        req.setAttribute("queryMonth", month);
        req.setAttribute("queryStatus", status == null ? "" : status);
        req.getRequestDispatcher("/student/records.jsp").forward(req, resp);
    }
}
