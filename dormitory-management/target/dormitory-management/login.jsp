<%-- 登录页面 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>登录 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<div class="container">
    <h2>宿舍考勤与管理系统</h2>
    <% if (request.getAttribute("successMsg") != null) { %>
    <div class="error-msg" style="color:#22c55e;"><%= request.getAttribute("successMsg") %></div>
    <% } %>
    <% if (request.getAttribute("errorMsg") != null) { %>
    <div class="error-msg"><%= request.getAttribute("errorMsg") %></div>
    <% } %>
    <form action="<%= ctx %>/login" method="post">
        <div class="form-group">
            <label for="username">用户名</label>
            <input type="text" id="username" name="username" placeholder="请输入用户名" required>
        </div>
        <div class="form-group">
            <label for="password">密码</label>
            <input type="password" id="password" name="password" placeholder="请输入密码" required>
        </div>
        <button type="submit" class="btn">登 录</button>
    </form>
    <a class="btn-link" href="<%= ctx %>/register">还没有账号？去注册</a>
</div>
</body>
</html>
