<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null || !"admin".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("activeMenu", "students");
    List<User> studentList = (List<User>) request.getAttribute("studentList");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String flashMsg = (String) session.getAttribute("flashMsg");
    if (flashMsg != null) session.removeAttribute("flashMsg");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <%@ include file="/includes/favicon-link.jsp" %>
    <title>学生管理 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
    <link rel="stylesheet" href="<%= ctx %>/css/admin-pages.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/admin-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">学生管理</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <% if (flashMsg != null) { %><div class="alert alert-info alert-toast" id="flashToast"><%= flashMsg %></div><% } %>
            <div class="admin-page-header">
                <h2>学生信息管理</h2>
                <p>查看、添加、修改、删除学生账号</p>
            </div>
            <div class="data-panel">
                <div class="data-panel-title">添加学生</div>
                <form action="<%= ctx %>/admin/students" method="post" class="form-row" autocomplete="off">
                    <input type="hidden" name="action" value="add">
                    <input type="text" tabindex="-1" autocomplete="username" aria-hidden="true" style="position:absolute;left:-9999px;width:1px;height:1px;opacity:0;">
                    <input type="password" tabindex="-1" autocomplete="current-password" aria-hidden="true" style="position:absolute;left:-9999px;width:1px;height:1px;opacity:0;">
                    <div class="form-group">
                        <label>学号/手机号 <span class="required">*</span></label>
                        <input type="text" name="username" class="form-control" placeholder="14位学号或11位手机号" required maxlength="14" inputmode="numeric" autocomplete="off" autocapitalize="off" spellcheck="false" readonly onfocus="this.removeAttribute('readonly');">
                        <small class="form-hint">学校规定学号为14位数字，也可使用手机号作为登录账号</small>
                    </div>
                    <div class="form-group">
                        <label>密码 <span class="required">*</span></label>
                        <input type="password" name="password" class="form-control" required autocomplete="new-password" readonly onfocus="this.removeAttribute('readonly');">
                    </div>
                    <div class="form-group">
                        <label>真实姓名 <span class="required">*</span></label>
                        <input type="text" name="realName" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>宿舍号</label>
                        <input type="text" name="dormNo" class="form-control" placeholder="如：3号楼302">
                    </div>
                    <div class="form-group" style="display:flex;align-items:flex-end;">
                        <button type="submit" class="btn btn-primary btn-sm">添加学生</button>
                    </div>
                </form>
            </div>
            <div class="data-panel">
                <div class="data-panel-title">学生列表</div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>学号</th><th>姓名</th><th>宿舍号</th><th>注册时间</th><th>操作</th></tr></thead>
                        <tbody>
                        <% if (studentList == null || studentList.isEmpty()) { %>
                        <tr><td colspan="5">暂无学生数据</td></tr>
                        <% } else {
                            for (User s : studentList) { %>
                        <tr>
                            <td><%= s.getUsername() %></td>
                            <td><%= s.getRealName() %></td>
                            <td><%= s.getDormNo() != null ? s.getDormNo() : "-" %></td>
                            <td><%= s.getCreateTime() != null ? sdf.format(s.getCreateTime()) : "-" %></td>
                            <td class="action-group">
                                <button type="button" class="btn btn-outline btn-sm edit-student-btn"
                                        data-id="<%= s.getId() %>"
                                        data-username="<%= s.getUsername() %>"
                                        data-realname="<%= s.getRealName() %>"
                                        data-dormno="<%= s.getDormNo() != null ? s.getDormNo() : "" %>">修改</button>
                                <button type="button" class="btn btn-outline btn-sm btn-delete delete-student-btn"
                                        data-id="<%= s.getId() %>"
                                        data-name="<%= s.getRealName() %>">删除</button>
                            </td>
                        </tr>
                        <%   }
                           } %>
                        </tbody>
                    </table>
                </div>
                <!-- <div class="reserved-area">数据展示扩展区（可接入导出 Excel、批量导入等功能）</div> -->
            </div>
        </main>
    </div>
</div>

<div class="modal-overlay" id="editModal">
    <div class="modal-box">
        <div class="modal-header"><h3>修改学生</h3><button type="button" class="modal-close" id="editCloseBtn">&times;</button></div>
        <div class="modal-body">
            <form action="<%= ctx %>/admin/students" method="post" id="editForm" autocomplete="off">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" id="editId">
                <div class="form-group"><label>学号/手机号</label><input type="text" name="username" id="editUsername" class="form-control" placeholder="14位学号或11位手机号" required maxlength="14" inputmode="numeric" autocomplete="off" autocapitalize="off" spellcheck="false"><small class="form-hint">14位学号或11位手机号</small></div>
                <div class="form-group"><label>真实姓名</label><input type="text" name="realName" id="editRealName" class="form-control" required autocomplete="off"></div>
                <div class="form-group"><label>宿舍号</label><input type="text" name="dormNo" id="editDormNo" class="form-control" autocomplete="off"></div>
                <div class="form-group"><label>新密码（留空则不修改）</label><input type="password" name="password" id="editPassword" class="form-control" autocomplete="new-password"></div>
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
            <p id="deleteHint">确定删除该学生吗？</p>
            <form action="<%= ctx %>/admin/students" method="post" id="deleteForm">
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

<script src="<%= ctx %>/js/message.js"></script>
<script src="<%= ctx %>/js/validation.js"></script>
<script>
(function () {
    var flash = document.getElementById('flashToast');
    if (flash) setTimeout(function () { flash.classList.add('hide'); setTimeout(function () { flash.remove(); }, 350); }, 2000);

    var addForm = document.querySelector('form input[name="action"][value="add"]');
    if (addForm) {
        addForm = addForm.closest('form');
        DormValidation.bindUsernameForm(addForm, null, { live: false });
        addForm.addEventListener('submit', function () {
            addForm.querySelector('input[name="username"]').removeAttribute('readonly');
            addForm.querySelector('input[name="password"]').removeAttribute('readonly');
        });
    }
    DormValidation.bindUsernameForm(document.getElementById('editForm'), null, { live: false });

    function bindModal(modal, openFn, closeBtn, cancelBtn) {
        function close() { modal.classList.remove('show'); document.body.style.overflow = ''; }
        if (closeBtn) closeBtn.addEventListener('click', close);
        if (cancelBtn) cancelBtn.addEventListener('click', close);
        modal.addEventListener('click', function (e) { if (e.target === modal) close(); });
        return { open: openFn, close: close };
    }

    var editModal = document.getElementById('editModal');
    var edit = bindModal(editModal, null, document.getElementById('editCloseBtn'), document.getElementById('editCancelBtn'));
    document.querySelectorAll('.edit-student-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.getElementById('editId').value = btn.getAttribute('data-id');
            document.getElementById('editUsername').value = btn.getAttribute('data-username');
            document.getElementById('editRealName').value = btn.getAttribute('data-realname');
            document.getElementById('editDormNo').value = btn.getAttribute('data-dormno');
            DormValidation.clearFieldError(document.getElementById('editUsername'));
            editModal.classList.add('show');
            document.body.style.overflow = 'hidden';
        });
    });

    var deleteModal = document.getElementById('deleteModal');
    bindModal(deleteModal, null, null, document.getElementById('deleteCancelBtn'));
    document.querySelectorAll('.delete-student-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.getElementById('deleteId').value = btn.getAttribute('data-id');
            document.getElementById('deleteHint').textContent = '确定删除学生「' + btn.getAttribute('data-name') + '」吗？';
            deleteModal.classList.add('show');
            document.body.style.overflow = 'hidden';
        });
    });
})();
</script>
</body>
</html>
