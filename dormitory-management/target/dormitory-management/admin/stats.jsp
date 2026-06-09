<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="java.util.List" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null || !"admin".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("activeMenu", "stats");
    int totalStudents = (Integer) request.getAttribute("totalStudents");
    int todayCheckedIn = (Integer) request.getAttribute("todayCheckedIn");
    int todayOnLeave = (Integer) request.getAttribute("todayOnLeave");
    int todayAbsent = (Integer) request.getAttribute("todayAbsent");
    int pendingLeave = (Integer) request.getAttribute("pendingLeave");
    List<String> trendLabels = (List<String>) request.getAttribute("trendLabels");
    List<Integer> trendValues = (List<Integer>) request.getAttribute("trendValues");
    int leavePending = (Integer) request.getAttribute("leavePending");
    int leaveApproved = (Integer) request.getAttribute("leaveApproved");
    int leaveRejected = (Integer) request.getAttribute("leaveRejected");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <%@ include file="/includes/favicon-link.jsp" %>
    <title>统计中心 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
    <link rel="stylesheet" href="<%= ctx %>/css/admin-pages.css">
    <!-- 引入图表库，用于生成图表 -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
</head>
<body>
<div class="layout">
    <%@ include file="/includes/admin-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">统计中心</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <div class="admin-page-header">
                <h2>数据统计中心</h2>
                <p>今日考勤与请假数据概览，图表可视化展示</p>
            </div>
            <div class="stats-grid">
                <div class="stat-card"><div class="label">注册学生</div><div class="value"><%= totalStudents %></div></div>
                <div class="stat-card"><div class="label">今日签到</div><div class="value"><%= todayCheckedIn %></div></div>
                <div class="stat-card"><div class="label">今日请假</div><div class="value"><%= todayOnLeave %></div></div>
                <div class="stat-card"><div class="label">今日缺勤</div><div class="value"><%= todayAbsent %></div></div>
            </div>
            <div class="data-panel">
                <div class="data-panel-title">图表分析</div>
                <div class="chart-grid">
                    <div class="chart-box">
                        <h4>近 7 日签到人数趋势</h4>
                        // id 传给下面的 js 使用
                        <div class="chart-canvas-wrap"><canvas id="checkinTrendChart"></canvas></div>
                    </div>
                    <div class="chart-box">
                        <h4>请假状态分布</h4>
                        <div class="chart-canvas-wrap"><canvas id="leavePieChart"></canvas></div>
                    </div>
                </div>
                <!-- <div class="reserved-area">数据展示扩展区（待审请假 <%= pendingLeave %> 条 · 可接入月度报表、楼栋对比等）</div> -->
            </div>
        </main>
    </div>
</div>
<script>
(function () {
    // 创建签到趋势图表的标签和数据
    var trendLabels = [<% if (trendLabels != null) { for (int i = 0; i < trendLabels.size(); i++) { %>'<%= trendLabels.get(i) %>'<%= i < trendLabels.size() - 1 ? "," : "" %><% } } %>];
    // 创建签到趋势图表的数据
    var trendValues = [<% if (trendValues != null) { for (int i = 0; i < trendValues.size(); i++) { %><%= trendValues.get(i) %><%= i < trendValues.size() - 1 ? "," : "" %><% } } %>];

    // 创建签到趋势图表
    new Chart(document.getElementById('checkinTrendChart'), {
        // 类型为柱状图
        type: 'bar',
        data: {
            labels: trendLabels,
            datasets: [{
                label: '签到人数',
                data: trendValues,
                backgroundColor: 'rgba(37, 99, 235, 0.65)',
                borderColor: '#2563eb',
                borderWidth: 1,
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
        }
    });

    // 创建请假状态分布图表
    new Chart(document.getElementById('leavePieChart'), {
        // 类型为饼图
        type: 'doughnut',
        data: {
            labels: ['待审批', '已通过', '已驳回'],
            datasets: [{
                data: [<%= leavePending %>, <%= leaveApproved %>, <%= leaveRejected %>],
                backgroundColor: ['#f59e0b', '#22c55e', '#ef4444']
            }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });
})();
</script>
</body>
</html>
