package com.dormitory.servlet;

import com.dormitory.dao.UserDao;
import com.dormitory.entity.User;

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
        String username = trim(req.getParameter("username"));
        String realName = trim(req.getParameter("realName"));
        String password = trim(req.getParameter("password"));
        String confirmPassword = trim(req.getParameter("confirmPassword"));
        String dormNo = trim(req.getParameter("dormNo"));

        if (isEmpty(username) || isEmpty(realName) || isEmpty(password)) {
            req.setAttribute("errorMsg", "请填写完整的注册信息");
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
        if (userDao.existsByUsername(username)) {
            req.setAttribute("errorMsg", "用户名已存在");
            forward(req, resp);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setDormNo(dormNo);
        user.setRole("student");

        if (userDao.insert(user)) {
            req.setAttribute("successMsg", "注册成功，请登录");
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
