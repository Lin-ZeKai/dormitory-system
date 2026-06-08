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
        <a href="<%= sidebarCtx %>/announcements" class="nav-item <%= "announce".equals(activeMenu) ? "active" : "" %>">公告管理</a>
    </nav>
</aside>
