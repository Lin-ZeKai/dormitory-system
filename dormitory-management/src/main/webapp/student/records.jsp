<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
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
    request.setAttribute("activeMenu", "records");
    List<Attendance> records = (List<Attendance>) request.getAttribute("records");
    String queryMonth = (String) request.getAttribute("queryMonth");
    String queryStatus = (String) request.getAttribute("queryStatus");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>考勤记录 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/student-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">考勤记录</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <div class="page-header"><h2>我的考勤记录</h2></div>
            <div class="card">
                <form action="<%= ctx %>/student/records" method="get" style="display:flex;gap:12px;align-items:flex-end;margin-bottom:16px;">
                    <div class="form-group" style="margin-bottom:0;flex:1;">
                        <label>查询月份</label>
                        <input type="month" name="month" class="form-control" value="<%= queryMonth %>">
                    </div>
                    <div class="form-group" style="margin-bottom:0;flex:1;">
                        <label>考勤状态</label>
                        <select name="status" class="form-control">
                            <option value="" <%= "".equals(queryStatus) ? "selected" : "" %>>全部</option>
                            <option value="normal" <%= "normal".equals(queryStatus) ? "selected" : "" %>>正常</option>
                            <option value="late" <%= "late".equals(queryStatus) ? "selected" : "" %>>迟到</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary">查 询</button>
                </form>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>日期</th><th>签到时间</th><th>状态</th></tr></thead>
                        <tbody>
                        <% if (records == null || records.isEmpty()) { %>
                        <tr><td colspan="3">暂无记录</td></tr>
                        <% } else {
                            for (Attendance r : records) {
                                String badge = "late".equals(r.getStatus()) ? "badge-warning" : "badge-success";
                                String statusName = "late".equals(r.getStatus()) ? "迟到" : "正常";
                        %>
                        <tr>
                            <td><%= r.getCheckDate() %></td>
                            <td><%= sdf.format(r.getCheckTime()) %></td>
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
</body>
</html>
