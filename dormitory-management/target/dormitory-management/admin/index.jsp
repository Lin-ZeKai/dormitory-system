<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Leave" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null || !"admin".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("activeMenu", "home");
    List<Leave> pendingLeaves = (List<Leave>) request.getAttribute("pendingLeaves");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    int totalStudents = (Integer) request.getAttribute("totalStudents");
    int todayCheckedIn = (Integer) request.getAttribute("todayCheckedIn");
    int todayAbsent = (Integer) request.getAttribute("todayAbsent");
    int pendingLeave = (Integer) request.getAttribute("pendingLeave");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>管理员首页 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/admin-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">管理员首页</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <div class="page-header"><h2>管理数据概览</h2></div>
            <div class="stats-grid">
                <div class="stat-card"><div class="label">注册学生</div><div class="value"><%= totalStudents %></div></div>
                <div class="stat-card"><div class="label">今日已签到</div><div class="value"><%= todayCheckedIn %></div></div>
                <div class="stat-card"><div class="label">今日缺勤</div><div class="value"><%= todayAbsent %></div></div>
                <div class="stat-card"><div class="label">待审请假</div><div class="value"><%= pendingLeave %></div></div>
            </div>
            <div class="card">
                <div class="card-title">待审批请假</div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>学生</th><th>类型</th><th>时间段</th><th>原因</th><th>操作</th></tr></thead>
                        <tbody>
                        <% if (pendingLeaves == null || pendingLeaves.isEmpty()) { %>
                        <tr><td colspan="5">暂无待审批请假</td></tr>
                        <% } else {
                            for (Leave leave : pendingLeaves) {
                                String typeName = "late".equals(leave.getLeaveType()) ? "晚归" : ("overnight".equals(leave.getLeaveType()) ? "外宿" : "其他");
                        %>
                        <tr>
                            <td><%= leave.getRealName() %></td>
                            <td><%= typeName %></td>
                            <td><%= sdf.format(leave.getStartTime()) %> ~ <%= sdf.format(leave.getEndTime()) %></td>
                            <td><%= leave.getReason() %></td>
                            <td>
                                <form action="<%= ctx %>/admin/home" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="approveLeave">
                                    <input type="hidden" name="leaveId" value="<%= leave.getId() %>">
                                    <button type="submit" class="btn btn-primary btn-sm">通过</button>
                                </form>
                                <form action="<%= ctx %>/admin/home" method="post" style="display:inline;margin-left:4px;">
                                    <input type="hidden" name="action" value="rejectLeave">
                                    <input type="hidden" name="leaveId" value="<%= leave.getId() %>">
                                    <button type="submit" class="btn btn-outline btn-sm">拒绝</button>
                                </form>
                            </td>
                        </tr>
                        <%   }
                           } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    </div>
</div>
</body>
</html>
