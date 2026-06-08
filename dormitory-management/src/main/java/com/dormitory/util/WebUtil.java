package com.dormitory.util;

import com.dormitory.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Web 层工具类
 */
public final class WebUtil {

    private WebUtil() {
    }

    public static User getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("loginUser");
    }

    public static boolean isAdmin(User user) {
        return user != null && "admin".equals(user.getRole());
    }

    public static void redirectLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    public static void redirectHome(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        if (isAdmin(user)) {
            resp.sendRedirect(req.getContextPath() + "/admin/home");
        } else {
            resp.sendRedirect(req.getContextPath() + "/student/home");
        }
    }
}
