<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Attendance" %>
<%@ page import="com.dormitory.entity.StudentAttendanceOverview" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null || !"admin".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("activeMenu", "attendance");
    List<Attendance> attendanceList = (List<Attendance>) request.getAttribute("attendanceList");
    List<User> studentOptions = (List<User>) request.getAttribute("studentOptions");
    List<StudentAttendanceOverview> statusOverviewList = (List<StudentAttendanceOverview>) request.getAttribute("statusOverviewList");
    String filterDate = (String) request.getAttribute("filterDate");
    String overviewDate = (String) request.getAttribute("overviewDate");
    Integer filterUserId = (Integer) request.getAttribute("filterUserId");
    Integer checkedCount = (Integer) request.getAttribute("overviewCheckedCount");
    Integer uncheckedCount = (Integer) request.getAttribute("overviewUncheckedCount");
    if (checkedCount == null) checkedCount = 0;
    if (uncheckedCount == null) uncheckedCount = 0;
    SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <%@ include file="/includes/favicon-link.jsp" %>
    <title>考勤管理 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
    <link rel="stylesheet" href="<%= ctx %>/css/admin-pages.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/admin-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">考勤管理</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <div class="admin-page-header">
                <h2>签到记录管理</h2>
                <p>查看签到明细，或按日期查看全部学生的考勤状态</p>
            </div>
            <div class="data-panel">
                <div class="data-panel-title">筛选条件</div>
                <form action="<%= ctx %>/admin/attendance" method="get" class="filter-bar">
                    <div class="form-group">
                        <label>签到日期</label>
                        <input type="date" name="checkDate" class="form-control" value="<%= filterDate != null ? filterDate : (overviewDate != null ? overviewDate : "") %>">
                    </div>
                    <div class="form-group">
                        <label>学生</label>
                        <select name="userId" class="form-control">
                            <option value="">全部学生</option>
                            <% if (studentOptions != null) {
                                for (User u : studentOptions) { %>
                            <option value="<%= u.getId() %>" <%= filterUserId != null && filterUserId.equals(u.getId()) ? "selected" : "" %>>
                                <%= u.getRealName() %>（<%= u.getUsername() %>）
                            </option>
                            <%   }
                               } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-sm">查询</button>
                        <a href="<%= ctx %>/admin/attendance" class="btn btn-outline btn-sm" style="margin-left:6px;">重置</a>
                    </div>
                </form>
            </div>

            <div class="view-tabs">
                <button type="button" class="view-tab active" data-target="recordsPanel">签到记录</button>
                <button type="button" class="view-tab" data-target="statusPanel">学生考勤状态</button>
            </div>

            <div class="data-panel view-panel active" id="recordsPanel">
                <div class="data-panel-title">签到记录列表</div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>学生</th><th>签到日期</th><th>签到时间</th><th>状态</th></tr></thead>
                        <tbody>
                        <% if (attendanceList == null || attendanceList.isEmpty()) { %>
                        <tr><td colspan="4">暂无签到记录</td></tr>
                        <% } else {
                            for (Attendance a : attendanceList) {
                                String statusName = "normal".equals(a.getStatus()) ? "正常" : ("late".equals(a.getStatus()) ? "迟到" : a.getStatus());
                                String badge = "normal".equals(a.getStatus()) ? "badge-success" : "badge-warning";
                        %>
                        <tr>
                            <td><%= a.getRealName() != null ? a.getRealName() : "-" %></td>
                            <td><%= a.getCheckDate() != null ? dateFmt.format(a.getCheckDate()) : "-" %></td>
                            <td><%= a.getCheckTime() != null ? timeFmt.format(a.getCheckTime()) : "-" %></td>
                            <td><span class="badge <%= badge %>"><%= statusName %></span></td>
                        </tr>
                        <%   }
                           } %>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="data-panel view-panel" id="statusPanel">
                <div class="data-panel-title">学生考勤状态<% if (overviewDate != null) { %>（<%= overviewDate %>）<% } %></div>
                <div class="status-summary">
                    <div class="status-summary-item info"><strong><%= statusOverviewList != null ? statusOverviewList.size() : 0 %></strong>学生总数</div>
                    <div class="status-summary-item success"><strong><%= checkedCount %></strong>已签到</div>
                    <div class="status-summary-item danger"><strong><%= uncheckedCount %></strong>未签到</div>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>学号/手机号</th>
                            <th>姓名</th>
                            <th>宿舍号</th>
                            <th>签到时间</th>
                            <th>考勤状态</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% if (statusOverviewList == null || statusOverviewList.isEmpty()) { %>
                        <tr><td colspan="5">暂无学生数据</td></tr>
                        <% } else {
                            for (StudentAttendanceOverview row : statusOverviewList) {
                                String statusName;
                                String badge;
                                if (row.isCheckedIn()) {
                                    if ("late".equals(row.getStatus())) {
                                        statusName = "迟到";
                                        badge = "badge-warning";
                                    } else {
                                        statusName = "正常";
                                        badge = "badge-success";
                                    }
                                } else {
                                    statusName = "未签到";
                                    badge = "badge-danger";
                                }
                        %>
                        <tr>
                            <td><%= row.getUsername() != null ? row.getUsername() : "-" %></td>
                            <td><%= row.getRealName() != null ? row.getRealName() : "-" %></td>
                            <td><%= row.getDormNo() != null && !row.getDormNo().isEmpty() ? row.getDormNo() : "-" %></td>
                            <td><%= row.getCheckTime() != null ? timeFmt.format(row.getCheckTime()) : "-" %></td>
                            <td><span class="badge <%= badge %>"><%= statusName %></span></td>
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
<script>
(function () {
    var tabs = document.querySelectorAll('.view-tab');
    var panels = document.querySelectorAll('.view-panel');
    tabs.forEach(function (tab) {
        tab.addEventListener('click', function () {
            var targetId = tab.getAttribute('data-target');
            tabs.forEach(function (t) { t.classList.remove('active'); });
            panels.forEach(function (p) { p.classList.remove('active'); });
            tab.classList.add('active');
            var panel = document.getElementById(targetId);
            if (panel) panel.classList.add('active');
        });
    });
})();
</script>
</body>
</html>
