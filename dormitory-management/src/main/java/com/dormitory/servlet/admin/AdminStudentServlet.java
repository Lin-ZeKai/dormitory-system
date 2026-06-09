package com.dormitory.servlet.admin;

import com.dormitory.dao.admin.AdminStudentDao;
import com.dormitory.entity.User;
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
        req.setAttribute("studentList", studentDao.findAllStudents());
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

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = trim(req.getParameter("username"));
        String phone = trim(req.getParameter("phone"));
        String password = trim(req.getParameter("password"));
        String realName = trim(req.getParameter("realName"));
        String dormNo = trim(req.getParameter("dormNo"));
        if (isEmpty(username) || isEmpty(phone) || isEmpty(password) || isEmpty(realName)) {
            AdminServletSupport.flash(req, "学号、手机号、密码、姓名不能为空");
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
        if (username.equals(phone)) {
            AdminServletSupport.flash(req, "学号与手机号不能相同");
            resp.sendRedirect(req.getContextPath() + "/admin/students");
            return;
        }
        if (studentDao.existsUsername(username, null)) {
            AdminServletSupport.flash(req, "学号已存在");
            resp.sendRedirect(req.getContextPath() + "/admin/students");
            return;
        }
        if (studentDao.existsPhone(phone, null)) {
            AdminServletSupport.flash(req, "手机号已被占用");
            resp.sendRedirect(req.getContextPath() + "/admin/students");
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(password);
        user.setRealName(realName);
        user.setDormNo(dormNo);
        AdminServletSupport.flash(req, studentDao.insertStudent(user) ? "学生添加成功" : "添加失败");
        resp.sendRedirect(req.getContextPath() + "/admin/students");
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
