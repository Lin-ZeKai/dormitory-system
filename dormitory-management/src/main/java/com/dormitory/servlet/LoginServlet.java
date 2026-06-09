package com.dormitory.servlet;

import com.dormitory.dao.UserDao;
import com.dormitory.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String login = req.getParameter("username");
        String password = req.getParameter("password");

        if (login == null || login.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMsg", "学号/手机号和密码不能为空");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userDao.findByLoginAndPassword(login.trim(), password.trim());
            if (user == null) {
                req.setAttribute("errorMsg", "学号/手机号或密码错误");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession();
            session.setAttribute("loginUser", user);

            if ("admin".equals(user.getRole())) {
                resp.sendRedirect(req.getContextPath() + "/admin/home");
            } else {
                resp.sendRedirect(req.getContextPath() + "/student/home");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("errorMsg", "登录失败：数据库连接异常或表结构未更新，请执行 sql/update_v3_phone.sql 后重试");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
