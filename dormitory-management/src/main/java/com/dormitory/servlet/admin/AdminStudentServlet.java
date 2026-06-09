package com.dormitory.servlet.admin;

import com.dormitory.dao.admin.AdminStudentDao;
import com.dormitory.entity.User;
import com.dormitory.util.DbSchemaUtil;
import com.dormitory.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminStudentServlet", urlPatterns = "/admin/students")
public class AdminStudentServlet extends HttpServlet {

    private final AdminStudentDao studentDao = new AdminStudentDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AdminServletSupport.requireAdmin(req, resp)) return;
        prepareStudentsPage(req);
        req.getRequestDispatcher("/admin/students.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AdminServletSupport.requireAdmin(req, resp)) return;

        String action = req.getParameter("action");
        if ("add".equals(action)) {
            handleAdd(req, resp);
        } else if ("update".equals(action)) {
            handleUpdate(req, resp);
        } else if ("delete".equals(action)) {
            handleDelete(req, resp);
        } else {
            AdminServletSupport.flash(req, "未知操作");
            resp.sendRedirect(req.getContextPath() + "/admin/students");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String phone = trim(req.getParameter("phone"));
        String password = trim(req.getParameter("password"));
        String realName = trim(req.getParameter("realName"));
        String dormNo = trim(req.getParameter("dormNo"));
        if (!DbSchemaUtil.isUserPhoneColumnAvailable()) {
            forwardAddForm(req, resp,
                    "数据库缺少 phone 字段，请先执行 sql/update_v3_phone.sql 并重启服务",
                    username, phone, password, realName, dormNo);
            return;
        }
        if (isEmpty(username) || isEmpty(phone) || isEmpty(password) || isEmpty(realName)) {
            forwardAddForm(req, resp, "学号、手机号、密码、姓名不能为空",
                    username, phone, password, realName, dormNo);
            return;
        }
        if (!ValidationUtil.isStudentId(username)) {
            forwardAddForm(req, resp, ValidationUtil.STUDENT_ID_MESSAGE,
                    username, phone, password, realName, dormNo);
            return;
        }
        if (!ValidationUtil.isPhone(phone)) {
            forwardAddForm(req, resp, ValidationUtil.PHONE_MESSAGE,
                    username, phone, password, realName, dormNo);
            return;
        }
        if (!ValidationUtil.isRealName(realName)) {
            forwardAddForm(req, resp, ValidationUtil.REAL_NAME_MESSAGE,
                    username, phone, password, realName, dormNo);
            return;
        }
        if (username.equals(phone)) {
            forwardAddForm(req, resp, "学号与手机号不能相同",
                    username, phone, password, realName, dormNo);
            return;
        }
        if (studentDao.existsUsername(username, null)) {
            forwardAddForm(req, resp, "学号已存在",
                    username, phone, password, realName, dormNo);
            return;
        }
        if (studentDao.existsPhone(phone, null)) {
            forwardAddForm(req, resp, "手机号已被占用",
                    username, phone, password, realName, dormNo);
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(password);
        user.setRealName(realName);
        user.setDormNo(dormNo);
        if (studentDao.insertStudent(user)) {
            AdminServletSupport.flash(req, "学生添加成功");
            resp.sendRedirect(req.getContextPath() + "/admin/students");
        } else {
            forwardAddForm(req, resp, "添加失败",
                    username, phone, password, realName, dormNo);
        }
    }

    private void forwardAddForm(HttpServletRequest req, HttpServletResponse resp, String message,
                                String username, String phone, String password,
                                String realName, String dormNo)
            throws ServletException, IOException {
        AdminServletSupport.flash(req, message);
        req.setAttribute("hasAddDraft", Boolean.TRUE);
        req.setAttribute("addDraftUsername", username != null ? username : "");
        req.setAttribute("addDraftPhone", phone != null ? phone : "");
        req.setAttribute("addDraftPassword", password != null ? password : "");
        req.setAttribute("addDraftRealName", realName != null ? realName : "");
        req.setAttribute("addDraftDormNo", dormNo != null ? dormNo : "");
        prepareStudentsPage(req);
        req.getRequestDispatcher("/admin/students.jsp").forward(req, resp);
    }

    private void prepareStudentsPage(HttpServletRequest req) {
        req.setAttribute("studentList", studentDao.findAllStudents());
        req.setAttribute("phoneColumnAvailable", DbSchemaUtil.isUserPhoneColumnAvailable());
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String username = trim(req.getParameter("username"));
            String phone = trim(req.getParameter("phone"));
            String realName = trim(req.getParameter("realName"));
            String dormNo = trim(req.getParameter("dormNo"));
            String password = trim(req.getParameter("password"));
            if (isEmpty(username) || isEmpty(phone) || isEmpty(realName)) {
                AdminServletSupport.flash(req, "学号、手机号、姓名不能为空");
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
            if (!ValidationUtil.isStudentId(username)) {
                AdminServletSupport.flash(req, ValidationUtil.STUDENT_ID_MESSAGE);
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
            if (!ValidationUtil.isPhone(phone)) {
                AdminServletSupport.flash(req, ValidationUtil.PHONE_MESSAGE);
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
            if (!ValidationUtil.isRealName(realName)) {
                AdminServletSupport.flash(req, ValidationUtil.REAL_NAME_MESSAGE);
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
            if (username.equals(phone)) {
                AdminServletSupport.flash(req, "学号与手机号不能相同");
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
            if (studentDao.existsUsername(username, id)) {
                AdminServletSupport.flash(req, "学号已被占用");
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
            if (studentDao.existsPhone(phone, id)) {
                AdminServletSupport.flash(req, "手机号已被占用");
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setPhone(phone);
            user.setRealName(realName);
            user.setDormNo(dormNo);
            user.setPassword(password);
            AdminServletSupport.flash(req, studentDao.updateStudent(user) ? "修改成功" : "修改失败");
        } catch (NumberFormatException e) {
            AdminServletSupport.flash(req, "参数错误");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/students");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            AdminServletSupport.flash(req, studentDao.deleteStudent(id) ? "删除成功" : "删除失败");
        } catch (NumberFormatException e) {
            AdminServletSupport.flash(req, "参数错误");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/students");
    }

    private String trim(String v) {
        return v == null ? null : v.trim();
    }

    private boolean isEmpty(String v) {
        return v == null || v.isEmpty();
    }
}
