<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>注册 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
</head>
<body class="auth-page">
<div class="auth-card">
    <div class="auth-logo">
        <h1>用户注册</h1>
        <p>创建学生账号</p>
    </div>
    <% if (request.getAttribute("errorMsg") != null) { %>
    <div class="alert alert-warning"><%= request.getAttribute("errorMsg") %></div>
    <% } %>
    <form action="<%= ctx %>/register" method="post">
        <div class="form-group">
            <label>用户名（学号） <span class="required">*</span></label>
            <input type="text" name="username" class="form-control" placeholder="作为登录账号" required>
        </div>
        <div class="form-group">
            <label>真实姓名 <span class="required">*</span></label>
            <input type="text" name="realName" class="form-control" required>
        </div>
        <div class="form-group">
            <label>密码 <span class="required">*</span></label>
            <input type="password" name="password" class="form-control" placeholder="至少6位" required>
        </div>
        <div class="form-group">
            <label>确认密码 <span class="required">*</span></label>
            <input type="password" name="confirmPassword" class="form-control" required>
        </div>
        <div class="form-group">
            <label>宿舍号</label>
            <input type="text" name="dormNo" class="form-control" placeholder="如：3号楼302">
        </div>
        <button type="submit" class="btn btn-primary btn-block">注 册</button>
    </form>
    <div class="auth-footer">已有账号？<a href="<%= ctx %>/login.jsp">返回登录</a></div>
</div>
</body>
</html>
