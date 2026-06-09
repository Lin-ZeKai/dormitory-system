<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <%@ include file="/includes/favicon-link.jsp" %>
    <title>注册 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
    <style>
        .auth-card input:-webkit-autofill,
        .auth-card input:-webkit-autofill:hover,
        .auth-card input:-webkit-autofill:focus {
            -webkit-box-shadow: 0 0 0 1000px #fff inset;
            box-shadow: 0 0 0 1000px #fff inset;
            -webkit-text-fill-color: var(--text);
        }
    </style>
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
    <form action="<%= ctx %>/register" method="post" novalidate autocomplete="off">
        <input type="text" tabindex="-1" autocomplete="username" aria-hidden="true" style="position:absolute;left:-9999px;width:1px;height:1px;opacity:0;">
        <input type="password" tabindex="-1" autocomplete="current-password" aria-hidden="true" style="position:absolute;left:-9999px;width:1px;height:1px;opacity:0;">
        <div class="form-group">
            <label>学号 <span class="required">*</span></label>
            <input type="text" name="studentNo" id="studentNo" class="form-control" placeholder="14位学号" maxlength="14" inputmode="numeric" autocomplete="off" autocapitalize="off" spellcheck="false" readonly onfocus="this.removeAttribute('readonly');">
            <small class="form-hint">学校规定学号为 14 位数字</small>
        </div>
        <div class="form-group">
            <label>手机号 <span class="required">*</span></label>
            <input type="text" name="phone" id="regPhone" class="form-control" placeholder="11位手机号" maxlength="11" inputmode="numeric" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
            <small class="form-hint">登录时可使用学号或手机号</small>
        </div>
        <div class="form-group">
            <label>真实姓名 <span class="required">*</span></label>
            <input type="text" name="realName" class="form-control" autocomplete="off">
        </div>
        <div class="form-group">
            <label>密码 <span class="required">*</span></label>
            <div class="password-field">
                <input type="password" name="password" id="regPassword" class="form-control" placeholder="至少6位" autocomplete="new-password" readonly onfocus="this.removeAttribute('readonly');">
                <%@ include file="/includes/password-eye-button.jsp" %>
            </div>
        </div>
        <div class="form-group">
            <label>确认密码 <span class="required">*</span></label>
            <div class="password-field">
                <input type="password" name="confirmPassword" id="regConfirmPassword" class="form-control" autocomplete="new-password" readonly onfocus="this.removeAttribute('readonly');">
                <%@ include file="/includes/password-eye-button.jsp" %>
            </div>
        </div>
        <div class="form-group">
            <label>宿舍号</label>
            <input type="text" name="dormNo" class="form-control" placeholder="如：3号楼302" autocomplete="off">
        </div>
        <button type="submit" class="btn btn-primary btn-block">注 册</button>
    </form>
    <div class="auth-footer">已有账号？<a href="<%= ctx %>/login.jsp">返回登录</a></div>
</div>
<script src="<%= ctx %>/js/message.js"></script>
<script src="<%= ctx %>/js/validation.js"></script>
<script src="<%= ctx %>/js/password-toggle.js"></script>
<script>
(function () {
    var form = document.querySelector('form[action$="/register"]');
    if (!form) return;
    DormValidation.bindRegisterForm(form, { live: false, studentField: 'studentNo' });
    form.addEventListener('submit', function () {
        ['studentNo', 'phone', 'password', 'confirmPassword'].forEach(function (name) {
            var el = form.querySelector('[name="' + name + '"]');
            if (el) el.removeAttribute('readonly');
        });
    });
})();
</script>
</body>
</html>
