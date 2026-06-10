<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" isELIgnored="false" %>
<%@ page import="com.dormitory.entity.CheckinReminder" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    List<CheckinReminder> reminderBannerList = (List<CheckinReminder>) request.getAttribute("checkinReminders");
    if (reminderBannerList != null && !reminderBannerList.isEmpty()) {
        SimpleDateFormat reminderDateFmt = new SimpleDateFormat("yyyy-MM-dd");
%>
<div class="checkin-reminder-stack">
<% for (CheckinReminder reminder : reminderBannerList) { %>
    <div class="checkin-reminder-banner">
        <div class="checkin-reminder-icon">!</div>
        <div class="checkin-reminder-content">
            <div class="checkin-reminder-title">管理员签到提醒</div>
            <p><%= reminder.getMessage() %></p>
            <div class="checkin-reminder-meta">
                提醒日期 <%= reminder.getRemindDate() != null ? reminderDateFmt.format(reminder.getRemindDate()) : "" %>
                · 来自 <%= reminder.getAdminName() != null ? reminder.getAdminName() : "管理员" %>
            </div>
        </div>
        <div class="checkin-reminder-actions">
            <a href="<%= request.getContextPath() %>/student/checkin" class="btn btn-primary btn-sm">立即签到</a>
            <form action="<%= request.getContextPath() %>/student/checkin-reminder" method="post">
                <input type="hidden" name="action" value="dismiss">
                <input type="hidden" name="id" value="<%= reminder.getId() %>">
                <button type="submit" class="btn btn-outline btn-sm">我知道了</button>
            </form>
        </div>
    </div>
<% } %>
</div>
<% } %>
