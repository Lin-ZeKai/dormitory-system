<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Announcement" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null || !"admin".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("activeMenu", "announce");
    List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String flashMsg = (String) session.getAttribute("flashMsg");
    if (flashMsg != null) session.removeAttribute("flashMsg");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <%@ include file="/includes/favicon-link.jsp" %>
    <title>公告管理 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
    <link rel="stylesheet" href="<%= ctx %>/css/admin-pages.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/admin-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">公告管理</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <% if (flashMsg != null) { %><div class="alert alert-info alert-toast" id="flashToast"><%= flashMsg %></div><% } %>
            <div class="admin-page-header">
                <h2>系统公告管理</h2>
                <p>发布、修改、删除系统公告（完整 CRUD）</p>
            </div>
            <div class="data-panel">
                <div class="data-panel-title">发布公告</div>
                <form action="<%= ctx %>/admin/announcements" method="post">
                    <input type="hidden" name="action" value="create">
                    <div class="form-group">
                        <label>公告标题 <span class="required">*</span></label>
                        <input type="text" name="title" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>公告内容 <span class="required">*</span></label>
                        <textarea name="content" class="form-control" required></textarea>
                    </div>
                    <div class="form-group">
                        <label><input type="checkbox" name="important" value="1"> 标记为重要</label>
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm">发布</button>
                </form>
            </div>
            <div class="data-panel">
                <div class="data-panel-title">公告列表</div>
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
                        <div class="action-group">
                            <button type="button" class="btn btn-outline btn-sm edit-announce-btn"
                                    data-id="<%= a.getId() %>"
                                    data-title="<%= a.getTitle() %>"
                                    data-important="<%= a.getIsImportant() != null && a.getIsImportant() == 1 ? "1" : "0" %>">修改</button>
                            <button type="button" class="btn btn-outline btn-sm btn-delete delete-announce-btn"
                                    data-id="<%= a.getId() %>"
                                    data-title="<%= a.getTitle() %>">删除</button>
                        </div>
                    </div>
                    <div class="meta"><%= a.getPublisher() %> · <%= sdf.format(a.getCreateTime()) %></div>
                    <p><%= a.getContent() %></p>
                    <textarea class="announce-content-store" id="announce-content-<%= a.getId() %>" hidden><%= a.getContent() %></textarea>
                </div>
                <%   }
                   } %>
                <!-- <div class="reserved-area">数据展示扩展区（可接入公告阅读统计）</div> -->
            </div>
        </main>
    </div>
</div>

<div class="modal-overlay" id="editModal">
    <div class="modal-box">
        <div class="modal-header"><h3>修改公告</h3><button type="button" class="modal-close" id="editCloseBtn">&times;</button></div>
        <div class="modal-body">
            <form action="<%= ctx %>/admin/announcements" method="post" id="editForm">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" id="editId">
                <div class="form-group"><label>标题</label><input type="text" name="title" id="editTitle" class="form-control" required></div>
                <div class="form-group"><label>内容</label><textarea name="content" id="editContent" class="form-control" required></textarea></div>
                <div class="form-group"><label><input type="checkbox" name="important" id="editImportant" value="1"> 标记为重要</label></div>
                <div class="modal-actions">
                    <button type="button" class="btn btn-outline" id="editCancelBtn">取消</button>
                    <button type="submit" class="btn btn-primary">保存</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal-overlay" id="deleteModal">
    <div class="modal-box modal-confirm">
        <div class="modal-body">
            <div class="modal-icon-danger">!</div>
            <h3>确认删除</h3>
            <p id="deleteHint">确定删除该公告吗？</p>
            <form action="<%= ctx %>/admin/announcements" method="post" id="deleteForm">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" id="deleteId">
                <div class="modal-actions">
                    <button type="button" class="btn btn-outline" id="deleteCancelBtn">取消</button>
                    <button type="submit" class="btn btn-primary btn-delete-submit">确认删除</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
(function () {
    var flash = document.getElementById('flashToast');
    if (flash) setTimeout(function () { flash.classList.add('hide'); setTimeout(function () { flash.remove(); }, 350); }, 2000);

    var editModal = document.getElementById('editModal');
    function closeEdit() { editModal.classList.remove('show'); document.body.style.overflow = ''; }
    document.getElementById('editCloseBtn').addEventListener('click', closeEdit);
    document.getElementById('editCancelBtn').addEventListener('click', closeEdit);
    editModal.addEventListener('click', function (e) { if (e.target === editModal) closeEdit(); });

    document.querySelectorAll('.edit-announce-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var id = btn.getAttribute('data-id');
            document.getElementById('editId').value = id;
            document.getElementById('editTitle').value = btn.getAttribute('data-title');
            var store = document.getElementById('announce-content-' + id);
            document.getElementById('editContent').value = store ? store.value : '';
            document.getElementById('editImportant').checked = btn.getAttribute('data-important') === '1';
            editModal.classList.add('show');
            document.body.style.overflow = 'hidden';
        });
    });

    var deleteModal = document.getElementById('deleteModal');
    function closeDelete() { deleteModal.classList.remove('show'); document.body.style.overflow = ''; }
    document.getElementById('deleteCancelBtn').addEventListener('click', closeDelete);
    deleteModal.addEventListener('click', function (e) { if (e.target === deleteModal) closeDelete(); });
    document.querySelectorAll('.delete-announce-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.getElementById('deleteId').value = btn.getAttribute('data-id');
            document.getElementById('deleteHint').textContent = '确定删除公告「' + btn.getAttribute('data-title') + '」吗？';
            deleteModal.classList.add('show');
            document.body.style.overflow = 'hidden';
        });
    });
})();
</script>
</body>
</html>
