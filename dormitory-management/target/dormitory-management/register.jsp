<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <%@ include file="/includes/favicon-link.jsp" %>
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
            <label>学号/手机号 <span class="required">*</span></label>
            <input type="text" name="username" class="form-control" placeholder="14位学号或11位手机号" required maxlength="14" inputmode="numeric">
            <small class="form-hint">学校规定学号为14位数字，也可使用手机号</small>
        </div>
        <div class="form-group">
            <label>真实姓名 <span class="required">*</span></label>
            <input type="text" name="realName" class="form-control" required>
        </div>
        <div class="form-group">
            <label>密码 <span class="required">*</span></label>
            <div class="password-field">
                <input type="password" name="password" class="form-control" placeholder="至少6位" required>
                <%@ include file="/includes/password-eye-button.jsp" %>
            </div>
        </div>
        <div class="form-group">
            <label>确认密码 <span class="required">*</span></label>
            <div class="password-field">
                <input type="password" name="confirmPassword" class="form-control" required>
                <%@ include file="/includes/password-eye-button.jsp" %>
            </div>
        </div>
        <div class="form-group">
            <label>宿舍号</label>
            <input type="text" name="dormNo" class="form-control" placeholder="如：3号楼302">
        </div>
        <button type="submit" class="btn btn-primary btn-block">注 册</button>
    </form>
    <div class="auth-footer">已有账号？<a href="<%= ctx %>/login.jsp">返回登录</a></div>
</div>
<script src="<%= ctx %>/js/message.js"></script>
<script src="<%= ctx %>/js/validation.js"></script>
<script src="<%= ctx %>/js/password-toggle.js"></script>
<script>DormValidation.bindUsernameForm(document.querySelector('form[action$="/register"]'));</script>
</body>
</html>
