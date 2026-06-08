<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Announcement" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    boolean isAdmin = Boolean.TRUE.equals(request.getAttribute("isAdmin"));
    request.setAttribute("activeMenu", "announce");
    List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>系统公告 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
</head>
<body>
<div class="layout">
    <% if (isAdmin) { %>
    <%@ include file="/includes/admin-sidebar.jsp" %>
    <% } else { %>
    <%@ include file="/includes/student-sidebar.jsp" %>
    <% } %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">系统公告</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <% if (request.getAttribute("errorMsg") != null) { %><div class="alert alert-warning"><%= request.getAttribute("errorMsg") %></div><% } %>
            <div class="page-header"><h2>公告通知</h2></div>
            <div class="card">
                <% if (announcements == null || announcements.isEmpty()) { %>
                <p class="tip">暂无公告</p>
                <% } else {
                    for (Announcement a : announcements) { %>
                <div class="announcement-item">
                    <h4><%= a.getTitle() %>
                        <% if (a.getIsImportant() != null && a.getIsImportant() == 1) { %>
                        <span class="badge badge-danger">重要</span>
                        <% } %>
                    </h4>
                    <div class="meta"><%= a.getPublisher() %> · <%= sdf.format(a.getCreateTime()) %></div>
                    <p><%= a.getContent() %></p>
                </div>
                <%   }
                   } %>
            </div>
            <% if (isAdmin) { %>
            <div class="card">
                <div class="card-title">发布公告</div>
                <form action="<%= ctx %>/announcements" method="post">
                    <div class="form-group">
                        <label>公告标题</label>
                        <input type="text" name="title" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>公告内容</label>
                        <textarea name="content" class="form-control" required></textarea>
                    </div>
                    <div class="form-group">
                        <label><input type="checkbox" name="important" value="1"> 标记为重要</label>
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm">发布</button>
                </form>
            </div>
            <% } %>
        </main>
    </div>
</div>
</body>
</html>
