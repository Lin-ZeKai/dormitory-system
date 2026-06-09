<%-- 登录页面 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <%@ include file="/includes/favicon-link.jsp" %>
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
            <label for="username">学号/手机号</label>
            <input type="text" id="username" name="username" placeholder="14位学号或11位手机号" required>
        </div>
        <div class="form-group">
            <label for="password">密码</label>
            <div class="password-field">
                <input type="password" id="password" name="password" placeholder="请输入密码" required>
                <%@ include file="/includes/password-eye-button.jsp" %>
            </div>
        </div>
        <button type="submit" class="btn">登 录</button>
    </form>
    <a class="btn-link" href="<%= ctx %>/register">还没有账号？去注册</a>
</div>
<script src="<%= ctx %>/js/password-toggle.js"></script>
</body>
</html>
