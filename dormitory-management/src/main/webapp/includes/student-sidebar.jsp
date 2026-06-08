<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.dormitory.entity.User" %>
<%
    String ctx = request.getContextPath();
    User loginUser = (User) session.getAttribute("loginUser");
    String active = (String) request.getAttribute("activeMenu");
    if (active == null) active = "";
%>
<aside class="sidebar">
    <div class="sidebar-brand">宿舍考勤系统</div>
    <nav class="sidebar-nav">
        <div class="nav-group-title">学生功能</div>
        <a href="<%= ctx %>/student/home" class="nav-item <%= "home".equals(active) ? "active" : "" %>">首页概览</a>
        <a href="<%= ctx %>/student/checkin" class="nav-item <%= "checkin".equals(active) ? "active" : "" %>">考勤签到</a>
        <a href="<%= ctx %>/student/leave" class="nav-item <%= "leave".equals(active) ? "active" : "" %>">请假申请</a>
        <a href="<%= ctx %>/student/records" class="nav-item <%= "records".equals(active) ? "active" : "" %>">考勤记录</a>
        <a href="<%= ctx %>/announcements" class="nav-item <%= "announce".equals(active) ? "active" : "" %>">系统公告</a>
    </nav>
</aside>
