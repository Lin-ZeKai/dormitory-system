package com.dormitory.servlet.admin;

import com.dormitory.dao.admin.AdminStudentDao;
import com.dormitory.entity.User;

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
        String password = trim(req.getParameter("password"));
        String realName = trim(req.getParameter("realName"));
        String dormNo = trim(req.getParameter("dormNo"));
        if (isEmpty(username) || isEmpty(password) || isEmpty(realName)) {
            AdminServletSupport.flash(req, "学号、密码、姓名不能为空");
            resp.sendRedirect(req.getContextPath() + "/admin/students");
            return;
        }
        if (studentDao.existsUsername(username, null)) {
            AdminServletSupport.flash(req, "学号已存在");
            resp.sendRedirect(req.getContextPath() + "/admin/students");
            return;
        }
        User user = new User();
        user.setUsername(username);
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
            String realName = trim(req.getParameter("realName"));
            String dormNo = trim(req.getParameter("dormNo"));
            String password = trim(req.getParameter("password"));
            if (isEmpty(username) || isEmpty(realName)) {
                AdminServletSupport.flash(req, "学号、姓名不能为空");
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
            if (studentDao.existsUsername(username, id)) {
                AdminServletSupport.flash(req, "学号已被占用");
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
            User user = new User();
            user.setId(id);
            user.setUsername(username);
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
