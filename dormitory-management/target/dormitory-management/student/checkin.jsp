<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Attendance" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("activeMenu", "checkin");
    Attendance todayAttendance = (Attendance) request.getAttribute("todayAttendance");
    boolean checkedIn = todayAttendance != null;
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>考勤签到 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/student-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">考勤签到</span>
            <div class="topbar-user">
                <span><%= loginUser.getRealName() %></span>
                <a href="<%= ctx %>/logout">退出登录</a>
            </div>
        </header>
        <main class="main-content">
            <% if (request.getAttribute("successMsg") != null) { %>
            <div class="alert alert-info"><%= request.getAttribute("successMsg") %></div>
            <% } %>
            <% if (request.getAttribute("errorMsg") != null) { %>
            <div class="alert alert-warning"><%= request.getAttribute("errorMsg") %></div>
            <% } %>
            <div class="page-header">
                <h2>每日考勤签到</h2>
                <p>签到时段：8:20-20:00</p>
            </div>
            <div class="card">
                <div class="checkin-box">
                    <div class="checkin-date" id="checkinDate"></div>
                    <div class="checkin-time" id="checkinClock">--:--:--</div>
                    <div class="checkin-status <%= checkedIn ? "done" : "pending" %>" id="checkinStatus">
                        <%= checkedIn ? "今日已签到" : "今日未签到" %>
                    </div>
                    <% if (!checkedIn) { %>
                    <form action="<%= ctx %>/student/checkin" method="post" style="display:inline;">
                        <button type="submit" class="btn btn-primary" style="padding:12px 48px;font-size:16px;">立即签到</button>
                    </form>
                    <% } else { %>
                    <button class="btn btn-primary" disabled style="padding:12px 48px;font-size:16px;">已签到</button>
                    <% } %>
                </div>
            </div>
        </main>
    </div>
</div>
<script>
(function () {
    function tick() {
        var now = new Date();
        var h = String(now.getHours()).padStart(2, '0');
        var m = String(now.getMinutes()).padStart(2, '0');
        var s = String(now.getSeconds()).padStart(2, '0');
        document.getElementById('checkinClock').textContent = h + ':' + m + ':' + s;
        var week = ['日','一','二','三','四','五','六'];
        document.getElementById('checkinDate').textContent =
            now.getFullYear() + '年' + (now.getMonth() + 1) + '月' + now.getDate() + '日 星期' + week[now.getDay()];
    }
    tick();
    setInterval(tick, 1000);
})();
</script>
</body>
</html>
