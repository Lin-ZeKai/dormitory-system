<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String activeMenu = (String) request.getAttribute("activeMenu");
    if (activeMenu == null) activeMenu = "";
    String sidebarCtx = request.getContextPath();
%>
<aside class="sidebar">
    <div class="sidebar-brand">宿舍考勤系统</div>
    <nav class="sidebar-nav">
        <div class="nav-group-title">管理功能</div>
        <a href="<%= sidebarCtx %>/admin/home" class="nav-item <%= "home".equals(activeMenu) ? "active" : "" %>">数据概览</a>
        <a href="<%= sidebarCtx %>/admin/students" class="nav-item <%= "students".equals(activeMenu) ? "active" : "" %>">学生管理</a>
        <a href="<%= sidebarCtx %>/admin/attendance" class="nav-item <%= "attendance".equals(activeMenu) ? "active" : "" %>">考勤管理</a>
        <a href="<%= sidebarCtx %>/admin/leaves" class="nav-item <%= "leaves".equals(activeMenu) ? "active" : "" %>">请假审核</a>
        <a href="<%= sidebarCtx %>/admin/announcements" class="nav-item <%= "announce".equals(activeMenu) ? "active" : "" %>">公告管理</a>
        <a href="<%= sidebarCtx %>/admin/stats" class="nav-item <%= "stats".equals(activeMenu) ? "active" : "" %>">统计中心</a>
    </nav>
</aside>
