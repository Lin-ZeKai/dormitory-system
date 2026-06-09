package com.dormitory.servlet.admin;

import com.dormitory.entity.User;
import com.dormitory.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理端 Servlet 公共校验（新增）
 */
public final class AdminServletSupport {

    private AdminServletSupport() {
    }

    public static boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = WebUtil.getLoginUser(req);
        if (user == null || !WebUtil.isAdmin(user)) {
            WebUtil.redirectLogin(req, resp);
            return false;
        }
        return true;
    }

    public static void flash(HttpServletRequest req, String msg) {
        req.getSession().setAttribute("flashMsg", msg);
    }
}
