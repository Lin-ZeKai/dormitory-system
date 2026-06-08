package com.dormitory.filter;

import com.dormitory.entity.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 登录拦截过滤器
 * <p>
 * 拦截未登录用户访问受保护页面：若 Session 中没有 loginUser，
 * 则重定向到登录页。登录页、注册页、静态资源等路径除外。
 * </p>
 */
public class LoginFilter implements Filter {

    /** 未登录时跳转的目标页面 */
    private static final String LOGIN_PAGE = "/login.jsp";

    /**
     * 白名单路径：这些 URL 不需要登录即可访问
     * - login.jsp / register.jsp：登录注册页面
     * - /login：登录 Servlet 处理地址
     * - /css/：静态样式文件
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/login.jsp",
            "/register.jsp",
            "/register",
            "/login",
            "/css/",
            "/prototype/"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 获取不含项目上下文路径的 URI，如 /index.jsp
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());

        // 白名单路径直接放行
        if (isExcludePath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 检查 Session 中是否已有登录用户（LoginServlet 登录成功后写入）
        HttpSession session = req.getSession(false);
        User loginUser = session == null ? null : (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 未登录，强制跳转到登录页
            resp.sendRedirect(contextPath + LOGIN_PAGE);
            return;
        }

        // 已登录，继续访问目标资源
        chain.doFilter(request, response);
    }

    /**
     * 判断当前请求路径是否在白名单中
     *
     * @param path 请求路径（不含 contextPath）
     * @return true 表示无需登录即可访问
     */
    private boolean isExcludePath(String path) {
        for (String exclude : EXCLUDE_PATHS) {
            if (path.equals(exclude) || path.startsWith(exclude)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
