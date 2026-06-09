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
    List<Leave> auditedLeaves = (List<Leave>) request.getAttribute("auditedLeaves");
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
    <%@ include file="/includes/favicon-link.jsp" %>
    <title>管理员首页 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
    <link rel="stylesheet" href="<%= ctx %>/css/admin-pages.css">
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
            <div class="admin-quick-nav">
                <a href="<%= ctx %>/admin/students" class="quick-nav-card"><strong>学生管理</strong><span>添加、修改、删除学生</span></a>
                <a href="<%= ctx %>/admin/attendance" class="quick-nav-card"><strong>考勤管理</strong><span>查看签到记录与筛选</span></a>
                <a href="<%= ctx %>/admin/leaves" class="quick-nav-card"><strong>请假审核</strong><span>通过或驳回请假申请</span></a>
                <a href="<%= ctx %>/admin/announcements" class="quick-nav-card"><strong>公告管理</strong><span>发布、修改、删除公告</span></a>
                <a href="<%= ctx %>/admin/stats" class="quick-nav-card"><strong>统计中心</strong><span>图表数据分析</span></a>
            </div>
            <!-- 待审批请假和已审批请假 -->
            <div class="card leave-tabs-card">
                <div class="tab-bar">
                    <button type="button" class="tab-btn active" data-tab="pending">
                        待审批请假
                        <% if (pendingLeave > 0) { %><span class="tab-badge"><%= pendingLeave %></span><% } %>
                    </button>
                    <button type="button" class="tab-btn" data-tab="audited">已审批请假</button>
                </div>
                <!-- id="tab-pending"：和按钮的 data-tab 对应，active：当前显示的标签 -->
                <div class="tab-panel active" id="tab-pending">
                    <div class="table-wrap">
                        <table>
                            <thead><tr><th>学生</th><th>联系电话</th><th>类型</th><th>时间段</th><th>原因</th><th>操作</th></tr></thead>
                            <tbody>
                            <% if (pendingLeaves == null || pendingLeaves.isEmpty()) { %>
                            <tr><td colspan="6">暂无待审批请假</td></tr>
                            <% } else {
                                for (Leave leave : pendingLeaves) {
                                    String typeName = LeaveUtil.getTypeName(leave.getLeaveType());
                            %>
                            <tr>
                                <td><%= leave.getRealName() %></td>
                                <td><%= leave.getPhone() != null ? leave.getPhone() : "-" %></td>
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
                <div class="tab-panel" id="tab-audited">
                    <div class="table-wrap">
                        <table>
                            <thead><tr><th>学生</th><th>联系电话</th><th>类型</th><th>时间段</th><th>原因</th><th>审批状态</th><th>审批意见</th><th>审批时间</th></tr></thead>
                            <tbody>
                            <% if (auditedLeaves == null || auditedLeaves.isEmpty()) { %>
                            <tr><td colspan="8">暂无已审批记录</td></tr>
                            <% } else {
                                for (Leave leave : auditedLeaves) {
                                    String typeName = LeaveUtil.getTypeName(leave.getLeaveType());
                                    String statusBadge = LeaveUtil.getStatusBadgeClass(leave.getStatus());
                                    String statusName = LeaveUtil.getStatusName(leave.getStatus());
                                    String auditComment = leave.getRejectReason() != null && !leave.getRejectReason().trim().isEmpty()
                                            ? leave.getRejectReason() : "-";
                                    String auditTimeText = leave.getAuditTime() != null
                                            ? sdf.format(leave.getAuditTime()) : "-";
                            %>
                            <tr>
                                <td><%= leave.getRealName() %></td>
                                <td><%= leave.getPhone() != null ? leave.getPhone() : "-" %></td>
                                <td><%= typeName %></td>
                                <td><%= sdf.format(leave.getStartTime()) %> ~ <%= sdf.format(leave.getEndTime()) %></td>
                                <td><%= leave.getReason() %></td>
                                <td><span class="badge <%= statusBadge %>"><%= statusName %></span></td>
                                <td><%= auditComment %></td>
                                <td><%= auditTimeText %></td>
                            </tr>
                            <%   }
                               } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<div class="modal-overlay" id="rejectModal" aria-hidden="true">
    <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="rejectModalTitle">
        <div class="modal-header">
            <h3 id="rejectModalTitle">填写拒绝原因</h3>
            <button type="button" class="modal-close" id="rejectCloseBtn" aria-label="关闭">&times;</button>
        </div>
        <div class="modal-body">
            <p id="rejectStudentHint" class="modal-hint">请说明拒绝理由，学生可在请假记录中查看。</p>
            <form action="<%= ctx %>/admin/home" method="post" id="rejectForm" novalidate>
                <input type="hidden" name="action" value="rejectLeave">
                <input type="hidden" name="leaveId" id="rejectLeaveId" value="">
                <div class="form-group">
                    <label>拒绝原因 <span class="required">*</span></label>
                    <textarea name="rejectReason" id="rejectReason" class="form-control" rows="4" placeholder="例如：请假时间与课程冲突"></textarea>
                </div>
                <div class="modal-actions">
                    <button type="button" class="btn btn-outline" id="rejectCancelBtn">取消</button>
                    <button type="submit" class="btn btn-primary">确认拒绝</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="<%= ctx %>/js/message.js"></script>
<script>
(function () {
    var modal = document.getElementById('rejectModal');
    var rejectForm = document.getElementById('rejectForm');
    var leaveIdInput = document.getElementById('rejectLeaveId');
    var reasonInput = document.getElementById('rejectReason');
    var hint = document.getElementById('rejectStudentHint');
    var cancelBtn = document.getElementById('rejectCancelBtn');
    var closeBtn = document.getElementById('rejectCloseBtn');

    function openModal(leaveId, studentName) {
        leaveIdInput.value = leaveId;
        reasonInput.value = '';
        hint.textContent = '学生「' + studentName + '」的请假将被拒绝，请填写原因。';
        modal.classList.add('show');
        modal.setAttribute('aria-hidden', 'false');
        document.body.style.overflow = 'hidden';
        setTimeout(function () { reasonInput.focus(); }, 50);
    }

    function closeModal() {
        modal.classList.remove('show');
        modal.setAttribute('aria-hidden', 'true');
        document.body.style.overflow = '';
    }

    document.querySelectorAll('.reject-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            openModal(btn.getAttribute('data-leave-id'), btn.getAttribute('data-student'));
        });
    });

    cancelBtn.addEventListener('click', closeModal);
    closeBtn.addEventListener('click', closeModal);

    rejectForm.addEventListener('submit', function (e) {
        var reason = (reasonInput.value || '').trim();
        if (!reason) {
            e.preventDefault();
            reasonInput.classList.add('is-invalid');
            if (window.Message) {
                Message.error('请填写拒绝原因');
            }
            reasonInput.focus();
            return;
        }
        reasonInput.classList.remove('is-invalid');
    });

    reasonInput.addEventListener('input', function () {
        if (reasonInput.classList.contains('is-invalid') && reasonInput.value.trim()) {
            reasonInput.classList.remove('is-invalid');
        }
    });

    modal.addEventListener('click', function (e) {
        if (e.target === modal) {
            closeModal();
        }
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && modal.classList.contains('show')) {
            closeModal();
        }
    });
})();

(function () {
    var tabBtns = document.querySelectorAll('.tab-btn');
    var tabPanels = document.querySelectorAll('.tab-panel');
    if (!tabBtns.length) return;

    tabBtns.forEach(function (btn) {
        btn.addEventListener('click', function () {
            var target = btn.getAttribute('data-tab');
            tabBtns.forEach(function (b) { b.classList.remove('active'); });
            tabPanels.forEach(function (p) { p.classList.remove('active'); });
            btn.classList.add('active');
            var panel = document.getElementById('tab-' + target);
            if (panel) panel.classList.add('active');
        });
    });
})();
</script>
</body>
</html>
