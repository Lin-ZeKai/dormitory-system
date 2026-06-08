<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctx = request.getContextPath();
    String active = (String) request.getAttribute("activeMenu");
    if (active == null) active = "";
%>
<aside class="sidebar">
    <div class="sidebar-brand">宿舍考勤系统</div>
    <nav class="sidebar-nav">
        <div class="nav-group-title">管理功能</div>
        <a href="<%= ctx %>/admin/home" class="nav-item <%= "home".equals(active) ? "active" : "" %>">数据概览</a>
        <a href="<%= ctx %>/announcements" class="nav-item <%= "announce".equals(active) ? "active" : "" %>">公告管理</a>
    </nav>
</aside>
