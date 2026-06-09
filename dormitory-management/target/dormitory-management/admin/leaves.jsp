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
    request.setAttribute("activeMenu", "leaves");
    List<Leave> pendingLeaves = (List<Leave>) request.getAttribute("pendingLeaves");
    List<Leave> auditedLeaves = (List<Leave>) request.getAttribute("auditedLeaves");
    Integer pendingLeave = (Integer) request.getAttribute("pendingLeave");
    if (pendingLeave == null) pendingLeave = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String flashMsg = (String) session.getAttribute("flashMsg");
    if (flashMsg != null) session.removeAttribute("flashMsg");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>请假审核 - 宿舍考勤与管理系统</title>
    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
    <link rel="stylesheet" href="<%= ctx %>/css/admin-pages.css">
</head>
<body>
<div class="layout">
    <%@ include file="/includes/admin-sidebar.jsp" %>
    <div class="main-wrapper">
        <header class="topbar">
            <span class="topbar-title">请假审核</span>
            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>
        </header>
        <main class="main-content">
            <% if (flashMsg != null) { %><div class="alert alert-info alert-toast" id="flashToast"><%= flashMsg %></div><% } %>
            <div class="admin-page-header">
                <h2>请假申请审核</h2>
                <p>查看请假申请，执行通过或驳回操作</p>
            </div>
            <div class="data-panel leave-tabs-card" style="padding-top:8px;">
                <div class="tab-bar">
                    <button type="button" class="tab-btn active" data-tab="pending">
                        待审批
                        <% if (pendingLeave > 0) { %><span class="tab-badge"><%= pendingLeave %></span><% } %>
                    </button>
                    <button type="button" class="tab-btn" data-tab="audited">已审批</button>
                </div>
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
                                <td class="action-group">
                                    <form action="<%= ctx %>/admin/leaves" method="post" class="inline-form">
                                        <input type="hidden" name="action" value="approveLeave">
                                        <input type="hidden" name="leaveId" value="<%= leave.getId() %>">
                                        <button type="submit" class="btn btn-primary btn-sm">通过</button>
                                    </form>
                                    <button type="button" class="btn btn-outline btn-sm reject-btn"
                                            data-leave-id="<%= leave.getId() %>"
                                            data-student="<%= leave.getRealName() %>">驳回</button>
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
                            <thead><tr><th>学生</th><th>联系电话</th><th>类型</th><th>时间段</th><th>原因</th><th>状态</th><th>审批意见</th><th>审批时间</th></tr></thead>
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
                                    String auditTimeText = leave.getAuditTime() != null ? sdf.format(leave.getAuditTime()) : "-";
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
                <div class="reserved-area">数据展示扩展区（可接入请假趋势分析）</div>
            </div>
        </main>
    </div>
</div>

<div class="modal-overlay" id="rejectModal" aria-hidden="true">
    <div class="modal-box" role="dialog">
        <div class="modal-header">
            <h3>填写驳回原因</h3>
            <button type="button" class="modal-close" id="rejectCloseBtn">&times;</button>
        </div>
        <div class="modal-body">
            <p id="rejectStudentHint">请说明驳回理由，学生可在请假记录中查看。</p>
            <form action="<%= ctx %>/admin/leaves" method="post" id="rejectForm">
                <input type="hidden" name="action" value="rejectLeave">
                <input type="hidden" name="leaveId" id="rejectLeaveId">
                <div class="form-group">
                    <label>驳回原因 <span class="required">*</span></label>
                    <textarea name="rejectReason" id="rejectReason" class="form-control" rows="4" required></textarea>
                </div>
                <div class="modal-actions">
                    <button type="button" class="btn btn-outline" id="rejectCancelBtn">取消</button>
                    <button type="submit" class="btn btn-primary">确认驳回</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
(function () {
    var flash = document.getElementById('flashToast');
    if (flash) setTimeout(function () { flash.classList.add('hide'); setTimeout(function () { flash.remove(); }, 350); }, 2000);

    document.querySelectorAll('.tab-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var target = btn.getAttribute('data-tab');
            document.querySelectorAll('.tab-btn').forEach(function (b) { b.classList.remove('active'); });
            document.querySelectorAll('.tab-panel').forEach(function (p) { p.classList.remove('active'); });
            btn.classList.add('active');
            document.getElementById('tab-' + target).classList.add('active');
        });
    });

    var modal = document.getElementById('rejectModal');
    var leaveIdInput = document.getElementById('rejectLeaveId');
    var reasonInput = document.getElementById('rejectReason');
    var hint = document.getElementById('rejectStudentHint');
    function closeModal() { modal.classList.remove('show'); document.body.style.overflow = ''; }
    document.querySelectorAll('.reject-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            leaveIdInput.value = btn.getAttribute('data-leave-id');
            reasonInput.value = '';
            hint.textContent = '学生「' + btn.getAttribute('data-student') + '」的请假将被驳回，请填写原因。';
            modal.classList.add('show');
            document.body.style.overflow = 'hidden';
            reasonInput.focus();
        });
    });
    document.getElementById('rejectCancelBtn').addEventListener('click', closeModal);
    document.getElementById('rejectCloseBtn').addEventListener('click', closeModal);
    modal.addEventListener('click', function (e) { if (e.target === modal) closeModal(); });
})();
</script>
</body>
</html>
