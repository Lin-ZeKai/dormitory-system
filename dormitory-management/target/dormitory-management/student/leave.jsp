<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Leave" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("activeMenu", "leave");
    List<Leave> leaveList = (List<Leave>) request.getAttribute("leaveList");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>请假申请 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/student-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">请假申请</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <% if (request.getAttribute("successMsg") != null) { %><div class="alert alert-info"><%= request.getAttribute("successMsg") %></div><% } %>
            <% if (request.getAttribute("errorMsg") != null) { %><div class="alert alert-warning"><%= request.getAttribute("errorMsg") %></div><% } %>
            <div class="page-header"><h2>提交请假申请</h2><p>无法按时归寝时，请提前填写并提交</p></div>
            <div class="card">
                <div class="card-title">填写请假信息</div>
                <form action="<%= ctx %>/student/leave" method="post">
                    <div class="form-row">
                        <div class="form-group">
                            <label>请假类型 <span class="required">*</span></label>
                            <select name="leaveType" class="form-control" required>
                                <option value="">请选择</option>
                                <option value="late">晚归</option>
                                <option value="overnight">外宿</option>
                                <option value="sick">病假</option>
                                <option value="public">公假</option>
                                <option value="event">事假</option>
                                <option value="other">其他</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>联系电话 <span class="required">*</span></label>
                            <input type="text" name="phone" class="form-control" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>开始时间 <span class="required">*</span></label>
                            <input type="datetime-local" name="startTime" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label>结束时间 <span class="required">*</span></label>
                            <input type="datetime-local" name="endTime" class="form-control" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>请假原因 <span class="required">*</span></label>
                        <textarea name="reason" class="form-control" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">提交申请</button>
                </form>
            </div>
            <div class="card">
                <div class="card-title">我的请假记录</div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>类型</th><th>时间段</th><th>状态</th><th>提交时间</th></tr></thead>
                        <tbody>
                        <% if (leaveList == null || leaveList.isEmpty()) { %>
                        <tr><td colspan="4">暂无请假记录</td></tr>
                        <% } else {
                            for (Leave leave : leaveList) {
                                String typeName = "late".equals(leave.getLeaveType()) ? "晚归" : ("overnight".equals(leave.getLeaveType()) ? "外宿" : "其他");
                                String statusBadge = "pending".equals(leave.getStatus()) ? "badge-warning" : ("approved".equals(leave.getStatus()) ? "badge-success" : "badge-danger");
                                String statusName = "pending".equals(leave.getStatus()) ? "待审批" : ("approved".equals(leave.getStatus()) ? "已通过" : "已拒绝");
                        %>
                        <tr>
                            <td><%= typeName %></td>
                            <td><%= sdf.format(leave.getStartTime()) %> ~ <%= sdf.format(leave.getEndTime()) %></td>
                            <td><span class="badge <%= statusBadge %>"><%= statusName %></span></td>
                            <td><%= sdf.format(leave.getCreateTime()) %></td>
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
