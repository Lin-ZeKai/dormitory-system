<%-- 登录页面 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>登录 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
    <style>
        .password-field {
            position: relative;
            width: 100%;
        }
        .password-field input {
            width: 100%;
            padding-right: 42px;
        }
        .password-toggle {
            position: absolute;
            right: 6px;
            top: 50%;
            transform: translateY(-50%);
            width: 32px;
            height: 32px;
            padding: 0;
            border: none;
            background: transparent;
            cursor: pointer;
            color: #909399;
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 2;
        }
        .password-toggle:hover {
            color: #409eff;
        }
        .password-toggle svg {
            width: 18px;
            height: 18px;
            display: block;
            fill: currentColor;
        }
        .password-toggle .icon-hide {
            display: none;
        }
        .password-toggle.is-visible .icon-show {
            display: none;
        }
        .password-toggle.is-visible .icon-hide {
            display: block;
        }
    </style>
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
            <div class="password-field">
                <input type="password" id="password" name="password" placeholder="请输入密码" required>
                <button type="button" class="password-toggle" id="passwordToggle" aria-label="显示密码" title="显示密码">
                    <svg class="icon-show" viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                    </svg>
                    <svg class="icon-hide" viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"/>
                    </svg>
                </button>
            </div>
        </div>
        <button type="submit" class="btn">登 录</button>
    </form>
    <a class="btn-link" href="<%= ctx %>/register">还没有账号？去注册</a>
</div>
<script>
(function () {
    var input = document.getElementById('password');
    var toggle = document.getElementById('passwordToggle');
    if (!input || !toggle) return;

    toggle.addEventListener('click', function () {
        var show = input.type === 'password';
        input.type = show ? 'text' : 'password';
        toggle.classList.toggle('is-visible', show);
        toggle.setAttribute('aria-label', show ? '隐藏密码' : '显示密码');
        toggle.setAttribute('title', show ? '隐藏密码' : '显示密码');
    });
})();
</script>
</body>
</html>
