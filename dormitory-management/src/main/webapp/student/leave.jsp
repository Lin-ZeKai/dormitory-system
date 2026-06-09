<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<%@ page import="com.dormitory.entity.User" %>

<%@ page import="com.dormitory.entity.Leave" %>

<%@ page import="com.dormitory.util.LeaveUtil" %>

<%@ page import="java.util.List" %>

<%@ page import="java.text.SimpleDateFormat" %>

<%

    User loginUser = (User) session.getAttribute("loginUser");

    if (loginUser == null) {

        response.sendRedirect(request.getContextPath() + "/login.jsp");

        return;

    }

    String ctx = request.getContextPath();

    request.setAttribute("activeMenu", "leave");

    List<Leave> leaveList = (List<Leave>) request.getAttribute("leaveList");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

%>

<!DOCTYPE html>

<html lang="zh-CN">

<head>

    <meta charset="UTF-8">
    <%@ include file="/includes/favicon-link.jsp" %>

    <title>请假申请 - 宿舍考勤与管理系统</title>

    <link rel="stylesheet" href="<%= ctx %>/css/common.css">
    <link rel="stylesheet" href="<%= ctx %>/lib/flatpickr/flatpickr.min.css">
    <link rel="stylesheet" href="<%= ctx %>/css/flatpickr-theme.css">

</head>

<body>

<div class="layout">

    <%@ include file="/includes/student-sidebar.jsp" %>

    <div class="main-wrapper">

        <header class="topbar">

            <span class="topbar-title">请假申请</span>

            <div class="topbar-user"><span><%= loginUser.getRealName() %></span><a href="<%= ctx %>/logout">退出登录</a></div>

        </header>

        <main class="main-content">

            <% if (request.getAttribute("successMsg") != null) { %><div class="alert alert-info"><%= request.getAttribute("successMsg") %></div><% } %>

            <% if (request.getAttribute("errorMsg") != null) { %><div class="alert alert-warning"><%= request.getAttribute("errorMsg") %></div><% } %>

            <div class="page-header"><h2>提交请假申请</h2><p>无法按时归寝时，请提前填写并提交</p></div>

            <div class="card">

                <div class="card-title">填写请假信息</div>

                <form action="<%= ctx %>/student/leave" method="post" novalidate>

                    <div class="form-row">

                        <div class="form-group">

                            <label>请假类型 <span class="required">*</span></label>

                            <div class="form-select-wrap">
                            <select name="leaveType" class="form-control">

                                <option value="">请选择</option>

                                <option value="late">晚归</option>

                                <option value="overnight">外宿</option>

                                <option value="sick">病假</option>

                                <option value="public">公假</option>

                                <option value="event">事假</option>

                                <option value="other">其他</option>

                            </select>
                            <span class="select-arrow"></span>
                            </div>

                        </div>

                        <div class="form-group">

                            <label>联系电话 <span class="required">*</span></label>

                            <input type="text" name="phone" class="form-control" placeholder="11位手机号" maxlength="11" inputmode="numeric">

                        </div>

                    </div>

                    <div class="form-row">

                        <div class="form-group">

                            <label>开始时间 <span class="required">*</span></label>

                            <div class="datetime-field">
                                <input type="text" name="startTime" id="startTime" class="form-control" placeholder="请选择开始时间">
                                <svg class="datetime-icon" viewBox="0 0 24 24" aria-hidden="true"><path fill="currentColor" d="M19 4h-1V2h-2v2H8V2H6v2H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2V6a2 2 0 00-2-2zm0 16H5V10h14v10zM7 12h2v2H7v-2zm4 0h2v2h-2v-2zm4 0h2v2h-2v-2z"/></svg>
                            </div>

                        </div>

                        <div class="form-group">

                            <label>结束时间 <span class="required">*</span></label>

                            <div class="datetime-field">
                                <input type="text" name="endTime" id="endTime" class="form-control" placeholder="请选择结束时间">
                                <svg class="datetime-icon" viewBox="0 0 24 24" aria-hidden="true"><path fill="currentColor" d="M19 4h-1V2h-2v2H8V2H6v2H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2V6a2 2 0 00-2-2zm0 16H5V10h14v10zM7 12h2v2H7v-2zm4 0h2v2h-2v-2zm4 0h2v2h-2v-2z"/></svg>
                            </div>

                        </div>

                    </div>

                    <div class="form-group">

                        <label>请假原因 <span class="required">*</span></label>

                        <textarea name="reason" class="form-control"></textarea>

                    </div>

                    <button type="submit" class="btn btn-primary">提交申请</button>

                </form>

            </div>

            <div class="card">

                <div class="card-title">我的请假记录</div>

                <div class="table-wrap">

                    <table>

                        <thead><tr><th>类型</th><th>时间段</th><th>状态</th><th>审批意见</th><th>提交时间</th></tr></thead>

                        <tbody>

                        <% if (leaveList == null || leaveList.isEmpty()) { %>

                        <tr><td colspan="5">暂无请假记录</td></tr>

                        <% } else {

                            for (Leave leave : leaveList) {

                                String typeName = LeaveUtil.getTypeName(leave.getLeaveType());

                                String statusBadge = LeaveUtil.getStatusBadgeClass(leave.getStatus());

                                String statusName = LeaveUtil.getStatusName(leave.getStatus());

                                String auditComment = "-";

                                if (leave.getRejectReason() != null && !leave.getRejectReason().trim().isEmpty()) {

                                    auditComment = leave.getRejectReason();

                                } else if ("approved".equals(leave.getStatus())) {

                                    auditComment = "审批通过";

                                }

                        %>

                        <tr>

                            <td><%= typeName %></td>

                            <td><%= sdf.format(leave.getStartTime()) %> ~ <%= sdf.format(leave.getEndTime()) %></td>

                            <td><span class="badge <%= statusBadge %>"><%= statusName %></span></td>

                            <td><%= auditComment %></td>

                            <td><%= sdf.format(leave.getCreateTime()) %></td>

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

<script src="<%= ctx %>/lib/flatpickr/flatpickr.min.js"></script>
<script src="<%= ctx %>/lib/flatpickr/zh.js"></script>
<script src="<%= ctx %>/js/datetime-picker.js"></script>
<script src="<%= ctx %>/js/message.js"></script>
<script src="<%= ctx %>/js/validation.js"></script>
<script>
(function () {
    var form = document.querySelector('form[action$="/student/leave"]');
    if (!form) return;

    DateTimePicker.bindLeaveForm(form);
    DormValidation.setupPhoneInput(form.querySelector('input[name="phone"]'), { live: false });

    form.addEventListener('submit', function (e) {
        var leaveType = form.querySelector('[name="leaveType"]');
        if (!leaveType.value) {
            e.preventDefault();
            Message.warning('请选择请假类型');
            return;
        }
        var phoneInput = form.querySelector('input[name="phone"]');
        if (!DormValidation.validatePhoneField(phoneInput, true)) {
            e.preventDefault();
            phoneInput.focus();
            return;
        }
        if (!document.getElementById('startTime').value || !document.getElementById('endTime').value) {
            e.preventDefault();
            Message.warning('请选择开始和结束时间');
            return;
        }
        var reason = form.querySelector('[name="reason"]');
        if (!reason.value.trim()) {
            e.preventDefault();
            Message.warning('请填写请假原因');
        }
    });
})();
</script>

</body>

</html>

