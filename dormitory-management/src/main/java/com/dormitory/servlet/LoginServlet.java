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

/**
 * 登录控制器（Controller 层）
 * <p>
 * 映射地址：/login
 * GET  → 转发到登录页面
 * POST → 验证用户名密码，成功后写入 Session 并跳转首页
 * </p>
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    /**
     * 处理 GET 请求：直接显示登录页面
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    /**
     * 处理 POST 请求：执行登录逻辑
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. 获取表单提交的用户名和密码
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 2. 非空校验
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMsg", "用户名和密码不能为空");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 3. 调用 DAO 查询数据库验证账号
        User user = userDao.findByUsernameAndPassword(username.trim(), password.trim());
        if (user == null) {
            req.setAttribute("errorMsg", "用户名或密码错误");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 4. 登录成功：将用户信息存入 Session，供 LoginFilter 和页面使用
        HttpSession session = req.getSession();
        session.setAttribute("loginUser", user);

        // 5. 按角色跳转到业务首页
        if ("admin".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/admin/home");
        } else {
            resp.sendRedirect(req.getContextPath() + "/student/home");
        }
    }
}
