<%-- 按角色跳转到业务首页 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    if ("admin".equals(loginUser.getRole())) {
        response.sendRedirect(ctx + "/admin/home");
    } else {
        response.sendRedirect(ctx + "/student/home");
    }
%>
