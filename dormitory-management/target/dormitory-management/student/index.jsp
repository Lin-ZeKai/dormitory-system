<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Announcement" %>
<%@ page import="com.dormitory.entity.Attendance" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("activeMenu", "home");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Attendance todayAttendance = (Attendance) request.getAttribute("todayAttendance");
    List<Announcement> latestAnnouncements = (List<Announcement>) request.getAttribute("latestAnnouncements");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <%@ include file="/includes/favicon-link.jsp" %>
    <title>学生首页 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/student-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">学生首页</span>
            <div class="topbar-user">
                <span><%= loginUser.getRealName() %><%= loginUser.getDormNo() != null ? " · " + loginUser.getDormNo() : "" %></span>
                <div class="avatar"><%
                    String displayName = loginUser.getRealName() != null ? loginUser.getRealName() : loginUser.getUsername();
                    out.print(displayName.substring(0, 1));
                %></div>
                <a href="<%= ctx %>/logout">退出登录</a>
            </div>
        </header>
        <main class="main-content">
            <div class="page-header">
                <h2>欢迎回来，<%= loginUser.getRealName() %></h2>
                <% if (todayAttendance != null) { %>
                <p>今日签到已完成，继续保持良好考勤习惯</p>
                <% } else { %>
                <p>请及时完成今日宿舍考勤签到</p>
                <% } %>
            </div>
            <div class="stats-grid">
                <div class="stat-card"><div class="label">本月签到</div><div class="value"><%= request.getAttribute("monthlyCount") %></div></div>
                <div class="stat-card"><div class="label">迟到次数</div><div class="value"><%= request.getAttribute("lateCount") %></div></div>
                <div class="stat-card"><div class="label">待审请假</div><div class="value"><%= request.getAttribute("pendingLeave") %></div></div>
                <div class="stat-card"><div class="label">出勤率</div><div class="value"><%= request.getAttribute("attendanceRate") %>%</div></div>
            </div>
            <div class="card">
                <div class="card-title">今日考勤状态</div>
                <p>签到时段：<%= com.dormitory.util.CheckinTimeUtil.getWindowText() %></p>
                <p style="margin-top:8px;">当前状态：
                    <% if (todayAttendance != null) { %>
                    <span class="badge badge-success">已签到</span>（<%= sdf.format(todayAttendance.getCheckTime()) %>）
                    <% } else { %>
                    <span class="badge badge-warning">未签到</span>
                    <% } %>
                </p>
                <% if (todayAttendance != null) { %>
                <button type="button" class="btn btn-disabled" style="margin-top:16px;" disabled>今日已签到</button>
                <p class="tip" style="margin-top:8px;">无需重复签到，可在「考勤记录」中查看详情</p>
                <% } else { %>
                <a href="<%= ctx %>/student/checkin" class="btn btn-primary" style="margin-top:16px;">去签到</a>
                <% } %>
            </div>
            <div class="card">
                <div class="card-title">最新公告</div>
                <% if (latestAnnouncements != null && !latestAnnouncements.isEmpty()) {
                    for (Announcement a : latestAnnouncements) { %>
                <div class="announcement-item">
                    <h4><%= a.getTitle() %></h4>
                    <div class="meta"><%= sdf.format(a.getCreateTime()) %> · <%= a.getPublisher() %></div>
                    <p><%= a.getContent().length() > 60 ? a.getContent().substring(0, 60) + "..." : a.getContent() %></p>
                </div>
                <%   }
                   } else { %>
                <p class="tip">暂无公告</p>
                <% } %>
                <a href="<%= ctx %>/announcements" class="btn-link">查看全部公告 →</a>
            </div>
        </main>
    </div>
</div>
</body>
</html>
