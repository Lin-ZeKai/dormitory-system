<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<%@ page import="com.dormitory.entity.User" %>

<%@ page import="com.dormitory.entity.Attendance" %>

<%@ page import="com.dormitory.util.CheckinTimeUtil" %>

<%
    User loginUser = (User) session.getAttribute("loginUser");

    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (request.getAttribute("inCheckinWindow") == null) {
        response.sendRedirect(request.getContextPath() + "/student/checkin");
        return;
    }

    String ctx = request.getContextPath();

    request.setAttribute("activeMenu", "checkin");

    Attendance todayAttendance = (Attendance) request.getAttribute("todayAttendance");

    boolean checkedIn = todayAttendance != null;

    Boolean inWindowObj = (Boolean) request.getAttribute("inCheckinWindow");

    Boolean canCheckinObj = (Boolean) request.getAttribute("canCheckin");

    boolean inWindow = inWindowObj != null && inWindowObj;

    boolean canCheckin = canCheckinObj != null && canCheckinObj;

    String windowText = (String) request.getAttribute("checkinWindowText");

    if (windowText == null) windowText = CheckinTimeUtil.getWindowText();

%>

<!DOCTYPE html>

<html lang="zh-CN">

<head>

    <meta charset="UTF-8">

    <%@ include file="/includes/favicon-link.jsp" %>

    <title>考勤签到 - 宿舍考勤与管理系统</title>

    <link rel="stylesheet" href="<%= ctx %>/css/common.css">

</head>

<body>

<div class="layout">

    <%@ include file="/includes/student-sidebar.jsp" %>

    <div class="main-wrapper">

        <header class="topbar">

            <span class="topbar-title">考勤签到</span>

            <div class="topbar-user">

                <span><%= loginUser.getRealName() %></span>

                <a href="<%= ctx %>/logout">退出登录</a>

            </div>

        </header>

        <main class="main-content">

            <%@ include file="/includes/checkin-reminder-banner.jsp" %>

            <% if (request.getAttribute("successMsg") != null) { %>

            <div class="alert alert-info"><%= request.getAttribute("successMsg") %></div>

            <% } %>

            <% if (request.getAttribute("errorMsg") != null) { %>

            <div class="alert alert-warning"><%= request.getAttribute("errorMsg") %></div>

            <% } %>

            <div class="page-header">

                <h2>每日考勤签到</h2>

                <p>请在规定时段内完成签到，签到时段：<%= windowText %></p>

            </div>

            <div class="card">

                <div class="checkin-box">

                    <div class="checkin-date" id="checkinDate"></div>

                    <div class="checkin-time" id="checkinClock">--:--:--</div>

                    <div class="checkin-status <%= checkedIn ? "done" : (inWindow ? "pending" : "closed") %>" id="checkinStatus">

                        <%= checkedIn ? "今日已签到" : (inWindow ? "签到时段内，可以签到" : "当前不在签到时段") %>

                    </div>

                    <% if (!checkedIn) { %>

                    <form id="checkinForm" action="<%= ctx %>/student/checkin" method="post" style="display:inline;">

                        <button type="submit" id="checkinBtn"

                                class="btn btn-primary <%= canCheckin ? "" : "btn-disabled" %>"

                                <%= canCheckin ? "" : "disabled" %>

                                style="padding:12px 48px;font-size:16px;">

                            <%= canCheckin ? "立即签到" : "不在签到时段" %>

                        </button>

                    </form>

                    <p class="tip" id="checkinTip" style="margin-top:16px;<%= canCheckin ? "display:none;" : "" %>">

                        签到开放时间为 <%= windowText %>，请在时段内再来签到

                    </p>

                    <% } else { %>

                    <button class="btn btn-primary btn-disabled" disabled style="padding:12px 48px;font-size:16px;">已签到</button>

                    <% } %>

                </div>

            </div>

        </main>

    </div>

</div>

<script>

(function () {

    var checkedIn = <%= checkedIn ? "true" : "false" %>;

    var startHour = <%= CheckinTimeUtil.getStartHour() %>;

    var startMinute = <%= CheckinTimeUtil.getStartMinute() %>;

    var endHour = <%= CheckinTimeUtil.getEndHour() %>;

    var endMinute = <%= CheckinTimeUtil.getEndMinute() %>;



    function isInWindow(date) {

        var m = date.getHours() * 60 + date.getMinutes();

        return m >= (startHour * 60 + startMinute) && m < (endHour * 60 + endMinute);

    }



    function tick() {

        var now = new Date();

        document.getElementById('checkinClock').textContent =

            String(now.getHours()).padStart(2, '0') + ':' +

            String(now.getMinutes()).padStart(2, '0') + ':' +

            String(now.getSeconds()).padStart(2, '0');

        var week = ['日','一','二','三','四','五','六'];

        document.getElementById('checkinDate').textContent =

            now.getFullYear() + '年' + (now.getMonth() + 1) + '月' + now.getDate() + '日 星期' + week[now.getDay()];



        if (checkedIn) return;



        var inWindow = isInWindow(now);

        var statusEl = document.getElementById('checkinStatus');

        var btn = document.getElementById('checkinBtn');

        var tip = document.getElementById('checkinTip');



        if (statusEl) {

            statusEl.textContent = inWindow ? '签到时段内，可以签到' : '当前不在签到时段';

            statusEl.className = 'checkin-status ' + (inWindow ? 'pending' : 'closed');

        }

        if (btn) {

            btn.disabled = !inWindow;

            btn.textContent = inWindow ? '立即签到' : '不在签到时段';

            btn.className = 'btn btn-primary' + (inWindow ? '' : ' btn-disabled');

        }

        if (tip) {

            tip.style.display = inWindow ? 'none' : 'block';

        }

    }

    tick();

    setInterval(tick, 1000);

})();

</script>

</body>

</html>


