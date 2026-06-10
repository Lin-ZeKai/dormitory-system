package com.dormitory.servlet.admin;



import com.dormitory.dao.CheckinReminderDao;

import com.dormitory.dao.admin.AdminAttendanceQueryDao;

import com.dormitory.entity.StudentAttendanceOverview;

import com.dormitory.entity.User;

import com.dormitory.util.CheckinTimeUtil;



import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.io.PrintWriter;

import java.text.SimpleDateFormat;

import java.util.Collections;

import java.util.Date;

import java.util.List;

import java.util.Set;



@WebServlet(name = "AdminAttendanceServlet", urlPatterns = "/admin/attendance")

public class AdminAttendanceServlet extends HttpServlet {



    private final AdminAttendanceQueryDao attendanceQueryDao = new AdminAttendanceQueryDao();

    private final CheckinReminderDao reminderDao = new CheckinReminderDao();



    @Override

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)

            throws ServletException, IOException {

        if (!AdminServletSupport.requireAdmin(req, resp)) return;

        preparePage(req);

        req.getRequestDispatcher("/admin/attendance.jsp").forward(req, resp);

    }



    @Override

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if (!AdminServletSupport.requireAdmin(req, resp)) return;



        User admin = (User) req.getSession().getAttribute("loginUser");

        String adminName = admin != null && admin.getRealName() != null ? admin.getRealName() : "管理员";

        String action = req.getParameter("action");

        String checkDate = normalizeDate(req.getParameter("checkDate"));

        boolean json = "json".equals(req.getParameter("format"));



        String message;

        boolean ok = false;

        try {

            if ("remindOne".equals(action)) {

                message = handleRemindOne(req, adminName, checkDate);

                ok = true;

            } else if ("remindAll".equals(action)) {

                message = handleRemindAll(req, adminName, checkDate);

                ok = true;

            } else {

                message = "未知操作";

            }

        } catch (NumberFormatException e) {

            message = "参数错误";

        } catch (RuntimeException e) {

            message = e.getMessage() != null ? e.getMessage() : "操作失败";

        }



        if (json) {

            writeJson(resp, ok, message);

            return;

        }



        AdminServletSupport.flash(req, message);

        resp.sendRedirect(req.getContextPath() + "/admin/attendance?checkDate=" + checkDate + "#statusPanel");

    }



    private String handleRemindOne(HttpServletRequest req, String adminName, String checkDate) {

        int userId = Integer.parseInt(req.getParameter("userId"));

        List<StudentAttendanceOverview> statusList = attendanceQueryDao.findStudentStatusOverview(checkDate);

        StudentAttendanceOverview target = null;

        for (StudentAttendanceOverview row : statusList) {

            if (row.getUserId() != null && row.getUserId() == userId) {

                target = row;

                break;

            }

        }

        if (target == null) {

            return "学生不存在";

        }

        if (target.isCheckedIn()) {

            return "该学生已签到，无需提醒";

        }

        reminderDao.upsert(userId, checkDate, buildMessage(checkDate), adminName);

        String name = target.getRealName() != null ? target.getRealName() : "学生";

        return "已向「" + name + "」发送签到提醒";

    }



    private String handleRemindAll(HttpServletRequest req, String adminName, String checkDate) {

        List<StudentAttendanceOverview> statusList = attendanceQueryDao.findStudentStatusOverview(checkDate);

        String message = buildMessage(checkDate);

        int count = 0;

        for (StudentAttendanceOverview row : statusList) {

            if (!row.isCheckedIn() && row.getUserId() != null) {

                reminderDao.upsert(row.getUserId(), checkDate, message, adminName);

                count++;

            }

        }

        if (count == 0) {

            return "当前没有未签到学生";

        }

        return "已向 " + count + " 名未签到学生发送提醒";

    }



    private void preparePage(HttpServletRequest req) {

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



        String overviewDate = normalizeDate(checkDate);

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

        Set<Integer> remindedUserIds;
        try {
            remindedUserIds = reminderDao.findRemindedUserIdsByDate(overviewDate);
        } catch (RuntimeException e) {
            remindedUserIds = Collections.emptySet();
        }
        req.setAttribute("remindedUserIds", remindedUserIds);
        int unremindedCount = 0;
        for (StudentAttendanceOverview row : statusList) {
            if (!row.isCheckedIn() && row.getUserId() != null && !remindedUserIds.contains(row.getUserId())) {
                unremindedCount++;
            }
        }
        req.setAttribute("unremindedCount", unremindedCount);
    }



    private String buildMessage(String checkDate) {

        return "管理员提醒您尽快完成 " + checkDate + " 的宿舍考勤签到，签到时段："

                + CheckinTimeUtil.getWindowText();

    }



    private String normalizeDate(String checkDate) {

        if (checkDate != null && !checkDate.trim().isEmpty()) {

            return checkDate.trim();

        }

        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    }



    private void writeJson(HttpServletResponse resp, boolean ok, String message) throws IOException {

        resp.setCharacterEncoding("UTF-8");

        resp.setContentType("application/json;charset=UTF-8");

        PrintWriter out = resp.getWriter();

        out.print("{\"ok\":");

        out.print(ok);

        out.print(",\"msg\":\"");

        out.print(escapeJson(message));

        out.print("\"}");

        out.flush();

    }



    private String escapeJson(String value) {

        if (value == null) {

            return "";

        }

        return value.replace("\\", "\\\\")

                .replace("\"", "\\\"")

                .replace("\r", "")

                .replace("\n", "\\n");

    }

}


