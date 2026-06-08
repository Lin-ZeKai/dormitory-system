/**
 * 静态原型通用脚本（演示用，后续逐步替换为后端接口）
 */

/** 获取项目上下文路径，如 /dormitory-management */
function getAppContext() {
    var path = window.location.pathname;
    var idx = path.indexOf('/prototype');
    if (idx > 0) {
        return path.substring(0, idx);
    }
    var parts = path.split('/').filter(function (p) { return p; });
    return parts.length > 0 ? '/' + parts[0] : '';
}

/** 绑定退出登录到后端 LogoutServlet */
function bindLogoutLinks() {
    var ctx = getAppContext();
    document.querySelectorAll('[data-action="logout"]').forEach(function (el) {
        el.href = ctx + '/logout';
    });
}

/** 页面加载完成后初始化 */
document.addEventListener('DOMContentLoaded', function () {
    bindLogoutLinks();
});

/** 阻止表单默认提交，提示尚未接入后端 */
function bindPrototypeForm(formId, successMsg) {
    var form = document.getElementById(formId);
    if (!form) return;
    form.addEventListener('submit', function (e) {
        e.preventDefault();
        alert(successMsg || '【原型提示】表单已填写，后续接入后端接口后可正常提交。');
    });
}

/** 签到按钮演示 */
function bindCheckinBtn(btnId) {
    var btn = document.getElementById(btnId);
    if (!btn) return;
    btn.addEventListener('click', function () {
        alert('【原型提示】签到成功！后续接入 POST /api/attendance/checkin 接口。');
        var status = document.getElementById('checkinStatus');
        if (status) {
            status.textContent = '今日已签到';
            status.className = 'checkin-status done';
        }
        btn.disabled = true;
        btn.textContent = '已签到';
    });
}

/** 实时时钟（签到页用） */
function startClock(clockId, dateId) {
    var clockEl = document.getElementById(clockId);
    var dateEl = document.getElementById(dateId);
    if (!clockEl) return;

    function tick() {
        var now = new Date();
        var h = String(now.getHours()).padStart(2, '0');
        var m = String(now.getMinutes()).padStart(2, '0');
        var s = String(now.getSeconds()).padStart(2, '0');
        clockEl.textContent = h + ':' + m + ':' + s;

        if (dateEl) {
            var week = ['日', '一', '二', '三', '四', '五', '六'];
            dateEl.textContent = now.getFullYear() + '年' + (now.getMonth() + 1) + '月' + now.getDate()
                + '日  星期' + week[now.getDay()];
        }
    }
    tick();
    setInterval(tick, 1000);
}
