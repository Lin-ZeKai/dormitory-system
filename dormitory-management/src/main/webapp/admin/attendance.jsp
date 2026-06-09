<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Attendance" %>
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
    String filterDate = (String) request.getAttribute("filterDate");
    Integer filterUserId = (Integer) request.getAttribute("filterUserId");
    SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
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
                <p>查看全部签到记录，支持按日期、学生筛选</p>
            </div>
            <div class="data-panel">
                <div class="data-panel-title">筛选条件</div>
                <form action="<%= ctx %>/admin/attendance" method="get" class="filter-bar">
                    <div class="form-group">
                        <label>签到日期</label>
                        <input type="date" name="checkDate" class="form-control" value="<%= filterDate != null ? filterDate : "" %>">
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
            <div class="data-panel">
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
                <div class="reserved-area">数据展示扩展区（可接入按楼栋统计、导出报表等功能）</div>
            </div>
        </main>
    </div>
</div>
</body>
</html>
