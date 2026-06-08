<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String activeMenu = (String) request.getAttribute("activeMenu");
    if (activeMenu == null) activeMenu = "";
    String sidebarCtx = request.getContextPath();
%>
<aside class="sidebar">
    <div class="sidebar-brand">宿舍考勤系统</div>
    <nav class="sidebar-nav">
        <div class="nav-group-title">学生功能</div>
        <a href="<%= sidebarCtx %>/student/home" class="nav-item <%= "home".equals(activeMenu) ? "active" : "" %>">首页概览</a>
        <a href="<%= sidebarCtx %>/student/checkin" class="nav-item <%= "checkin".equals(activeMenu) ? "active" : "" %>">考勤签到</a>
        <a href="<%= sidebarCtx %>/student/leave" class="nav-item <%= "leave".equals(activeMenu) ? "active" : "" %>">请假申请</a>
        <a href="<%= sidebarCtx %>/student/records" class="nav-item <%= "records".equals(activeMenu) ? "active" : "" %>">考勤记录</a>
        <a href="<%= sidebarCtx %>/announcements" class="nav-item <%= "announce".equals(activeMenu) ? "active" : "" %>">系统公告</a>
    </nav>
</aside>
