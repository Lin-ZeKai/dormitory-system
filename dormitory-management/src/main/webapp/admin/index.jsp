<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.User" %>
<%@ page import="com.dormitory.entity.Leave" %>
<%@ page import="com.dormitory.util.LeaveUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null || !"admin".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("activeMenu", "home");
    List<Leave> pendingLeaves = (List<Leave>) request.getAttribute("pendingLeaves");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    int totalStudents = (Integer) request.getAttribute("totalStudents");
    int todayCheckedIn = (Integer) request.getAttribute("todayCheckedIn");
    int todayAbsent = (Integer) request.getAttribute("todayAbsent");
    int pendingLeave = (Integer) request.getAttribute("pendingLeave");
    String flashMsg = (String) session.getAttribute("flashMsg");
    if (flashMsg != null) {
        session.removeAttribute("flashMsg");
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>管理员首页 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/admin-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">管理员首页</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <% if (flashMsg != null) { %><div class="alert alert-info"><%= flashMsg %></div><% } %>
            <div class="page-header"><h2>管理数据概览</h2></div>
            <div class="stats-grid">
                <div class="stat-card"><div class="label">注册学生</div><div class="value"><%= totalStudents %></div></div>
                <div class="stat-card"><div class="label">今日已签到</div><div class="value"><%= todayCheckedIn %></div></div>
                <div class="stat-card"><div class="label">今日缺勤</div><div class="value"><%= todayAbsent %></div></div>
                <div class="stat-card"><div class="label">待审请假</div><div class="value"><%= pendingLeave %></div></div>
            </div>
            <div class="card">
                <div class="card-title">待审批请假</div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>学生</th><th>类型</th><th>时间段</th><th>原因</th><th>操作</th></tr></thead>
                        <tbody>
                        <% if (pendingLeaves == null || pendingLeaves.isEmpty()) { %>
                        <tr><td colspan="5">暂无待审批请假</td></tr>
                        <% } else {
                            for (Leave leave : pendingLeaves) {
                                String typeName = LeaveUtil.getTypeName(leave.getLeaveType());
                        %>
                        <tr>
                            <td><%= leave.getRealName() %></td>
                            <td><%= typeName %></td>
                            <td><%= sdf.format(leave.getStartTime()) %> ~ <%= sdf.format(leave.getEndTime()) %></td>
                            <td><%= leave.getReason() %></td>
                            <td>
                                <form action="<%= ctx %>/admin/home" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="approveLeave">
                                    <input type="hidden" name="leaveId" value="<%= leave.getId() %>">
                                    <button type="submit" class="btn btn-primary btn-sm">通过</button>
                                </form>
                                <button type="button" class="btn btn-outline btn-sm reject-btn"
                                        data-leave-id="<%= leave.getId() %>"
                                        data-student="<%= leave.getRealName() %>">拒绝</button>
                            </td>
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

<div class="modal-overlay" id="rejectModal">
    <div class="modal-box">
        <h3>填写拒绝原因</h3>
        <p id="rejectStudentHint">请说明拒绝理由，学生可在请假记录中查看。</p>
        <form action="<%= ctx %>/admin/home" method="post" id="rejectForm">
            <input type="hidden" name="action" value="rejectLeave">
            <input type="hidden" name="leaveId" id="rejectLeaveId" value="">
            <div class="form-group">
                <label>拒绝原因 <span class="required">*</span></label>
                <textarea name="rejectReason" id="rejectReason" class="form-control" required placeholder="例如：请假时间与课程冲突"></textarea>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn btn-outline" id="rejectCancelBtn">取消</button>
                <button type="submit" class="btn btn-primary">确认拒绝</button>
            </div>
        </form>
    </div>
</div>

<script>
(function () {
    var modal = document.getElementById('rejectModal');
    var form = document.getElementById('rejectForm');
    var leaveIdInput = document.getElementById('rejectLeaveId');
    var reasonInput = document.getElementById('rejectReason');
    var hint = document.getElementById('rejectStudentHint');
    var cancelBtn = document.getElementById('rejectCancelBtn');

    function openModal(leaveId, studentName) {
        leaveIdInput.value = leaveId;
        reasonInput.value = '';
        hint.textContent = '学生「' + studentName + '」的请假将被拒绝，请填写原因。';
        modal.classList.add('show');
        reasonInput.focus();
    }

    function closeModal() {
        modal.classList.remove('show');
    }

    document.querySelectorAll('.reject-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            openModal(btn.getAttribute('data-leave-id'), btn.getAttribute('data-student'));
        });
    });

    cancelBtn.addEventListener('click', closeModal);
    modal.addEventListener('click', function (e) {
        if (e.target === modal) {
            closeModal();
        }
    });
})();
</script>
</body>
</html>
