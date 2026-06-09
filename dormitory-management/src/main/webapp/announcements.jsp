<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Announcement" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    boolean isAdmin = Boolean.TRUE.equals(request.getAttribute("isAdmin"));
    request.setAttribute("activeMenu", "announce");
    List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String flashMsg = (String) session.getAttribute("flashMsg");
    if (flashMsg != null) {
        session.removeAttribute("flashMsg");
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>系统公告 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
</head>
<body>
<div class="layout">
    <% if (isAdmin) { %>
    <%@ include file="/includes/admin-sidebar.jsp" %>
    <% } else { %>
    <%@ include file="/includes/student-sidebar.jsp" %>
    <% } %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">系统公告</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <% if (flashMsg != null) { %>
            <div class="alert alert-info alert-toast" id="flashToast"><%= flashMsg %></div>
            <% } %>
            <% if (request.getAttribute("errorMsg") != null) { %>
            <div class="alert alert-warning alert-toast" id="errorToast"><%= request.getAttribute("errorMsg") %></div>
            <% } %>
            <div class="page-header"><h2>公告通知</h2></div>
            <div class="card">
                <% if (announcements == null || announcements.isEmpty()) { %>
                <p class="tip">暂无公告</p>
                <% } else {
                    for (Announcement a : announcements) { %>
                <div class="announcement-item">
                    <div class="announcement-head">
                        <h4><%= a.getTitle() %>
                            <% if (a.getIsImportant() != null && a.getIsImportant() == 1) { %>
                            <span class="badge badge-danger">重要</span>
                            <% } %>
                        </h4>
                        <% if (isAdmin) { %>
                        <button type="button" class="btn btn-outline btn-sm btn-delete delete-announce-btn"
                                data-id="<%= a.getId() %>"
                                data-title="<%= a.getTitle() %>">删除</button>
                        <% } %>
                    </div>
                    <div class="meta"><%= a.getPublisher() %> · <%= sdf.format(a.getCreateTime()) %></div>
                    <p><%= a.getContent() %></p>
                </div>
                <%   }
                   } %>
            </div>
            <% if (isAdmin) { %>
            <div class="card">
                <div class="card-title">发布公告</div>
                <form action="<%= ctx %>/announcements" method="post">
                    <div class="form-group">
                        <label>公告标题</label>
                        <input type="text" name="title" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>公告内容</label>
                        <textarea name="content" class="form-control" required></textarea>
                    </div>
                    <div class="form-group">
                        <label><input type="checkbox" name="important" value="1"> 标记为重要</label>
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm">发布</button>
                </form>
            </div>
            <% } %>
        </main>
    </div>
</div>

<% if (isAdmin) { %>
<div class="modal-overlay" id="deleteModal" aria-hidden="true">
    <div class="modal-box modal-confirm" role="dialog" aria-modal="true" aria-labelledby="deleteModalTitle">
        <div class="modal-header" style="justify-content:flex-end;padding-bottom:0;">
            <button type="button" class="modal-close" id="deleteCloseBtn" aria-label="关闭">&times;</button>
        </div>
        <div class="modal-body">
            <div class="modal-icon-danger">!</div>
            <h3 id="deleteModalTitle">确认删除公告</h3>
            <p id="deleteModalHint">删除后不可恢复，请确认是否继续。</p>
            <form action="<%= ctx %>/announcements" method="post" id="deleteForm">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="announcementId" id="deleteAnnouncementId" value="">
                <div class="modal-actions">
                    <button type="button" class="btn btn-outline" id="deleteCancelBtn">取消</button>
                    <button type="submit" class="btn btn-primary btn-delete-submit">确认删除</button>
                </div>
            </form>
        </div>
    </div>
</div>
<% } %>

<script>
(function () {
    function autoHideToast(id) {
        var el = document.getElementById(id);
        if (!el) return;
        setTimeout(function () {
            el.classList.add('hide');
            setTimeout(function () {
                if (el.parentNode) {
                    el.parentNode.removeChild(el);
                }
            }, 350);
        }, 2000);
    }

    autoHideToast('flashToast');
    autoHideToast('errorToast');

    var modal = document.getElementById('deleteModal');
    if (!modal) return;

    var form = document.getElementById('deleteForm');
    var idInput = document.getElementById('deleteAnnouncementId');
    var hint = document.getElementById('deleteModalHint');
    var cancelBtn = document.getElementById('deleteCancelBtn');
    var closeBtn = document.getElementById('deleteCloseBtn');

    function openModal(id, title) {
        idInput.value = id;
        hint.textContent = '确定删除公告「' + title + '」吗？删除后不可恢复。';
        modal.classList.add('show');
        modal.setAttribute('aria-hidden', 'false');
        document.body.style.overflow = 'hidden';
    }

    function closeModal() {
        modal.classList.remove('show');
        modal.setAttribute('aria-hidden', 'true');
        document.body.style.overflow = '';
    }

    document.querySelectorAll('.delete-announce-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            openModal(btn.getAttribute('data-id'), btn.getAttribute('data-title'));
        });
    });

    cancelBtn.addEventListener('click', closeModal);
    closeBtn.addEventListener('click', closeModal);
    modal.addEventListener('click', function (e) {
        if (e.target === modal) closeModal();
    });
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && modal.classList.contains('show')) closeModal();
    });
})();
</script>
</body>
</html>
