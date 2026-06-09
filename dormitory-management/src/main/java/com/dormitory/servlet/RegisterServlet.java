package com.dormitory.servlet;

import com.dormitory.dao.UserDao;
import com.dormitory.entity.User;
import com.dormitory.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String studentNo = trim(req.getParameter("studentNo"));
        if (isEmpty(studentNo)) {
            studentNo = trim(req.getParameter("username"));
        }
        String phone = trim(req.getParameter("phone"));
        String realName = trim(req.getParameter("realName"));
        String password = trim(req.getParameter("password"));
        String confirmPassword = trim(req.getParameter("confirmPassword"));
        String dormNo = trim(req.getParameter("dormNo"));

        if (isEmpty(studentNo) || isEmpty(phone) || isEmpty(realName) || isEmpty(password)) {
            req.setAttribute("errorMsg", "请填写完整的注册信息");
            forward(req, resp);
            return;
        }
        if (!ValidationUtil.isStudentId(studentNo)) {
            req.setAttribute("errorMsg", ValidationUtil.STUDENT_ID_MESSAGE);
            forward(req, resp);
            return;
        }
        if (!ValidationUtil.isPhone(phone)) {
            req.setAttribute("errorMsg", ValidationUtil.PHONE_MESSAGE);
            forward(req, resp);
            return;
        }
        if (!ValidationUtil.isRealName(realName)) {
            req.setAttribute("errorMsg", ValidationUtil.REAL_NAME_MESSAGE);
            forward(req, resp);
            return;
        }
        if (studentNo.equals(phone)) {
            req.setAttribute("errorMsg", "学号与手机号不能相同");
            forward(req, resp);
            return;
        }
        if (!password.equals(confirmPassword)) {
            req.setAttribute("errorMsg", "两次输入的密码不一致");
            forward(req, resp);
            return;
        }
        if (password.length() < 6) {
            req.setAttribute("errorMsg", "密码长度不能少于6位");
            forward(req, resp);
            return;
        }
        if (userDao.existsByUsername(studentNo)) {
            req.setAttribute("errorMsg", "该学号已被注册");
            forward(req, resp);
            return;
        }
        if (userDao.existsByPhone(phone)) {
            req.setAttribute("errorMsg", "该手机号已被注册");
            forward(req, resp);
            return;
        }

        User user = new User();
        user.setUsername(studentNo);
        user.setPhone(phone);
        user.setPassword(password);
        user.setRealName(realName);
        user.setDormNo(dormNo);
        user.setRole("student");

        if (userDao.insert(user)) {
            req.setAttribute("successMsg", "注册成功，可使用学号或手机号登录");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } else {
            req.setAttribute("errorMsg", "注册失败，请稍后重试");
            forward(req, resp);
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
